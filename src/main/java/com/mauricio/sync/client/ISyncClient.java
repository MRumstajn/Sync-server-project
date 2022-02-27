package com.mauricio.sync.client;

import com.mauricio.sync.packets.IPacket;

import java.io.IOException;

public interface ISyncClient {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void sendPacket(IPacket packet) throws IOException;
    String getServerIP();
    int getServerPort();
}
