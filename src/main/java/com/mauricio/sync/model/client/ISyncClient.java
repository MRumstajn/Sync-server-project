package com.mauricio.sync.model.client;

import com.mauricio.sync.model.packets.IPacket;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ISyncClient {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void sendPacket(IPacket packet) throws IOException;
    void sendFile(String path, boolean sendEof) throws IOException;
    void sendDir(String path) throws IOException;
    String getServerIP();
    int getServerPort();
    Map<String, Boolean> getFileList();
    void setAuthenticated(boolean status);
    boolean isAuthenticated();
    boolean doesFileExist(String filename);
    boolean doesDirExist(String dirname);
    File getFile(String filename);
    File getObservedDir();
    String getFullPath(String filename);
    void addFile(String file, String host, boolean isDir);
    void removeFile(String file, String host, boolean isDir);
    void fileSyncStarted(String file, boolean isDir);
    void fileSyncCompleted(String file, boolean isDir);
    void setObservedDir(File file);
    void writeBuffer(byte[] buff, String path) throws IOException;
    void registerFiles() throws IOException;
    void unregisterRemovedFiles() throws IOException;
    void downloadFile(String filename, String host, boolean isDir) throws IOException;
    void fetchFileList() throws IOException;
    void serverFileListed(String filename, String host, boolean isDir);
    void serverFileUnlisted(String filename, String host, boolean isDir);
    void receivedListPacket();
}
