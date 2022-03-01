package com.mauricio.sync.server;

import com.mauricio.sync.events.EventEmitter;
import com.mauricio.sync.packets.wrappers.*;
import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class SyncClientDeviceHandler implements Runnable{
    private ISyncServer server;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private IPacketParser packetParser;
    private int deviceID;

    public SyncClientDeviceHandler(ISyncServer server, Socket client, IPacketParser packetParser, int deviceID){
        this.server = server;
        this.client = client;
        this.packetParser = packetParser;
        this.deviceID = deviceID;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            while (!client.isClosed()){
                String rawPacket;
                try {
                    rawPacket = in.readUTF();
                } catch (IOException e){
                    break;
                }
                IPacket packet = packetParser.parse(rawPacket);
                String type = (String) packet.get("type");
                switch (type){
                    case "ping":
                        // reply
                        PingPacketWrapper pingPacket = (PingPacketWrapper)
                                PacketWrapperFactory.createPacketWrapper("ping", packetParser.getPacketClass());
                        sendPacket(pingPacket);
                        break;
                    case "disconnect":
                        client.close();
                        break;
                    case "auth":
                        AuthPacketWrapper authPacket = new AuthPacketWrapper(packet);
                        if (authPacket.validate()) {
                            authDevice(authPacket);
                        } else {
                            sendPacket(createErrorPacket("Invalid auth packet"));
                        }
                        break;
                    case "sync":
                        // client requesting file
                        SyncFilePacketWrapper requestPacket = new SyncFilePacketWrapper(packet);
                        if (requestPacket.validate()){
                            SyncClientDevice fileHost = server.getFileHost(requestPacket.getPath());
                            if (fileHost != null) {
                                // relay request to the client with the file
                                server.addRelayRoute(fileHost, server.getDeviceWithID(deviceID));
                                fileHost.getHandler().sendPacket(requestPacket);
                                server.syncStarted(requestPacket.getPath(), fileHost, server.getDeviceWithID(deviceID));
                            } else {
                                sendPacket(createErrorPacket("Requested file is not registered on the server"));
                            }
                        } else {
                            sendPacket(createErrorPacket("Invalid sync packet"));
                        }
                        break;
                    case "sync_data":
                        // client sending piece of file
                        SyncDataPacketWrapper dataPacket = new SyncDataPacketWrapper(packet);
                        if (dataPacket.validate()){
                            relayDataPacket(dataPacket);
                            if (dataPacket.getData().equals("eof")){
                                SyncClientDevice device = server.getDeviceWithID(deviceID);
                                server.syncCompleted(dataPacket.getPath(), device, server.getRelayRoute(device));
                                server.removeRelayRoute(device);
                            }
                        } else {
                            sendPacket(createErrorPacket("Invalid data packet"));
                        }
                        break;
                    case "add_files":
                        AddFilesPacketWrapper addFilesPacket = new AddFilesPacketWrapper(packet);
                        List<Map<String, Object>> fileObjList = addFilesPacket.getFiles();
                        SyncClientDevice device = server.getDeviceWithID(deviceID);
                        for (Map<String, Object> obj : fileObjList){
                            //device.addFile((String) obj.get("path"), (Boolean) obj.get("is_dir"));
                            server.addFile(device, (String) obj.get("path"), (Boolean) obj.get("is_dir"));
                        }
                        break;
                }
            }
            server.removeDevice(server.getDeviceWithID(deviceID));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendPacket(IPacket packet) throws IOException {
        out.writeUTF(packet.stringify());
    }

    private ErrorPacketWrapper createErrorPacket(String msg){
        ErrorPacketWrapper packet = (ErrorPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("error", packetParser.getPacketClass());
        packet.setErrorMsg(msg);
        return packet;
    }

    private void authDevice(AuthPacketWrapper authPacket) throws IOException {
        if (!authPacket.validate()) {
            sendPacket(createErrorPacket("Invalid auth packet"));
            return;
        }
        String username = authPacket.getUsername();
        String password = authPacket.getPassword();
        AuthPacketWrapper authResponsePacket = (AuthPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("auth", packetParser.getPacketClass());
        if (server.isPasswordValid(password)){
            authResponsePacket.setStatus(true);
            SyncClientDevice device = server.getDeviceWithID(deviceID);
            server.setDeviceName(device, username);
            server.setDeviceAuth(device);
        } else {
            authResponsePacket.setStatus(false);
        }
        sendPacket(authResponsePacket);
    }

    private void relayDataPacket(SyncDataPacketWrapper dataPacket) throws IOException {
        String base64 = dataPacket.getData();
        byte[] received = Base64.getDecoder().decode(base64);
        // packet payload sizes must match on the server and the client for correct file transport
        boolean ok = true;
        if (received.length > SyncServer.PACKET_PAYLOAD_SIZE){
            sendPacket(createErrorPacket("Data packet payload is too large, must be <= "
                    + SyncServer.PACKET_PAYLOAD_SIZE + " bytes"));
            ok = false;
        }
        // send to the client that requested the file
        SyncClientDevice requester = server.getRelayRoute(server.getDeviceWithID(deviceID));
        if (requester == null){
            return;
        }
        if (ok) {
            requester.getHandler().sendPacket(dataPacket);
        } else {
            requester.getHandler().sendPacket(createErrorPacket("Client containing the requested file"
                    + " has sent a packet that is too large"));
        }
    }
}
