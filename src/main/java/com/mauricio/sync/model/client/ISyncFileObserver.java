package com.mauricio.sync.model.client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ISyncFileObserver {
    void stop();
    void setObservedDir(File dir);
    File getObservedDir();
    File getFile(String filename);
    String getFullPath(String filename);
    boolean doesFileExist(String filename);
    boolean doesDirectoryExist(String dirname);
    File[] getFiles();
    Map<String, Boolean> getSyncStatusMap();
    void writeBuffer(byte[] buff, String path) throws IOException;
    Map<String, Boolean> getAddCache();
    Map<String, Boolean> getRemoveCache();
}
