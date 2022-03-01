package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

public abstract class PacketWrapper implements IPacket {
    private IPacket packet;

    public PacketWrapper(IPacket packet){
        this.packet = packet;
    }

    @Override
    public Object get(Object key) {
        return packet.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        packet.put(key, value);
    }

    @Override
    public boolean containsKey(Object key) {
        return packet.containsKey(key);
    }

    @Override
    public String stringify() {
        return packet.stringify();
    }

    public abstract boolean validate();
}
