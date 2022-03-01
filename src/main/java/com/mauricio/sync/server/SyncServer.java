package com.mauricio.sync.server;

import com.mauricio.sync.packets.parsers.IPacketParser;
import com.mauricio.sync.packets.parsers.PacketParserFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncServer implements ISyncServer{
    private ServerSocket serverSocket;
    private int port;
    private List<SyncClientDevice> clients;
    private Map<SyncClientDevice, SyncClientDevice> relayRouteMap;
    private String packetParserType;
    private IPacketParser packetParser;
    private String password;
    private boolean usePassword;
    private int deviceIDCounter = 0;
    public static final int PACKET_PAYLOAD_SIZE = 1024;

    @SuppressWarnings("deprecation")
    public SyncServer(int port, String packetParserType, String password) throws InvalidParameterException{
        this.port = port;
        this.password = password;
        this.packetParserType = packetParserType;
        packetParser = PacketParserFactory.createParser(packetParserType);
        if (packetParser == null){
            throw new InvalidParameterException("Invalid parser type " + packetParserType);
        }
        usePassword = password.length() > 0;
        clients = new ArrayList<>();
        relayRouteMap = new HashMap<>();
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
            // parser is not thread safe so every thread needs its own parser
            packetParser = PacketParserFactory.createParser(packetParserType);
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
    public SyncClientDevice getFileHost(String path) {
        for (SyncClientDevice device : clients) {
            for (String filePath : device.getFiles().keySet()){
               if (filePath.equals(path)){
                   return device;
               }
            }
        }
        return null;
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

    @Override
    public void addRelayRoute(SyncClientDevice sender, SyncClientDevice receiver) {
        relayRouteMap.put(sender, receiver);
    }

    @Override
    public void removeRelayRoute(SyncClientDevice sender) {
        relayRouteMap.remove(sender);
    }

    @Override
    public SyncClientDevice getRelayRoute(SyncClientDevice sender) {
        return relayRouteMap.get(sender);
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
