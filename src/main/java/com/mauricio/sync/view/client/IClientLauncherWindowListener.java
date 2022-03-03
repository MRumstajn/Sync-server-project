package com.mauricio.sync.view.client;

/**
 * @author Mauricio Rumštajn
 */
public interface IClientLauncherWindowListener {
    /**
     * Called when launcher button is clicked.
     *
     * @param ip
     * @param port
     * @param password
     */
    void onConnect(String ip, int port, String password);
}
