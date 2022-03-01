package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

public class AuthPacketWrapper extends PacketWrapper {

    public AuthPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "auth");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("username") || !containsKey("password") || !containsKey("status")){
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
        Object status = get("status");
        if (!(status instanceof Boolean)){
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

    public void setUsername(String username){
        put("username", username);
    }

    public void setPassword(String password){
        put("password", password);
    }

    public void setStatus(boolean status){
        put("status", status);
    }

    public boolean getStatus(){
        return (boolean) get("status");
    }
}
