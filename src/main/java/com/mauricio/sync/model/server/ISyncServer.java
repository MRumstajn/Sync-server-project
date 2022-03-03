package com.mauricio.sync.model.server;

import java.io.IOException;
import java.util.List;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncServer {
    /**
     * Starts the server.
     * @throws IOException if an I/O exception occurs.
     */
    void start() throws IOException;
    /**
     * Stops the server.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void stop() throws IOException;
    /**
     * Get the server port.
     *
     * @return port.
     */
    int getPort();
    /**
     * Get is server using password.
     *
     * @return is using password
     */
    boolean usesPassword();
    /**
     * Set is server using password.
     * @param isUsing
     */
    void setIsUsingPassword(boolean isUsing);
    /**
     * Set server password.
     *
     * @param password
     */
    void setPassword(String password);
    /**
     * Get server password.
     *
     * @return password
     */
    String getPassword();
    /**
     * Validate received password against server password.
     *
     * @param password
     * @return is password valid
     */
    boolean isPasswordValid(String password);
    /**
     * Get connected clients.
     *
     * @return clients
     */
    List<SyncClientDevice> getClients();
    /**
     * Get host of file.
     *
     * @param path name of the file.
     * @return client device that is the host.
     */
    SyncClientDevice getFileHost(String path);
    /**
     * Get device by id.
     *
     * @param id
     * @return device or null if id is invalid.
     */
    SyncClientDevice getDeviceWithID(int id);
    /**
     * Get device by name.
     *
     * @param username
     * @return device or null if username is invalid.
     */
    SyncClientDevice getDeviceWithUsername(String username);

    /**
     * Add device to client list.
     *
     * @param device
     */
    void addDevice(SyncClientDevice device);
    /**
     * Remove device from client list.
     *
     * @param device
     */
    void removeDevice(SyncClientDevice device);
    /**
     * Set name for device (also notifies listeners).
     *
     * @param device
     * @param name
     */
    void setDeviceName(SyncClientDevice device, String name);
    /**
     * Set authentication status for device (also notifies listeners).
     *
     * @param device
     */
    void setDeviceAuth(SyncClientDevice device);
    /**
     * Called when file is registered.
     *
     * @param device device that registered file.
     * @param path filename.
     * @param isDir is file a dir.
     */
    void addFile(SyncClientDevice device, String path, boolean isDir);
    /**
     * Called when a file is unregistered.
     *
     * @param device device that unregistered file.
     * @param path filename.
     * @param isDir is file a dir.
     */
    void removeFile(SyncClientDevice device, String path, boolean isDir);
    /**
     * Called when relaying started for a file.
     *
     * @param file filename
     * @param cl1 sender
     * @param cl2 receiver
     */
    void syncStarted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    /**
     * Called when relaying completed for a file.
     *
     * @param file filename
     * @param cl1 sender
     * @param cl2 receiver
     */
    void syncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2);
    /**
     * Adds a relay route (or bridge) that connects two clients.
     * Used for relaying file data.
     * Bridge closes if the file data is "eof".
     *
     * @param sender
     * @param receiver
     */
    void addRelayRoute(SyncClientDevice sender, SyncClientDevice receiver);
    /**
     * Removes a relay route (or bridge) that connects two clients.
     * Used for relaying file data.
     * Bridge closes if the file data is "eof".
     *
     * @param sender
     */
    void removeRelayRoute(SyncClientDevice sender);
    /**
     * Gets the receiver of a relay route.
     *
     * @param sender
     * @return receiver
     */
    SyncClientDevice getRelayRoute(SyncClientDevice sender);
}
