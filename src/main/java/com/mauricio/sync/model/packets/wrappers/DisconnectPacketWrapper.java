package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

public class DisconnectPacketWrapper extends PacketWrapper {

    public DisconnectPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "disconnect");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("disconnect")){
            return false;
        }
        return true;
    }
}
