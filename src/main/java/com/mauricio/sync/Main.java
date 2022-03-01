package com.mauricio.sync;

import com.mauricio.sync.client.SyncClient;
import com.mauricio.sync.packets.JSONPacket;
import com.mauricio.sync.packets.wrappers.PacketWrapperFactory;
import com.mauricio.sync.packets.wrappers.SyncFilePacketWrapper;
import com.mauricio.sync.server.ISyncServerListener;
import com.mauricio.sync.server.SyncClientDevice;
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
                    SyncServer server = new SyncServer(10000,"json_packet_parser", "");
                    server.addListener(new ISyncServerListener() {
                        @Override
                        public void onServerStart() {
                            System.out.println("Server started");
                        }

                        @Override
                        public void onServerStop() {
                            System.out.println("Server stopped");
                        }

                        @Override
                        public void onClientConnect(SyncClientDevice client) {
                            System.out.println("New connection");
                        }

                        @Override
                        public void onClientDisconnect(SyncClientDevice client) {
                            System.out.println("Client #" + client.getId() + " disconnected");
                        }

                        @Override
                        public void onClientAuthenticated(SyncClientDevice client) {
                            System.out.println("Client #" + client.getId() + " authenticated with username "
                                    + client.getName());
                        }

                        @Override
                        public void onClientSetName(SyncClientDevice client) {
                            System.out.println("Client #" + client.getId() + " is named " + client.getName());
                        }

                        @Override
                        public void onAddFile(SyncClientDevice client, String filename, boolean isDir) {
                            System.out.println("client " + client.getName() + " added file " + filename);
                            System.out.println("\tfolder: " + isDir);
                        }

                        @Override
                        public void onRemoveFile(SyncClientDevice client, String filename, boolean isDir) {
                            System.out.println("client " + client.getName() + " removed file " + filename);
                            System.out.println("\tfolder: " + isDir);
                        }

                        @Override
                        public void onSyncStart(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            System.out.println("relaying file " + file + " from client " + cl1.getName()
                                    + " to client " + cl2.getName() );
                        }

                        @Override
                        public void onSyncCompleted(String file, SyncClientDevice cl1, SyncClientDevice cl2) {
                            System.out.println("finished relaying file " + file + " from client " + cl1.getName()
                                    + " to client " + cl2.getName() );
                        }
                    });
                    server.start();
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
