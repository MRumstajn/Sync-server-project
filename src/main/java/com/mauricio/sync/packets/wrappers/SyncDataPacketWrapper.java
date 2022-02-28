package com.mauricio.sync.packets.wrappers;

import com.mauricio.sync.packets.IPacket;

public class SyncDataPacketWrapper extends PacketWrapper {

    public SyncDataPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "sync_data");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("path")|| !containsKey("data")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("sync_data")){
            return false;
        }
        Object data = get("data");
        if (!(data instanceof String)){
            return false;
        }
        Object path = get("path");
        if (!(path instanceof String)){
            return false;
        }
        if (((String) path).length() == 0){
            return false;
        }
        return true;
    }

    public String getData(){
        return (String) get("data");
    }

    public void setData(String data){
        put("data", data);
    }

    public void setPath(String path){
        put("path", path);
    }

    public String getPath(){
        return (String) get("path");
    }
}
