package com.mauricio.sync.controller;

import com.mauricio.sync.view.client.ClientLauncherWindow;
import com.mauricio.sync.view.client.ClientWindow;
import com.mauricio.sync.view.client.IClientLauncherWindowListener;
import com.mauricio.sync.view.client.IClientWindowListener;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class ClientAppController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientLauncherWindow launcherWindow = new ClientLauncherWindow();
        launcherWindow.setListener(new IClientLauncherWindowListener() {
            @Override
            public void onConnect(String ip, int port, String password) {
                launcherWindow.close();
                ClientWindow clientWindow = new ClientWindow();
                clientWindow.addFileToList("data.txt", "maurek-PC", false);
                clientWindow.addFileToList("data.txt", "maurek-PC", true);
                clientWindow.setListener(new IClientWindowListener() {
                    @Override
                    public void onSetSyncDir(File dir) {
                        System.out.println("new observable dir is " + dir.getAbsolutePath());
                    }

                    @Override
                    public void onDownloadFile(String file, boolean isDir, String host) {
                        System.out.println("Downloading file " + file + "...");
                        System.out.println("\t folder: " + isDir);
                        System.out.println("\t host: " + host);
                    }
                });
            }
        });
    }

    public void start(){
        launch();
    }
}
