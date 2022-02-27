package com.mauricio.sync.packets.wrappers;

import com.mauricio.sync.packets.IPacket;
import com.mauricio.sync.packets.wrappers.PacketWrapper;

public class ErrorPacketWrapper extends PacketWrapper {

    public ErrorPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "error");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("msg")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("error")){
            return false;
        }
        Object msg = get("msg");
        if (!(msg instanceof String)){
            return false;
        }
        return true;
    }

    public void setErrorMsg(String msg){
        put("msg", msg);
    }

    public String getErrorMsg(){
        return (String) get("msg");
    }
}
