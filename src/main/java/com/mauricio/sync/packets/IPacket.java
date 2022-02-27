package com.mauricio.sync.packets;

public interface IPacket {
    Object get(Object key);
    void put(Object key, Object value);
    boolean containsKey(Object key);
    String stringify();
}
