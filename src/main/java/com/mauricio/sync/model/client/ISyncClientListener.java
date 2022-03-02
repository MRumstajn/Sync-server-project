package com.mauricio.sync.model.client;

import java.util.Map;

public interface ISyncClientListener {
    void onConnect(String ip, int port);
    void onDisconnect(String ip, int port);
    void onAuthenticated(boolean status);
    void onFileAdded(String filename, String host, boolean isDir); // (from the server)
    void onFileRemoved(String filename, String host, boolean isDir); // (from the server)
    void onFileSyncStarted(String filename, boolean isDir);
    void onFileSyncCompleted(String filename, boolean isDir);
    void onServerFileListed(String filename, String host, boolean isDir); // file listed on server
    void onReceivedListPacket();
}
