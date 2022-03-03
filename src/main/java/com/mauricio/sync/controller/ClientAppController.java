package com.mauricio.sync.controller;

import com.mauricio.sync.model.client.ISyncClientListener;
import com.mauricio.sync.model.client.SyncClient;
import com.mauricio.sync.view.client.ClientLauncherWindow;
import com.mauricio.sync.view.client.ClientWindow;
import com.mauricio.sync.view.client.IClientLauncherWindowListener;
import com.mauricio.sync.view.client.IClientWindowListener;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author Mauricio Rumštajn
 */
public class ClientAppController extends Application {

    /**
     * Main entry point of the client app JavaFX application.
     *
     * @param primaryStage the newly created root stage
     * @throws Exception
     * @see Application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientLauncherWindow launcherWindow = new ClientLauncherWindow();
        launcherWindow.setListener(new IClientLauncherWindowListener() {
            @Override
            public void onConnect(String ip, int port, String password) {
                SyncClient client = new SyncClient(ip, port, System.getProperty("user.name"),
                        password, "json_packet_parser");

                launcherWindow.close();

                ClientWindow clientWindow = new ClientWindow();
                clientWindow.setListener(new IClientWindowListener() {
                    @Override
                    public void onSetSyncDir(File dir) {
                        client.setObservedDir(dir);
                    }

                    @Override
                    public void onDownloadFile(String file, boolean isDir, String host) {
                        try {
                            client.downloadFile(file, host, isDir);
                        } catch (IOException e) {
                            clientWindow.showErrorDialog("Unable to download file " + file + " from client " + host);
                        }
                    }

                    @Override
                    public void onWindowClose() {
                        // unregister all files in client cache and close client
                        try {
                            client.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                client.addListener(new ISyncClientListener() {
                    @Override
                    public void onConnect(String ip, int port) {
                        clientWindow.setIpLabelText(ip + ":" + port + " (Not authenticated)");
                    }

                    @Override
                    public void onDisconnect(String ip, int port) {
                        clientWindow.setIpLabelText("-");
                    }

                    @Override
                    public void onAuthenticated(boolean status) {
                        System.out.println("AUTH");
                        if (!status){
                            return;
                        }
                        System.out.println("AUTH");
                        clientWindow.setIpLabelText(ip + ":" + port + " (Authenticated)");
                        try {
                            System.out.println("fetching file list");
                            client.fetchFileList();
                        } catch (IOException e) {
                            clientWindow.showErrorDialog("Unable to fetch list of files on the server");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFileAdded(String filename, String host, boolean isDir) {
                        // this event is handled by the observer
                    }

                    @Override
                    public void onFileRemoved(String filename, String host, boolean isDir) {
                        // this event is handled by the observera
                    }

                    @Override
                    public void onFileSyncStarted(String filename, boolean isDir) {
                        clientWindow.showInfoDialog("Download file " + filename + "...");
                    }

                    @Override
                    public void onFileSyncCompleted(String filename, boolean isDir) {
                        clientWindow.showInfoDialog("Download complete for file " + filename);
                    }

                    @Override
                    public void onServerFileListed(String filename, String host, boolean isDir) {
                        clientWindow.addFileToList(filename, host, isDir);
                    }

                    @Override
                    public void onServerFileUnlisted(String filename, String host, boolean isDir) {
                        clientWindow.removeFileFromList(filename, host, isDir);
                    }

                    @Override
                    public void onReceivedListPacket() {
                        clientWindow.clearFileList();
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            client.connect();
                        } catch (IOException | InterruptedException e){
                            launcherWindow.showErrorDialog("Failed to connect to the server");
                            return;
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * Starts the application.
     */
    public void start(){
        launch();
    }
}
