package com.mauricio.sync;

import com.mauricio.sync.client.SyncClient;
import com.mauricio.sync.packets.parsers.JSONPacketParser;
import com.mauricio.sync.server.SyncServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new SyncServer(10000, JSONPacketParser.class, "abc").start();
                } catch (IOException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            new SyncClient("localhost", 10000, "client #1", "abc",
                    JSONPacketParser.class).connect();
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
