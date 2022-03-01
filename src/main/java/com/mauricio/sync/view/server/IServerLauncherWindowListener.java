package com.mauricio.sync.view.server;

public interface IServerLauncherWindowListener {
    void onStart(int port, boolean usePassword, String password);
}
