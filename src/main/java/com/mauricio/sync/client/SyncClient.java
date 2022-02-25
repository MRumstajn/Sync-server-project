package com.mauricio.sync.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SyncClient implements ISyncClient{
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String ip;
    private int port;

    public SyncClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
        Scanner scanner = new Scanner(System.in);
        new Thread(new SyncClientMessageReceiver(clientSocket)).start();
        while (true){
            System.out.print("Send: ");
            String line = scanner.nextLine();
            send(line);
            if (line.equals("exit")){
                break;
            }
        }
    }

    @Override
    public void disconnect() throws IOException {
        clientSocket.close();
    }

    @Override
    public void send(String msg) throws IOException{
        out.writeUTF(msg);
    }

    @Override
    public String getServerIP() {
        return ip;
    }

    @Override
    public int getServerPort() {
        return port;
    }
}
