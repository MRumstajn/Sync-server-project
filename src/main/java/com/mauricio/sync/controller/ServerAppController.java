package com.mauricio.sync.controller;

import com.mauricio.sync.model.server.ISyncServerListener;
import com.mauricio.sync.model.server.SyncClientDevice;
import com.mauricio.sync.model.server.SyncServer;
import com.mauricio.sync.view.server.IServerLauncherWindowListener;
import com.mauricio.sync.view.server.IServerWindowListener;
import com.mauricio.sync.view.server.ServerLauncherWindow;
import com.mauricio.sync.view.server.ServerWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketAddress;

public class ServerAppController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
            ServerLauncherWindow launcherWindow = new ServerLauncherWindow();

            launcherWindow.setListener(new IServerLauncherWindowListener() {
                @Override
                public void onStart(int port, boolean usePassword, String password) {
                    launcherWindow.close();

                    SyncServer server = new SyncServer(port, "json_packet_parser", password);

                    ServerWindow serverWindow = new ServerWindow();
                    SyncServer finalServer = server;
                    serverWindow.setListener(new IServerWindowListener() {
                        @Override
                        public void onStopButtonClicked() {
                            try {
                                finalServer.stop();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLogButtonClicked() {
                            System.out.println("log btn clicked");
                        }

                        @Override
                        public void onSettingsButtonClicked() {
                            System.out.println("settings btn clicked");
                        }
                    });

                    server.addListener(new ISyncServerListener() {
                        @Override
                        public void onServerStart() {
                            serverWindow.setStatusLabel("Online");
                        }

                        @Override
                        public void onServerStop() {
                            serverWindow.setStatusLabel("Offline");
                        }

                        @Override
                        public void onClientConnect(SyncClientDevice client) {
                            // ignoring this event for now
                        }

                        @Override
                        public void onClientDisconnect(SyncClientDevice client) {
                            serverWindow.removeClientFromList(client.getName());
                        }

                        @Override
                        public void onClientAuthenticated(SyncClientDevice client) {
                            String ip = client.getClientSocket().getInetAddress().toString();
                            int port = client.getClientSocket().getPort();
                            serverWindow.addClientToList(client.getName(), ip, port);
                        }

                        @Override
                        public void onClientSetName(SyncClientDevice client) {
                            // ignoring this event for now
                        }

                        @Override
                        public void onAddFile(SyncClientDevice client, String filename, boolean isDir) {
                            serverWindow.addFileToList(filename, client.getName(), isDir);
                        }

                        @Override
                        public void onRemoveFile(SyncClientDevice client, String filename, boolean isDir) {
                            serverWindow.removeFileFromList(filename, client.getName(), isDir);
                        }

                        @Override
                        public void onSyncStart(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            // ignoring this event for now
                        }

                        @Override
                        public void onSyncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            // ignoring this event for now
                        }
                    });
                    SyncServer finalServer1 = server;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                finalServer1.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
    }

    public void start(){
        launch();
    }
}
