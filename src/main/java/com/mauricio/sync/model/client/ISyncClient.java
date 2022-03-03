package com.mauricio.sync.model.client;

import com.mauricio.sync.model.packets.IPacket;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Mauricio Rumštajn
 */
public interface ISyncClient {
    /**
     * Connect the client to the serve.
     *
     * @throws IOException if an I/O exception occurs
     */
    void connect() throws IOException;
    /**
     * Disconnects the client from the server.
     *
     * @throws IOException if an I/O exception occurs
     */
    void disconnect() throws IOException;
    /**
     * Sends a {@link IPacket} to the server.
     * @param packet
     * @throws IOException
     * @see IPacket
     */
    void sendPacket(IPacket packet) throws IOException;
    /**
     * Sends file data to the server.
     *
     * @param path the path to the file to send.
     * @param sendEof omitting EOF allows multiple files to be sent in one go.
     *                When server receives an EOF, the relaying stops.
     * @throws IOException if an I/O exception occurs.
     */
    void sendFile(String path, boolean sendEof) throws IOException;
    /**
     * Sends directory to the server.
     *
     * @param path the path to the directory to send.
     * @throws IOException if an I/O exception occurs.
     */
    void sendDir(String path) throws IOException;
    /**
     *
     * @return the IP of the server the client is connected to.
     */
    String getServerIP();

    /**
     *
     * @return the port of the server the client is connected to
     */
    int getServerPort();
    /**
     * The local file map keeps track of files and whether they are directories.
     * <pre>
     *    Struct:
     *      - String filename
     *      - Boolean isDir
     * </pre>
     *
     * @return the local file map.
     */
    Map<String, Boolean> getFileList();
    /**
     * Sets the client authenticated status.
     *
     * @param status whether the client is authenticated.
     */
    void setAuthenticated(boolean status);
    /**
     *
     * @return is the client authenticated.
     */
    boolean isAuthenticated();
    /**
     *
     * @param filename the file to check.
     * @return true if file exists or false if not.
     */
    boolean doesFileExist(String filename);
    /**
     *
     * @param dirname the directory to check.
     * @return true if directory exists or false if not.
     */
    boolean doesDirExist(String dirname);
    /**
     * Gets the specified file from {@link ISyncFileObserver}.
     *
     * @param filename the file to get.
     * @return the file object.
     */
    File getFile(String filename);
    /**
     * Get the directory observed by {@link ISyncFileObserver}.
     *
     * @return the directory object;
     */
    File getObservedDir();
    /**
     * Get the full path to a file in the observed directory.
     *
     * @param filename the file to get path to.
     * @return full path to the file.
     */
    String getFullPath(String filename);
    /**
     * Add file to the client cache.
     *
     * @param file the name of the file to add.
     * @param host the name of the client who is hosting this file.
     * @param isDir whether the file is a directory.
     */
    void addFile(String file, String host, boolean isDir);
    /**
     * Remove file from client cache.
     *
     * @param file the name of the file to remove.
     * @param host the name of the client who is hosting this file.
     * @param isDir whether the file is a directory.
     */
    void removeFile(String file, String host, boolean isDir);
    /**
     * Notifies listeners of started sync.
     *
     * @param file file to sync.
     * @param isDir whether the file is a directory.
     */
    void fileSyncStarted(String file, boolean isDir);
    /**
     * Notifies listeners of completed sync.
     *
     * @param file file that synced.
     * @param isDir whether the file is a directory.
     */
    void fileSyncCompleted(String file, boolean isDir);
    /**
     * Setter for the observed directory of {@link ISyncFileObserver}.
     *
     * @param file the folder file object.
     */
    void setObservedDir(File file);
    /**
     * Writes bytes to a file.
     *
     * @param buff bytes to write.
     * @param path file to write in.
     * @throws IOException if an I/O exception occurs.
     */
    void writeBuffer(byte[] buff, String path) throws IOException;
    /**
     * Notifies the server about new files in {@link ISyncFileObserver}'s caches.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void registerFiles() throws IOException;
    /**
     * Notifies the server about removed files in {@link ISyncFileObserver}'s caches.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void unregisterRemovedFiles() throws IOException;
    /**
     * Downloads the file to the observed directory.
     *
     * @param filename the name of the file to download.
     * @param host the name of the host the file is hosted on.
     * @param isDir whether the file is a directory.
     * @throws IOException if an I/O exception occurs.
     */
    void downloadFile(String filename, String host, boolean isDir) throws IOException;
    /**
     * Requests the server to send a list of registered files.
     *
     * @throws IOException if an I/O exception occurs.
     */
    void fetchFileList() throws IOException;
    /**
     * Notifies listeners when server lists a registered file.
     *
     * @param filename registered file name.
     * @param host host from which the file was registered.
     * @param isDir whether the file is a directory.
     */
    void serverFileListed(String filename, String host, boolean isDir);
    /**
     * Notifies listeners when server unlists a registered file.
     * @param filename registered file name.
     * @param host host from which the file was registered.
     * @param isDir whether the file is a directory.
     */
    void serverFileUnlisted(String filename, String host, boolean isDir);
    /**
     * Notifies listeners when a list packet is received.
     */
    void receivedListPacket();
}
