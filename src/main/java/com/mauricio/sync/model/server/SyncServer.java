package com.mauricio.sync.model.server;

import com.mauricio.sync.model.events.EventEmitter;
import com.mauricio.sync.model.packets.parsers.IPacketParser;
import com.mauricio.sync.model.packets.parsers.PacketParserFactory;
import com.mauricio.sync.model.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.model.packets.wrappers.RemoveFilesPacketWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mauricio Rumštajn
 */
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

    /**
     * {@inheritDoc}
     * @throws IOException
     */
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

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void stop() throws IOException{
        serverSocket.close();
        for (ISyncServerListener listener : getListeners()) {
            listener.onServerStop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean usesPassword() {
        return usePassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsUsingPassword(boolean isUsing) {
        usePassword = isUsing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SyncClientDevice> getClients() {
        return clients;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncClientDevice getDeviceWithID(int id) {
        for (SyncClientDevice client : clients) {
            if (client.getId() == id){
                return client;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncClientDevice getDeviceWithUsername(String username) {
        for (SyncClientDevice client : clients) {
            if (client.getName() == username){
                return client;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDevice(SyncClientDevice device){
        clients.add(device);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientConnect(device);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeDevice(SyncClientDevice device){
        // remove all files linked to the client
        System.out.println("device file map size: " + device.getFiles().size());
        for (String s : device.getFiles().keySet()) {
            System.out.println("Server removing file " + s);
            try {
                broadcastRemoveFilesPacket(s, device.getName(), device.getFiles().get(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (ISyncServerListener listener : getListeners()) {
                listener.onRemoveFile(device, s, device.getFiles().get(s));
            }
        }
        if (!device.getClientSocket().isClosed()){
            try {
                device.getClientSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clients.remove(device);

        for (ISyncServerListener listener : getListeners()) {
            listener.onClientDisconnect(device);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeviceAuth(SyncClientDevice device){
        device.setAuthenticated(true);
        for (ISyncServerListener listener : getListeners()) {
            listener.onClientAuthenticated(device);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFile(SyncClientDevice device, String path, boolean isDir) {
        device.addFile(path, isDir);
        for (ISyncServerListener listener : getListeners()) {
            listener.onAddFile(device, path, isDir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFile(SyncClientDevice device, String path, boolean isDir) {
        device.removeFile(path);
        for (ISyncServerListener listener : getListeners()) {
            listener.onRemoveFile(device, path, isDir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncStarted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
        for (ISyncServerListener listener : getListeners()) {
            listener.onSyncStart(file, cl1, cl2);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void syncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
        for (ISyncServerListener listener : getListeners()) {
            listener.onSyncCompleted(file, cl1, cl2);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRelayRoute(SyncClientDevice sender, SyncClientDevice receiver) {
        relayRouteMap.put(sender, receiver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRelayRoute(SyncClientDevice sender) {
        relayRouteMap.remove(sender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncClientDevice getRelayRoute(SyncClientDevice sender) {
        return relayRouteMap.get(sender);
    }

    /**
     * Get number of connected clients.
     *
     * @return number of clients.
     */
    public int getConnectedClientCount(){
        return clients.size();
    }

    /**
     * Check if name is used.
     *
     * @param username
     * @return is used.
     */
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

    /**
     * Notify all clients about a file being removed.
     *
     * @param filename
     * @param host
     * @param isDir
     * @throws IOException
     */
    private void broadcastRemoveFilesPacket(String filename, String host, boolean isDir) throws IOException {
        RemoveFilesPacketWrapper removeFilePacket = (RemoveFilesPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("remove_files", packetParser.getPacketClass());
        removeFilePacket.addFile(filename, isDir);
        removeFilePacket.setHost(host);
        for (SyncClientDevice client : clients) {
            client.getHandler().sendPacket(removeFilePacket);
        }
    }
}
