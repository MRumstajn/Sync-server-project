package com.mauricio.sync.server;

import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SyncServer implements ISyncServer{
    private ServerSocket serverSocket;
    private int port;
    private List<SyncClientDevice> clients;
    private IPacketParser packetParser;

    @SuppressWarnings("deprecation")
    public SyncServer(int port, Class<? extends IPacketParser> packetParserClass) throws InstantiationException,
            IllegalAccessException {
        this.port = port;
        clients = new ArrayList<>();
        this.packetParser = packetParserClass.newInstance();
    }

    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()){
            Socket client = serverSocket.accept();
            System.out.println("New connection from " + client.getRemoteSocketAddress());
            SyncClientDeviceHandler handler = new SyncClientDeviceHandler(client, packetParser);
            new Thread(handler).start();
            SyncClientDevice device = new SyncClientDevice(0, client, handler);
            clients.add(device);
        }
    }

    @Override
    public void stop() throws IOException{
        serverSocket.close();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public List<SyncClientDevice> getClients() {
        return clients;
    }
}
