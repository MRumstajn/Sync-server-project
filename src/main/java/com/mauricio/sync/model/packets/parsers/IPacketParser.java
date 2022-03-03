package com.mauricio.sync.model.packets.parsers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * @author Mauricio Rumštajn
 */
public interface IPacketParser<T extends IPacket> {
    /**
     * Parse raw packet.
     *
     * @param rawPacket
     * @return a packet of type T
     */
    T parse(String rawPacket);
    /**
     * Gets the packet class that this parser supports.
     *
     * @return the packet class
     */
    Class<T> getPacketClass();
}
