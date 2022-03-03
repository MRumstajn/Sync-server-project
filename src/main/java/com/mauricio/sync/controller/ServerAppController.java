package com.mauricio.sync.controller;

import com.mauricio.sync.model.server.ISyncServerListener;
import com.mauricio.sync.model.server.SyncClientDevice;
import com.mauricio.sync.model.server.SyncServer;
import com.mauricio.sync.view.server.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerAppController extends Application {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start(Stage primaryStage) throws Exception {
            ServerLauncherWindow launcherWindow = new ServerLauncherWindow();

            launcherWindow.setListener(new IServerLauncherWindowListener() {
                @Override
                public void onStart(int port, boolean usePassword, String password) {
                    launcherWindow.close();

                    SyncServer server = new SyncServer(port, "json_packet_parser", password);
                    ServerLogWindow logWindow = new ServerLogWindow();
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
                            logWindow.initOwner(serverWindow);
                            logWindow.show();
                        }

                        @Override
                        public void onSettingsButtonClicked() {
                            new ServerSettingsWindow().setListener(new IServerSettingsWindowListener() {
                                @Override
                                public void onApply(boolean usePassword, String newPassword) {
                                    if (usePassword) {
                                        System.out.println("new psw " + newPassword);
                                        server.setIsUsingPassword(true);
                                        server.setPassword(newPassword);
                                    } else {
                                        server.setIsUsingPassword(true);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onClientDisconnectButtonClicked(String username) {
                            server.removeDevice(server.getDeviceWithUsername(username));
                        }
                    });

                    server.addListener(new ISyncServerListener() {
                        @Override
                        public void onServerStart() {
                            serverWindow.setPortLabel(String.valueOf(server.getPort()));
                            serverWindow.setStatusLabel("Online");
                            logWindow.appendToConsole(getTimedText("server started"));
                        }

                        @Override
                        public void onServerStop() {
                            serverWindow.setStatusLabel("Offline");
                            logWindow.appendToConsole(getTimedText("server stopped"));
                        }

                        @Override
                        public void onClientConnect(SyncClientDevice client) {
                            serverWindow.setClientCountLabel(String.valueOf(server.getConnectedClientCount()));
                            logWindow.appendToConsole(getTimedText("new connection from " +
                                    client.getClientSocket().getInetAddress()));
                        }

                        @Override
                        public void onClientDisconnect(SyncClientDevice client) {
                            serverWindow.removeClientFromList(client.getName());
                            serverWindow.setClientCountLabel(String.valueOf(server.getConnectedClientCount()));
                            logWindow.appendToConsole(getTimedText("client " + client.getClientSocket().getInetAddress() + " disconnected"));
                        }

                        @Override
                        public void onClientAuthenticated(SyncClientDevice client) {
                            String ip = client.getClientSocket().getInetAddress().toString();
                            int port = client.getClientSocket().getPort();
                            serverWindow.addClientToList(client.getName(), ip, port);
                            logWindow.appendToConsole(getTimedText("client " + client.getClientSocket().getInetAddress()
                                    + " authenticated as " + client.getName()));
                        }

                        @Override
                        public void onClientSetName(SyncClientDevice client) {
                            logWindow.appendToConsole(getTimedText("set name for client " + client.getClientSocket().getInetAddress()
                                    + " to " + client.getName()));
                        }

                        @Override
                        public void onAddFile(SyncClientDevice client, String filename, boolean isDir) {
                            serverWindow.addFileToList(filename, client.getName(), isDir);
                            String whatWasAdded = "file";
                            if (isDir){
                                whatWasAdded = "folder";
                            }
                            logWindow.appendToConsole(getTimedText("client " + client.getName() + " added " + whatWasAdded
                                    + " " + filename));
                        }

                        @Override
                        public void onRemoveFile(SyncClientDevice client, String filename, boolean isDir) {
                            serverWindow.removeFileFromList(filename, client.getName(), isDir);
                            String whatWasAdded = "file";
                            if (isDir){
                                whatWasAdded = "folder";
                            }
                            logWindow.appendToConsole(getTimedText("client " + client.getName() + " removed " + whatWasAdded
                                    + " " + filename));
                        }

                        @Override
                        public void onSyncStart(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            logWindow.appendToConsole(getTimedText("Sync started, client " + cl1.getName() + " sending file/folder " +
                                    "to client " + cl2.getName()));
                        }

                        @Override
                        public void onSyncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            logWindow.appendToConsole(getTimedText("Sync completed, client " + cl1.getName() + " sent file/folder " +
                                    "to client " + cl2.getName()));
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

    private String getTimedText(String text){
        LocalDateTime time = LocalDateTime.now();
        return "[" + time.format(dtf) + "] " + text;
    }
}
