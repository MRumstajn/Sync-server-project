package com.mauricio.sync.packets.wrappers;

import com.mauricio.sync.packets.IPacket;

import java.util.List;

public class AddFilesPacketWrapper extends PacketWrapper{

    public AddFilesPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "add_files");
    }

    @Override
    public boolean validate() {
        if (!containsKey("type") || !containsKey("files")){
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)){
            return false;
        }
        if (!type.equals("add_files")){
            return false;
        }
        Object files = get("files");
        if (!(files instanceof List)){
            return false;
        }
        if (((List<String>) files).size() == 0){
            return false;
        }
        return true;
    }

    public void setFiles(List<String> files){
        put("files", files);
    }

    public List<String> getFiles(){
        return (List<String>) get("files");
    }
}
