package com.mauricio.sync.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncClientDeviceHandler implements Runnable{
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SyncClientDeviceHandler(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            while (!client.isClosed()){
                String msg = in.readUTF();
                out.writeUTF(msg);
                if (msg.equals("exit")){
                    client.close();
                }
            }
            System.out.println("client " + client.getRemoteSocketAddress() + " disconnected");
        } catch (IOException e){
            e.printStackTrace();
        }


    }
}
