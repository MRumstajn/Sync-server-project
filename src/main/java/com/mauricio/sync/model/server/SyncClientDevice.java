package com.mauricio.sync.model.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFile(String file, boolean isDir){
        files.put(file, isDir);
    }

    public void removeFile(String file){
        files.remove(file);
    }

    public Map<String, Boolean> getFiles() {
        return files;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public SyncClientDeviceHandler getHandler() {
        return handler;
    }

    public int getId() {
        return id;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
