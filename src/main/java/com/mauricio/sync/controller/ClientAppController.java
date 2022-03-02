package com.mauricio.sync.controller;

import com.mauricio.sync.view.client.ClientLauncherWindow;
import com.mauricio.sync.view.client.IClientLauncherWindowListener;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientAppController extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ClientLauncherWindow().setListener(new IClientLauncherWindowListener() {
            @Override
            public void onConnect(String ip, int port, String password) {
                System.out.println(ip);
                System.out.println(port);
                System.out.println(password);
            }
        });
    }

    public void start(){
        launch();
    }
}
