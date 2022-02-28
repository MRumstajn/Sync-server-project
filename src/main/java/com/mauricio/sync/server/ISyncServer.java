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
    void setDeviceAuth(int deviceID);
    void addRelayRoute(SyncClientDevice sender, SyncClientDevice receiver);
    void removeRelayRoute(SyncClientDevice sender);
    SyncClientDevice getRelayRoute(SyncClientDevice sender);
}
