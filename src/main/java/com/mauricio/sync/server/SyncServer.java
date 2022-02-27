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
    private String password;
    private boolean usePassword;
    private int deviceIDCounter = 0;

    @SuppressWarnings("deprecation")
    public SyncServer(int port, Class<? extends IPacketParser> packetParserClass, String password)
            throws InstantiationException,
            IllegalAccessException {
        this.port = port;
        this.packetParser = packetParserClass.newInstance();
        this.password = password;
        usePassword = password.length() > 0;
        clients = new ArrayList<>();
    }

    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        while (!serverSocket.isClosed()){
            Socket client = serverSocket.accept();
            System.out.println("New connection from " + client.getRemoteSocketAddress());
            SyncClientDeviceHandler handler = new SyncClientDeviceHandler(this, client, packetParser, deviceIDCounter);
            SyncClientDevice device = new SyncClientDevice(deviceIDCounter, client, handler);
            clients.add(device);
            new Thread(handler).start();
            deviceIDCounter += 1;
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
    public boolean usesPassword() {
        return usePassword;
    }

    @Override
    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }

    @Override
    public List<SyncClientDevice> getClients() {
        return clients;
    }

    @Override
    public SyncClientDevice getDeviceWithID(int id) {
        for (SyncClientDevice client : clients) {
            if (client.getId() == id){
                return client;
            }
        }
        return null;
    }

    public void addDevice(SyncClientDevice device){
        clients.add(device);
    }

    public void removeDevice(SyncClientDevice device){
        clients.remove(device);
    }

    public void setDeviceName(int deviceID, String name){
        if (isNameUsed(name)) {
            name += "(" + deviceID + ")";
        }
        getDeviceWithID(deviceID).setName(name);
    }

    @Override
    public void setDeviceAuth(int deviceID){
        getDeviceWithID(deviceID).setAuthenticated(true);
    }

    public int getConnectedClientCount(){
        return clients.size();
    }

    private boolean isNameUsed(String username){
        for (SyncClientDevice client : clients) {
            if (client.getName().equals(username)){
                return true;
            }
        }
        return false;
    }
}
