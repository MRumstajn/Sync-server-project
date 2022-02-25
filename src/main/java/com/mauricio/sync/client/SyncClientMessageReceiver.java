package com.mauricio.sync.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncClientMessageReceiver implements Runnable{
    private Socket clientSocket;

    public SyncClientMessageReceiver(Socket clientSocketSocket){
        this.clientSocket = clientSocketSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            while (!clientSocket.isClosed()){
                String msg = in.readUTF();
                System.out.println("Client received: " + msg);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
