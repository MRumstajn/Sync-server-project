package com.mauricio.sync.model.client;

import com.mauricio.sync.model.events.EventEmitter;
import com.mauricio.sync.model.packets.parsers.PacketParserFactory;
import com.mauricio.sync.model.packets.wrappers.*;
import com.mauricio.sync.model.packets.IPacket;
import com.mauricio.sync.model.packets.parsers.IPacketParser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * @author Mauricio Rumštajn
 */
public class SyncClient extends EventEmitter<ISyncClientListener> implements ISyncClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String ip;
    private int port;
    private String username;
    private String password;
    private boolean authenticated;
    private IPacketParser packetParser;
    private SyncFileObserver fileObserver;
    private Map<String, Boolean> serverFilesMap;
    public static final int PACKET_PAYLOAD_SIZE = 1024;

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public SyncClient(String ip, int port, String username, String password, String packetParserType)
            throws InvalidParameterException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        packetParser = PacketParserFactory.createParser(packetParserType);
        if (packetParser == null) {
            throw new InvalidParameterException("Invalid packet parser type " + packetParserType);
        }
        fileObserver = new SyncFileObserver();
        serverFilesMap = new HashMap<>();
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        for (ISyncClientListener listener : getListeners()) {
            listener.onConnect(ip, port);
        }
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
        fileObserver.addListener(new ISyncFileObserverListener() {

            @Override
            public void onFileAdded(String filename, boolean isDir) {
                try {
                    addFile(filename, username, isDir);
                    registerFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFileRemoved(String filename, boolean isDir) {
                try {
                    removeFile(filename, username, isDir);
                    unregisterRemovedFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(new SyncClientMessageReceiver(this, clientSocket, packetParser)).start();
        new Thread(fileObserver).start();
        // client authentication test
        System.out.println("Authenticating...");
        AuthPacketWrapper authPacket = (AuthPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("auth", packetParser.getPacketClass());
        authPacket.setUsername(username);
        authPacket.setPassword(password);
        authPacket.setStatus(false);
        sendPacket(authPacket);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        clientSocket.close();
        for (ISyncClientListener listener : getListeners()) {
            listener.onDisconnect(ip, port);
        }
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void sendPacket(IPacket packet) throws IOException {
        out.writeUTF(packet.stringify());
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     *
     */
    @Override
    public void sendFile(String path, boolean sendEof) throws IOException {
        System.out.println("Sending file " + path + "...");
        File file = fileObserver.getFile(path);
        FileInputStream in = new FileInputStream(file);
        long fileSize = Files.size(file.toPath());
        int packetsRequired = (int) (fileSize / PACKET_PAYLOAD_SIZE);
        if (packetsRequired == 0) {
            packetsRequired = 1;
        }
        for (int i = 0; i < packetsRequired; i++) {
            byte[] buff = new byte[PACKET_PAYLOAD_SIZE];
            in.read(buff);
            SyncDataPacketWrapper dataPacket = (SyncDataPacketWrapper)
                    PacketWrapperFactory.createPacketWrapper("sync_data", packetParser.getPacketClass());
            dataPacket.setPath(path);
            dataPacket.setData(Base64.getEncoder().encodeToString(buff));
            sendPacket(dataPacket);
            // give the server some time to process the previous packet
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }


        // when eof is omitted, the client can receive multiple files
        if (sendEof) {
            SyncDataPacketWrapper dataEndPacket = (SyncDataPacketWrapper)
                    PacketWrapperFactory.createPacketWrapper("sync_data", packetParser.getPacketClass());
            dataEndPacket.setPath(path);
            dataEndPacket.setData("eof");
            sendPacket(dataEndPacket);
        }
        in.close();
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void sendDir(String path) throws IOException {
        List<String> filePaths = fileObserver.deepListFiles(fileObserver.getFile(path),
                new ArrayList<>(), "", false);
        for (int i = 0; i < filePaths.size(); i++) {
            if (i < filePaths.size() - 1) {
                sendFile(filePaths.get(i), false);
            } else {
                sendFile(filePaths.get(i), true);
            }
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getServerIP() {
        return ip;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int getServerPort() {
        return port;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> getFileList() {
        return serverFilesMap;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void setAuthenticated(boolean status) {
        System.out.println("AUTHY");
        authenticated = status;
        System.out.println(getListeners().size());
        for (ISyncClientListener listener : getListeners()) {
            System.out.println("notified listener");
            listener.onAuthenticated(status);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean doesFileExist(String filename) {
        return fileObserver.doesFileExist(filename);
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean doesDirExist(String dirname) {
        return fileObserver.doesDirectoryExist(dirname);
    }

    /**
     *
     * {@inheritDoc}
     */
    public File getFile(String filename) {
        return fileObserver.getFile(filename);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public File getObservedDir() {
        return fileObserver.getObservedDir();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getFullPath(String filename) {
        return fileObserver.getFullPath(filename);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void addFile(String file, String host, boolean isDir) {
        serverFilesMap.put(file, isDir);
        for (ISyncClientListener listener : getListeners()) {
            listener.onFileAdded(file, host, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void removeFile(String file, String host, boolean isDir) {
        serverFilesMap.remove(file);
        for (ISyncClientListener listener : getListeners()) {
            listener.onFileRemoved(file, host, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void fileSyncStarted(String file, boolean isDir) {
        for (ISyncClientListener listener : getListeners()) {
            listener.onFileSyncStarted(file, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void fileSyncCompleted(String file, boolean isDir) {
        for (ISyncClientListener listener : getListeners()) {
            listener.onFileSyncCompleted(file, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void setObservedDir(File dir) {
        fileObserver.setObservedDir(dir);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void writeBuffer(byte[] buff, String path) throws IOException {
        fileObserver.writeBuffer(buff, path);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void registerFiles() throws IOException {
        AddFilesPacketWrapper addFilesPacket = (AddFilesPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("add_files", packetParser.getPacketClass());
        Map<String, Boolean> syncStatusMap = fileObserver.getSyncStatusMap();
        Map<String, Boolean> addCache = fileObserver.getAddCache();
        for (String filename : addCache.keySet()){
            // get relative path (from root to file)
            String relPath = fileObserver.deepListFiles(fileObserver.getObservedDir(),
                    new ArrayList<>(), filename, true).get(0);
            //addFilesPacket.addFile(relPath, addCache.get(relPath));
            addFilesPacket.addFile(filename, addCache.get(relPath));
            syncStatusMap.put(filename, true);
            addCache.remove(filename);
        }
        //addCache.clear();
        sendPacket(addFilesPacket);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void unregisterRemovedFiles() throws IOException {
        RemoveFilesPacketWrapper removeFilesPacket = (RemoveFilesPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("remove_files", packetParser.getPacketClass());
        Map<String, Boolean> removeCache = fileObserver.getRemoveCache();
        for (String filename : removeCache.keySet()){
            // get relative path (from root to file)
            /*String relPath = fileObserver.deepListFiles(fileObserver.getObservedDir(),
                    new ArrayList<>(), filename, true).get(0);
            System.out.println(".-------------------------." + relPath);*/
            //removeFilesPacket.addFile(relPath, removeCache.get(filename));
            removeFilesPacket.addFile(filename, removeCache.get(filename));
            removeCache.remove(filename);
        }
        //removeCache.clear();
        sendPacket(removeFilesPacket);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void downloadFile(String filename, String host, boolean isDir) throws IOException {
        SyncFilePacketWrapper syncPacket = (SyncFilePacketWrapper)
                PacketWrapperFactory.createPacketWrapper("sync", packetParser.getPacketClass());
        syncPacket.setPath(filename);
        syncPacket.setIsDir(isDir);
        sendPacket(syncPacket);
    }

    /**
     *
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void fetchFileList() throws IOException {
        ListFilesPacketWrapper listPacket = (ListFilesPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("list_files", packetParser.getPacketClass());
        sendPacket(listPacket);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void serverFileListed(String filename, String host, boolean isDir) {
        for (ISyncClientListener listener : getListeners()) {
            listener.onServerFileListed(filename, host, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void serverFileUnlisted(String filename, String host, boolean isDir) {
        for (ISyncClientListener listener : getListeners()) {
            listener.onServerFileUnlisted(filename, host, isDir);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void receivedListPacket() {
        for (ISyncClientListener listener : getListeners()) {
            listener.onReceivedListPacket();
        }
    }
}
