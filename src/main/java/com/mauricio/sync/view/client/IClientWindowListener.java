package com.mauricio.sync.view.client;

import java.io.File;

/**
 * @author Mauricio Rumštajn
 */
public interface IClientWindowListener {
    /**
     * Called when "Apply" button is clicked.
     *
     * @param dir
     */
    void onSetSyncDir(File dir);
    /**
     * Called when one of the download buttons is clicked.
     *
     * @param file
     * @param isDir
     * @param host
     */
    void onDownloadFile(String file, boolean isDir, String host);

    /**
     * Called when window is closed.
     */
    void onWindowClose();
}
