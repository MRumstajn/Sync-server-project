package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * Packet wrapper base class.
 * Provides validation and basic packet functionality.
 * Meant to be extended by custom packet wrapper classes
 *
 * @author Mauricio Rumštajn
 */
public abstract class PacketWrapper implements IPacket {
    private IPacket packet;

    public PacketWrapper(IPacket packet){
        this.packet = packet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return packet.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Object key, Object value) {
        packet.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return packet.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String stringify() {
        return packet.stringify();
    }

    /**
     * Validate the integrity of the packet.
     *
     * @return true for valid, false for invalid.
     */
    public abstract boolean validate();
}
