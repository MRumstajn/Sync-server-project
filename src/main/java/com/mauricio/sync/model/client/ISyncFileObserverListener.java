package com.mauricio.sync.model.client;

public interface ISyncFileObserverListener {
    void onFileAdded(String filename, boolean isDir); // discovered new file in folder
    void onFileRemoved(String filename, boolean isDir); // existing file removed from folder
}
