package com.mauricio.sync.client;

public interface ISyncClientListener {
    void onConnect(String ip, int port);
    void onDisconnect(String ip, int port);
    void onAuthenticated(boolean status);
    void onFileAdded(String filename, boolean isDir); // (from the server)
    void onFileRemoved(String filename, boolean isDir); // (from the server)
    void onFileSyncStarted(String filename, boolean isDir);
    void onFileSyncCompleted(String filename, boolean isDir);
}
