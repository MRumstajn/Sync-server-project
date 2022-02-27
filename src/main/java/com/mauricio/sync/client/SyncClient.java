package com.mauricio.sync.client;

import com.mauricio.sync.packets.wrappers.DisconnectPacketWrapper;
import com.mauricio.sync.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.packets.wrappers.PingPacketWrapper;
import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;
import com.mauricio.sync.packets.JSONPacket;

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
    private IPacketParser packetParser;

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public SyncClient(String ip, int port, Class<? extends IPacketParser> packetParserClass)
            throws InstantiationException, IllegalAccessException {
        this.ip = ip;
        this.port = port;
        packetParser = packetParserClass.newInstance();
    }

    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
        Scanner scanner = new Scanner(System.in);
        new Thread(new SyncClientMessageReceiver(this, clientSocket, packetParser)).start();
        while (true){
            System.out.println("What would you like to do?");
            System.out.println("\ta.) ping");
            System.out.println("\tb.) exit");
            String option = scanner.nextLine();
            if (option.equals("a")){
                PingPacketWrapper pingPacket = (PingPacketWrapper)
                        PacketWrapperFactory.createPacketWrapper("ping", JSONPacket.class);
                pingPacket.setIsRequest(true);
                sendPacket(pingPacket);
            } else {
                DisconnectPacketWrapper disconnectPacket = new DisconnectPacketWrapper(new JSONPacket());
                sendPacket(disconnectPacket);
                break;
            }
        }
        disconnect();
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
