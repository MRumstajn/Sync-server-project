package com.mauricio.sync.model.client;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncFileObserver {
    /**
     * Stops the observer.
     */
    void stop();
    /**
     * Sets the observed directory.
     *
     * @param dir the file obj.
     */
    void setObservedDir(File dir);
    /**
     *
     * @return the observed dir.
     */
    File getObservedDir();
    /**
     * Get file from observed directory root.
     *
     * @param filename
     * @return the file obj.
     */
    File getFile(String filename);
    /**
     * Get full path to a file in the observed dir.
     *
     * @param filename
     * @return the full path.
     */
    String getFullPath(String filename);
    /**
     *
     * @param filename
     * @return true if exists, false otherwise
     */
    boolean doesFileExist(String filename);
    /**
     *
     * @param dirname
     * @return true if exists, false otherwise
     */
    boolean doesDirectoryExist(String dirname);
    /**
     * Get all files from observed directory.
     *
     * @return list of files.
     */
    File[] getFiles();
    /**
     * Sync status map keeps track of files and whether they are registered on the server.
     * <pre>
     *     Struct:
     *          - String filename
     *          - Boolean registered
     * </pre>
     * @return the sync map.
     */
    Map<String, Boolean> getSyncStatusMap();
    /**
     * Write data to file.
     *
     * @param buff bytes to write.
     * @param path file to write to.
     * @throws IOException if an I/O exception occurs.
     */
    void writeBuffer(byte[] buff, String path) throws IOException;
    /**
     * Add cache temporarily stores files that were recently added.
     * It is emptied by the client once the files are registered.
     *
     * @return the add cache.
     */
    Map<String, Boolean> getAddCache();
    /**
     * Remove cache temporarily stores files that were recently removed.
     * It is emptied by the client once the files are unregistered.
     *
     * @return the remove cache.
     */
    Map<String, Boolean> getRemoveCache();
}
