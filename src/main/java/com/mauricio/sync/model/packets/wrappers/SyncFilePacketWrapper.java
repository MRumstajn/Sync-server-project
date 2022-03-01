package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

public class SyncFilePacketWrapper extends PacketWrapper {

    public SyncFilePacketWrapper(IPacket packet) {
        super(packet);
        put("type", "sync");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("path") || !containsKey("is_dir")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("sync")){
            return false;
        }
        Object path = get("path");
        if (!(path instanceof String)){
            return false;
        }
        if (((String) path).length() == 0){
            return false;
        }
        Object isDir = get("is_dir");
        if (!(isDir instanceof Boolean)){
            return false;
        }
        return true;
    }

    public String getPath(){
        return (String) get("path");
    }

    public void setPath(String path){
        put("path", path);
    }

    public void setIsDir(boolean status){
        put("is_dir", status);
    }

    public boolean isDir(){
        return (boolean) get("is_dir");
    }
}
