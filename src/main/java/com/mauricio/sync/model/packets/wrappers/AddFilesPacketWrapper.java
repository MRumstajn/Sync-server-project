package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Object> arr = (List<Object>) files;
        if (arr.size() == 0){
            return false;
        }
        for (Object o : arr) {
            if (!(o instanceof Map)){
                return false;
            }
            for (Object key : ((Map<?, ?>) o).keySet()){
                if (!(key instanceof String)){
                    return false;
                }
            }
            Map<String, Object> fileObj = (Map<String, Object>) o;
            if (!fileObj.containsKey("path") || !fileObj.containsKey("is_dir")){
                return false;
            }
            Object path = fileObj.get("path");
            if (!(path instanceof String)){
                return false;
            }
            if (((String) path).length() == 0){
                return false;
            }
            Object isDir = fileObj.get("is_dir");
            if (!(isDir instanceof Boolean)){
                return false;
            }
        }
        return true;
    }

    public void addFile(String path, boolean isDir){
        List<Map<String, Object>> fileList = getFiles();
        if (fileList == null){
            fileList = new ArrayList<>();
        }
        Map<String, Object> fileMap = new HashMap<>();
        fileMap.put("path", path);
        fileMap.put("is_dir", isDir);
        fileList.add(fileMap);
        put("files", fileList);
    }

    public List<Map<String, Object>> getFiles(){
        return (List<Map<String, Object>>) get("files");
    }
}
