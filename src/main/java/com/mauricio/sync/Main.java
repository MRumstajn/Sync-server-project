package com.mauricio.sync;

import com.mauricio.sync.client.SyncClient;
import com.mauricio.sync.packets.JSONPacket;
import com.mauricio.sync.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.packets.wrappers.SyncFilePacketWrapper;
import com.mauricio.sync.server.SyncServer;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Main {

    public static void main(String[] args) {
       new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new SyncServer(10000,"json_packet_parser", "").start();
                } catch (IOException | InvalidParameterException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // client #1
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncClient client = new SyncClient("localhost", 10000, "client #1", "",
                            "json_packet_parser");
                    client.setObservedDir(new File("C:/Users/mauri/Desktop/files"));
                    client.connect();

                    // testing file relaying system
                    /*SyncFilePacketWrapper testPacket = (SyncFilePacketWrapper)
                            PacketWrapperFactory.createPacketWrapper("sync", JSONPacket.class);
                    testPacket.setPath("data_v2.txt");
                    testPacket.setIsDir(false);
                    // give the other client some time to connect and register its files on the server
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.sendPacket(testPacket);*/
                    //

                } catch (IOException | InvalidParameterException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // client #2
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncClient client = new SyncClient("localhost", 10000, "client #2", "",
                            "json_packet_parser");
                    client.setObservedDir(new File("C:/Users/mauri/Desktop/files2"));
                    client.connect();

                    // testing file relaying system
                    SyncFilePacketWrapper testPacket = (SyncFilePacketWrapper)
                            PacketWrapperFactory.createPacketWrapper("sync", JSONPacket.class);
                    testPacket.setPath("New folder");
                    testPacket.setIsDir(true);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.sendPacket(testPacket);
                } catch (IOException | InvalidParameterException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
