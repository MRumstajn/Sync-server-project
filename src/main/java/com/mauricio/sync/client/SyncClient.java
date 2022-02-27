package com.mauricio.sync.client;

import com.mauricio.sync.packets.wrappers.AuthPacketWrapper;
import com.mauricio.sync.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SyncClient implements ISyncClient{
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String ip;
    private int port;
    private String username;
    private String password;
    private IPacketParser packetParser;

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public SyncClient(String ip, int port, String username, String password,
                      Class<? extends IPacketParser> packetParserClass)
            throws InstantiationException, IllegalAccessException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        packetParser = packetParserClass.newInstance();
    }

    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
        new Thread(new SyncClientMessageReceiver(this, clientSocket, packetParser)).start();

        // client authentication test
        System.out.println("Authenticating...");
        AuthPacketWrapper authPacket = (AuthPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("auth", packetParser.getPacketClass());
        authPacket.setUsername(username);
        authPacket.setPassword(password);
        authPacket.setStatus(false);
        sendPacket(authPacket);
    }

    @Override
    public void disconnect() throws IOException {
        clientSocket.close();
    }

    @Override
    public void sendPacket(IPacket packet) throws IOException {
        out.writeUTF(packet.stringify());
    }

    @Override
    public String getServerIP() {
        return ip;
    }

    @Override
    public int getServerPort() {
        return port;
    }
}
