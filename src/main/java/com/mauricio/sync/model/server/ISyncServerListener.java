package com.mauricio.sync.model.server;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncServerListener {
    /**
     * Called when server starts.
     */
    void onServerStart();
    /**
     * Called when server stops.
     */
    void onServerStop();
    /**
     * Called when client connects to server.
     */
    void onClientConnect(SyncClientDevice client);
    /**
     * Called when client disconnects from server.
     */
    void onClientDisconnect(SyncClientDevice client);
    /**
     * Called when client authenticates with server.
     */
    void onClientAuthenticated(SyncClientDevice client);
    /**
     * Called when server sets client's name.
     */
    void onClientSetName(SyncClientDevice client);
    /**
     * Called when client registers a file.
     */
    void onAddFile(SyncClientDevice client, String filename, boolean isDir);
    /**
     * Called when client unregisters a file.
     */
    void onRemoveFile(SyncClientDevice client, String filename, boolean isDir);
    /**
     * Called when relaying for a file starts.
     */
    void onSyncStart(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    /**
     * Called when relaying for a file completes.
     */
    void onSyncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
}
