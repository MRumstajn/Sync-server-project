package com.mauricio.sync.model.packets.parsers;

import com.mauricio.sync.model.packets.IPacket;

public interface IPacketParser<T extends IPacket> {
    T parse(String rawPacket);
    Class<T> getPacketClass();
}
