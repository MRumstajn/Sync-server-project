package com.mauricio.sync.model.server;

import com.mauricio.sync.model.events.EventEmitter;
import com.mauricio.sync.model.packets.parsers.IPacketParser;
import com.mauricio.sync.model.packets.parsers.PacketParserFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncServer extends EventEmitter<ISyncServerListener> implements ISyncServer{
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
        for (ISyncServerListener listener : getListeners()) {
            listener.onServerStart();
        }
        while (!serverSocket.isClosed()){
            Socket client = serverSocket.accept();
            System.out.println("New connection from " + client.getRemoteSocketAddress());
            SyncClientDeviceHandler handler = new SyncClientDeviceHandler(this, client, packetParser, deviceIDCounter);
            SyncClientDevice device = new SyncClientDevice(deviceIDCounter, client, handler);
            addDevice(device);
            new Thread(handler).start();
            // parser is not thread safe so every thread needs its own parser
            packetParser = PacketParserFactory.createParser(packetParserType);
            deviceIDCounter += 1;
            for (ISyncServerListener listener : getListeners()) {
                listener.onClientConnect(device);
            }
        }
    }

    @Override
    public void stop() throws IOException{
        serverSocket.close();
        for (ISyncServerListener listener : getListeners()) {
            listener.onServerStop();
        }
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
    public void setIsUsingPassword(boolean isUsing) {
        usePassword = isUsing;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isPasswordValid(String password) {
        if (!usePassword) {
            return true;
        }
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

    @Override
    public void addDevice(SyncClientDevice device){
        clients.add(device);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientConnect(device);
        }
    }

    @Override
    public void removeDevice(SyncClientDevice device){
        clients.remove(device);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientDisconnect(device);
        }
    }

    @Override
    public void setDeviceName(SyncClientDevice device, String name){
        if (isNameUsed(name)) {
            name += "(" + device.getId() + ")";
        }
        device.setName(name);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientSetName(device);
        }
    }

    @Override
    public void setDeviceAuth(SyncClientDevice device){
        device.setAuthenticated(true);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientAuthenticated(device);
        }
    }

    @Override
    public void addFile(SyncClientDevice device, String path, boolean isDir) {
        device.addFile(path, isDir);
        for (ISyncServerListener listener : getListeners()) {
            listener.onAddFile(device, path, isDir);
        }
    }

    @Override
    public void removeFile(SyncClientDevice device, String path, boolean isDir) {
        device.removeFile(path);
        for (ISyncServerListener listener : getListeners()) {
            listener.onRemoveFile(device, path, isDir);
        }
    }

    @Override
    public void syncStarted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
        for (ISyncServerListener listener : getListeners()) {
            listener.onSyncStart(file, cl1, cl2);
        }
    }

    @Override
    public void syncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
        for (ISyncServerListener listener : getListeners()) {
            listener.onSyncCompleted(file, cl1, cl2);
        }
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
            if (!client.isAuthenticated()){
                continue;
            }
            if (client.getName().equals(username)){
                return true;
            }
        }
        return false;
    }
}
