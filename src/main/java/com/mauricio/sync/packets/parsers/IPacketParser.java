package com.mauricio.sync.packets.parsers;

import com.mauricio.sync.packets.IPacket;

public interface IPacketParser<T extends IPacket> {
    T parse(String rawPacket);
    Class<T> getPacketClass();
}
