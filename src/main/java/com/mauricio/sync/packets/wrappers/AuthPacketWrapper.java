package com.mauricio.sync.packets.wrappers;

import com.mauricio.sync.packets.IPacket;

public class AuthPacketWrapper extends PacketWrapper {

    public AuthPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "auth");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("username") || !containsKey("password")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("auth")){
            return false;
        }
        Object username = get("username");
        if (!(username instanceof String)){
            return false;
        }
        Object password = get("password");
        if (!(password instanceof String)){
            return false;
        }
        return true;
    }

    public String getUsername(){
        return (String) get("username");
    }

    public String getPassword(){
        return (String) get("password");
    }
}
