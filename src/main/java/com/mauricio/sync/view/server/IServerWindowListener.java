package com.mauricio.sync.view.server;

/**
 * @author Mauricio Rumštajn
 */
public interface IServerWindowListener {
    /**
     * Called when stop button is clicked.
     */
    void onStopButtonClicked();

    /**
     * Called when log button is clicked.
     */
    void onLogButtonClicked();

    /**
     * Called when settings button is clicked.
     */
    void onSettingsButtonClicked();

    /**
     * Called when one of the client tab disconnect buttons is clicked.
     * @param username
     */
    void onClientDisconnectButtonClicked(String username);
}
