package com.mauricio.sync;

import com.mauricio.sync.controller.ServerAppController;

public class MainServer {

    public static void main(String[] args) {
        new ServerAppController().start();
    }
}
