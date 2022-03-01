package com.mauricio.sync.client;

import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;
import com.mauricio.sync.packets.wrappers.AuthPacketWrapper;
import com.mauricio.sync.packets.wrappers.ErrorPacketWrapper;
import com.mauricio.sync.packets.wrappers.SyncDataPacketWrapper;
import com.mauricio.sync.packets.wrappers.SyncFilePacketWrapper;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

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
                        break;
                    case "sync_data":
                        SyncDataPacketWrapper dataPacket = new SyncDataPacketWrapper(packet);
                        if (!dataPacket.getData().equals("eof")) {
                            byte[] buff = Base64.getDecoder().decode(dataPacket.getData());
                            client.writeBuffer(buff, dataPacket.getPath());
                        }
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
