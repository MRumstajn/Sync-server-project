package com.mauricio.sync.model.client;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncFileObserverListener {
    /**
     * Called when a new file is added to the observed directory.
     *
     * @param filename
     * @param isDir
     */
    void onFileAdded(String filename, boolean isDir);
    /**
     * Called when an existing file is remove from the observed directory..
     *
     * @param filename
     * @param isDir
     */
    void onFileRemoved(String filename, boolean isDir);
}
