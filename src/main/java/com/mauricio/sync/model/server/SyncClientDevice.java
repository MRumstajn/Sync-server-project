package com.mauricio.sync.model.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mauricio Rumštajn
 */
public class SyncClientDevice {
    private String name;
    private int id;
    private boolean isAuthenticated;
    private Map<String, Boolean> files;
    private Socket clientSocket;
    private SyncClientDeviceHandler handler;

    public SyncClientDevice(int id, Socket clientSocket, SyncClientDeviceHandler handler){
        this.id = id;
        this.clientSocket = clientSocket;
        this.handler = handler;
        files = new HashMap<>();
    }

    /**
     * Set client name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get client name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Add file to client files register.
     *
     * @param file
     * @param isDir
     */
    public void addFile(String file, boolean isDir){
        files.put(file, isDir);
    }

    /**
     * Remove file from client files register.
     *
     * @param file
     */
    public void removeFile(String file){
        files.remove(file);
    }

    /**
     * Get client files register.
     *
     * @return register
     */
    public Map<String, Boolean> getFiles() {
        return files;
    }

    /**
     * Set client authentication status.
     *
     * @param authenticated
     */
    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    /**
     * Get client authentication status.
     *
     * @return status
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Get device handler.
     *
     * @return handler
     */
    public SyncClientDeviceHandler getHandler() {
        return handler;
    }

    /**
     * Get device id.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Get client socket.
     *
     * @return socket
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
}
