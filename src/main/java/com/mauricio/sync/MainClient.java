package com.mauricio.sync;

import com.mauricio.sync.controller.ClientAppController;
import com.mauricio.sync.model.client.SyncClient;

import java.io.IOException;

public class MainClient {

    public static void main(String[] args) {
        new ClientAppController().start();
    }


}
