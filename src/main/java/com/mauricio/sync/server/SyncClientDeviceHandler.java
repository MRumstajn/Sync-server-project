package com.mauricio.sync.server;

import com.mauricio.sync.packets.wrappers.ErrorPacketWrapper;
import com.mauricio.sync.packets.wrappers.AuthPacketWrapper;
import com.mauricio.sync.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.packets.wrappers.PingPacketWrapper;
import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                String rawPacket = in.readUTF();
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
                }
            }
            System.out.println("client " + client.getRemoteSocketAddress() + " disconnected");
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
            device.setName(username);
            server.setDeviceAuth(deviceID);
        } else {
            authResponsePacket.setStatus(false);
        }
        sendPacket(authResponsePacket);
    }
}
