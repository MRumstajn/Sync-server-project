package com.mauricio.sync.controller;

import com.mauricio.sync.view.server.IServerLauncherWindowListener;
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
                    // TODO start server window
                }
            });
    }

    public void start(){
        launch();
    }
}
