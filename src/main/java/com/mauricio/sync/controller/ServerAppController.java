package com.mauricio.sync.controller;

import com.mauricio.sync.model.server.SyncServer;
import com.mauricio.sync.view.server.IServerLauncherWindowListener;
import com.mauricio.sync.view.server.IServerWindowListener;
import com.mauricio.sync.view.server.ServerLauncherWindow;
import com.mauricio.sync.view.server.ServerWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class ServerAppController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
            ServerLauncherWindow launcherWindow = new ServerLauncherWindow();

            launcherWindow.setListener(new IServerLauncherWindowListener() {
                @Override
                public void onStart(int port, boolean usePassword, String password) {
                    launcherWindow.close();
                    System.out.println(password);
                    //SyncServer server = new SyncServer(port, "json_packet_parser", password);
                    ServerWindow serverWindow = new ServerWindow();
                    serverWindow.setListener(new IServerWindowListener() {
                        @Override
                        public void onStopButtonClicked() {
                            System.out.println("stop btn clicked");
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
                }
            });
    }

    public void start(){
        launch();
    }
}
