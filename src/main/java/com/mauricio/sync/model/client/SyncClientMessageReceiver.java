package com.mauricio.sync.model.client;

import com.mauricio.sync.model.packets.IPacket;
import com.mauricio.sync.model.packets.parsers.IPacketParser;
import com.mauricio.sync.model.packets.wrappers.*;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author Mauricio Rumštajn
 */
public class SyncClientMessageReceiver implements Runnable{
    private ISyncClient client;
    private Socket clientSocket;
    private IPacketParser packetParser;

    @SuppressWarnings("deprecation")
    public SyncClientMessageReceiver(ISyncClient client, Socket clientSocketSocket, IPacketParser packetParser) {
        this.client = client;
        this.clientSocket = clientSocketSocket;
        this.packetParser = packetParser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            while (!clientSocket.isClosed()){
                String rawPacket = in.readUTF();
                IPacket packet = packetParser.parse(rawPacket);
                String type = (String) packet.get("type");
                switch (type){
                    case "ping":
                        // received reply
                        System.out.println("Server responded to ping");
                        break;
                    case "disconnect":
                        // server is disconnecting client
                        client.disconnect();
                        break;
                    case "auth":
                        AuthPacketWrapper authResponsePacket = new AuthPacketWrapper(packet);
                        if (authResponsePacket.getStatus()){
                            System.out.println("Client authenticated");
                        } else {
                            System.out.println("Authentication failed");
                        }
                        client.setAuthenticated(authResponsePacket.getStatus());
                        break;
                    case "error":
                        ErrorPacketWrapper errorPacketWrapper = new ErrorPacketWrapper(packet);
                        System.out.println(errorPacketWrapper.getErrorMsg());
                        break;
                    case "sync":
                        SyncFilePacketWrapper syncPacket = new SyncFilePacketWrapper(packet);
                        if (syncPacket.isDir()){
                            if (client.doesDirExist(syncPacket.getPath())){
                                client.sendDir(syncPacket.getPath());
                            }
                        } else {
                            if (client.doesFileExist(syncPacket.getPath())){
                                client.sendFile(syncPacket.getPath(), true);
                            }
                        }
                        client.fileSyncStarted(syncPacket.getPath(), syncPacket.isDir());
                        break;
                    case "sync_data":
                        SyncDataPacketWrapper dataPacket = new SyncDataPacketWrapper(packet);
                        if (!dataPacket.getData().equals("eof")) {
                            byte[] buff = Base64.getDecoder().decode(dataPacket.getData());
                            client.writeBuffer(buff, dataPacket.getPath());
                        } else {
                            boolean isDir = client.getFile(dataPacket.getPath()).isDirectory();
                            client.fileSyncCompleted(dataPacket.getPath(), isDir);
                        }
                        break;
                    case "list_files":
                        ListFilesPacketWrapper listFilesPacket = new ListFilesPacketWrapper(packet);
                        List<Map<String, Object>> fileList = listFilesPacket.getFiles();
                        if (fileList != null) {
                            for (Map<String, Object> dataMap : listFilesPacket.getFiles()) {
                                String filename = (String) dataMap.get("path");
                                String host = (String) dataMap.get("host");
                                boolean isDir = (Boolean) dataMap.get("is_dir");
                                client.addFile(filename, host, isDir);
                                client.serverFileListed(filename, host, isDir);
                            }
                        }
                        break;
                    case "add_files":
                        AddFilesPacketWrapper addFilesPacket = new AddFilesPacketWrapper(packet);
                        List<Map<String, Object>> fileObjList = addFilesPacket.getFiles();
                        for (Map<String, Object> obj : fileObjList) {
                            String filename = (String) obj.get("path");
                            String host = (String) addFilesPacket.get("host");
                            boolean isDir = (Boolean) obj.get("is_dir");
                            client.addFile(filename, host, isDir);
                            client.serverFileListed(filename, host, isDir);
                        }
                        break;
                    case "remove_files":
                        RemoveFilesPacketWrapper removeFilesPacket = new RemoveFilesPacketWrapper(packet);
                        List<Map<String, Object>> fileObjList2 = removeFilesPacket.getFiles();
                        for (Map<String, Object> obj : fileObjList2) {
                            String filename = (String) obj.get("path");
                            String host = (String) removeFilesPacket.get("host");
                            boolean isDir = (Boolean) obj.get("is_dir");
                            client.removeFile(filename, host, isDir);
                            client.serverFileUnlisted(filename, host, isDir);
                        }
                        break;

                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
