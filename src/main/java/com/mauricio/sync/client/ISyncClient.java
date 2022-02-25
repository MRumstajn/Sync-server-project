package com.mauricio.sync.client;

import java.io.IOException;

public interface ISyncClient {
    void connect() throws IOException;
    void disconnect() throws IOException;
    void send(String msg) throws IOException;
    String getServerIP();
    int getServerPort();
}
