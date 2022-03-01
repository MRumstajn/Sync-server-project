package com.mauricio.sync.model.server;

public interface ISyncServerListener {
    void onServerStart();
    void onServerStop();
    void onClientConnect(SyncClientDevice client);
    void onClientDisconnect(SyncClientDevice client);
    void onClientAuthenticated(SyncClientDevice client);
    void onClientSetName(SyncClientDevice client);
    void onAddFile(SyncClientDevice client, String filename, boolean isDir);
    void onRemoveFile(SyncClientDevice client, String filename, boolean isDir);
    void onSyncStart(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    void onSyncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
}
