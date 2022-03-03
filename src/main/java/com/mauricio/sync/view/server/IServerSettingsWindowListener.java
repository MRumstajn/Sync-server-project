package com.mauricio.sync.view.server;

/**
 * @author Mauricio Rumštajn
 */
public interface IServerSettingsWindowListener {
    /**
     * Called when "Apply" button is clicked.
     *
     * @param usePassword
     * @param newPassword
     */
    void onApply(boolean usePassword, String newPassword);
}
