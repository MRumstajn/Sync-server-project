package com.mauricio.sync.view.server;

/**
 * @author Mauricio Rumštajn
 */
public interface IServerLauncherWindowListener {
    /**
     * Called when launcher button is clicked.
     *
     * @param port
     * @param usePassword
     * @param password
     */
    void onStart(int port, boolean usePassword, String password);
}
