package com.mauricio.sync.view.client;

import java.io.File;

public interface IClientWindowListener {
    void onSetSyncDir(File dir);
    void onDownloadFile(String file, boolean isDir, String host);
}
