package com.mauricio.sync.client;

import com.mauricio.sync.packets.IPacket;

import java.io.File;
import java.io.IOException;

public interface ISyncClient {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void sendPacket(IPacket packet) throws IOException;
    void sendFile(String path, boolean sendEof) throws IOException;
    void sendDir(String path) throws IOException;
    String getServerIP();
    int getServerPort();
    void setAuthenticated(boolean status);
    boolean isAuthenticated();
    boolean doesFileExist(String filename);
    boolean doesDirExist(String dirname);
    File getFile(String filename);
    File getObservedDir();
    String getFullPath(String filename);
    void addFile(String file, boolean isDir);
    void removeFile(String file, boolean isDir);
    void fileSyncStarted(String file, boolean isDir);
    void fileSyncCompleted(String file, boolean isDir);
    void setObservedDir(File file);
    void writeBuffer(byte[] buff, String path) throws IOException;
    void registerFiles() throws IOException;
}
