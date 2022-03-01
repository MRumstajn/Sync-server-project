package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

public class PingPacketWrapper extends PacketWrapper {

    public PingPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "ping");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("is_request")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("ping")){
            return false;
        }
        Object isRequest = get("is_request");
        if (!(isRequest instanceof Boolean)){
            return false;
        }
        return true;
    }

    public boolean isRequest(){
        return (boolean) get("is_request");
    }

    public void setIsRequest(boolean status){
        put("is_request", status);
    }
}
