package com.mauricio.sync.client;

import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.parsers.IPacketParser;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class SyncClientMessageReceiver implements Runnable{
    private ISyncClient client;
    private Socket clientSocket;
    private IPacketParser packetParser;

    @SuppressWarnings("deprecation")
    public SyncClientMessageReceiver(ISyncClient client, Socket clientSocketSocket, IPacketParser packetParser) {
        this.client = client;
        this.clientSocket = clientSocketSocket;
        this.packetParser = packetParser;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            while (!clientSocket.isClosed()){
                String rawPacket = in.readUTF();
                IPacket packet = packetParser.parse(rawPacket);
                String type = (String) packet.get("type");
                switch (type){
                    case "ping":
                        // received reply
                        System.out.println("Server responded to ping");
                        break;
                    case "disconnect":
                        // server is disconnecting client
                        client.disconnect();
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
