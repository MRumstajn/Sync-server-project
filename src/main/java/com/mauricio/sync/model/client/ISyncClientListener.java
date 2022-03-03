package com.mauricio.sync.model.client;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncClientListener {
    /**
     * Called when client connects to the server.
     *
     * @param ip ip of the server
     * @param port port of the server
     */
    void onConnect(String ip, int port);
    /**
     * Called when client disconnects from the server.
     *
     * @param ip ip of the server.
     * @param port port of the server.
     */
    void onDisconnect(String ip, int port);
    /**
     * Called when client is authenticated or fails authentication.
     *
     * @param status whether the authentication was successful
     */
    void onAuthenticated(boolean status);
    /**
     * Called when server notifies client about added file.
     *
     * @param filename name of file that was added.
     * @param host host of file that was added.
     * @param isDir whether file is a directory.
     */
    void onFileAdded(String filename, String host, boolean isDir);
    /**
     * Called when server notifies client about removed file.
     *
     * @param filename name of file that was removed.
     * @param host host of file that was removed.
     * @param isDir whether file is a directory.
     */
    void onFileRemoved(String filename, String host, boolean isDir);
    /**
     * Called when server starts relaying file data.
     *
     * @param filename name of file that is being relayed.
     * @param isDir whether file is a directory.
     */
    void onFileSyncStarted(String filename, boolean isDir);
    /**
     * Called when server stops relaying file data.
     *
     * @param filename name of file that is being relayed.
     * @param isDir whether file is a directory.
     */
    void onFileSyncCompleted(String filename, boolean isDir);
    /**
     * Called when server tells client about a registered file.
     *
     * @param filename name of file.
     * @param isDir whether file is a directory.
     */
    void onServerFileListed(String filename, String host, boolean isDir);
    /**
     * Called when server tells client that the registered file is removed.
     *
     * @param filename name of file.
     * @param isDir whether file is a directory.
     */
    void onServerFileUnlisted(String filename, String host, boolean isDir);
    /**
     * Called when the client receives a list packet from the server.
     */
    void onReceivedListPacket();
}
