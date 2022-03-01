package com.mauricio.sync.server;

import java.io.IOException;
import java.util.List;

public interface ISyncServer {
    void start() throws IOException;
    void stop() throws IOException;
    int getPort();
    boolean usesPassword();
    boolean isPasswordValid(String password);
    List<SyncClientDevice> getClients();
    SyncClientDevice getFileHost(String path);
    SyncClientDevice getDeviceWithID(int id);
    void addDevice(SyncClientDevice device);
    void removeDevice(SyncClientDevice device);
    void setDeviceName(SyncClientDevice device, String name);
    void setDeviceAuth(SyncClientDevice device);
    void addFile(SyncClientDevice device, String path, boolean isDir);
    void removeFile(SyncClientDevice device, String path, boolean isDir);
    void syncStarted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    void syncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    void addRelayRoute(SyncClientDevice sender, SyncClientDevice receiver);
    void removeRelayRoute(SyncClientDevice sender);
    SyncClientDevice getRelayRoute(SyncClientDevice sender);
}
