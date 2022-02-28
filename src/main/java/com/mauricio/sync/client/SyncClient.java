package com.mauricio.sync.client;

import com.mauricio.sync.packets.parsers.PacketParserFactory;
import com.mauricio.sync.packets.wrappers.*;
import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SyncClient implements ISyncClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String ip;
    private int port;
    private String username;
    private String password;
    private IPacketParser packetParser;
    private SyncFileObserver fileObserver;
    public static final int PACKET_PAYLOAD_SIZE = 1024;

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public SyncClient(String ip, int port, String username, String password, String packetParserType)
            throws InvalidParameterException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        packetParser = PacketParserFactory.createParser(packetParserType);
        if (packetParser == null){
            throw new InvalidParameterException("Invalid packet parser type " + packetParserType);
        }
        fileObserver = new SyncFileObserver();
    }

    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
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
        registerFiles();
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
    public void sendFile(String path) throws IOException {
        File file = fileObserver.getFile(path);
        FileInputStream in = new FileInputStream(file);
        long fileSize = Files.size(file.toPath());
        int packetsRequired = (int) (fileSize / PACKET_PAYLOAD_SIZE);
        if (packetsRequired == 0){
            packetsRequired = 1;
        }
        for (int i = 0; i < packetsRequired; i++) {
            byte[] buff = new byte[PACKET_PAYLOAD_SIZE];
            if (in.read(buff) > 0) {
                SyncDataPacketWrapper dataPacket = (SyncDataPacketWrapper)
                        PacketWrapperFactory.createPacketWrapper("sync_data", packetParser.getPacketClass());
                dataPacket.setPath(path);
                dataPacket.setData(Base64.getEncoder().encodeToString(buff));
                sendPacket(dataPacket);
            } else {
                break;
            }
            // give the server some time to process the previous packet
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }
        SyncDataPacketWrapper dataEndPacket = (SyncDataPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("sync_data", packetParser.getPacketClass());
        dataEndPacket.setPath(path);
        dataEndPacket.setData("eof");
        sendPacket(dataEndPacket);
        in.close();


    }

    @Override
    public void sendDir(String path) throws IOException{

    }

    @Override
    public String getServerIP() {
        return ip;
    }

    @Override
    public int getServerPort() {
        return port;
    }

    public boolean doesFileExist(String filename) {
        return fileObserver.doesFileExist(filename);
    }

    public boolean doesDirExist(String dirname) {
        return fileObserver.doesDirectoryExist(dirname);
    }

    public File getFile(String filename) {
        return fileObserver.getFile(filename);
    }

    @Override
    public File getObservedDir() {
        return fileObserver.getObservedDir();
    }

    @Override
    public String getFullPath(String filename) {
        return fileObserver.getFullPath(filename);
    }

    @Override
    public void setObservedDir(File dir) {
        fileObserver.setObservedDir(dir);
    }

    @Override
    public void writeBuffer(byte[] buff, String path) throws IOException {
        fileObserver.writeBuffer(buff, path);
    }

    @Override
    public void registerFiles() throws IOException {
        AddFilesPacketWrapper addFilesPacket = (AddFilesPacketWrapper)
                PacketWrapperFactory.createPacketWrapper("add_files", packetParser.getPacketClass());
        List<String> files = new ArrayList<>();
        for (File file : fileObserver.getFiles()){
            files.add(file.getName());
        }
        addFilesPacket.setFiles(files);
        sendPacket(addFilesPacket);
    }
}
