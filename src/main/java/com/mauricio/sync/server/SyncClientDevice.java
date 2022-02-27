package com.mauricio.sync.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SyncClientDevice {
    private String name;
    private int id;
    private boolean isAuthenticated;
    private List<String> files;
    private Socket clientSocket;
    private SyncClientDeviceHandler handler;

    public SyncClientDevice(int id, Socket clientSocket, SyncClientDeviceHandler handler){
        this.id = id;
        this.clientSocket = clientSocket;
        this.handler = handler;
        files = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFile(String file){
        files.add(file);
    }

    public void removeFile(String file){
        files.remove(file);
    }

    public List<String> getFiles() {
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
}
