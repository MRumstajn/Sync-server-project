package com.mauricio.sync.server;

import java.io.IOException;
import java.util.List;

public interface ISyncServer {
    void start() throws IOException;
    void stop() throws IOException;
    int getPort();
    List<SyncClientDevice> getClients();
}
