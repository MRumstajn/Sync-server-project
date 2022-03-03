package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper used to request and list registered files from the server.
 *
 * @author Mauricio Rumštajn
 */
public class ListFilesPacketWrapper extends PacketWrapper {

    public ListFilesPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "list_files");
        /* data structure of "files"
        * filename: {host: isDir}
        * */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate() {
        if (!containsKey("type")) {
            return false;
        }
        Object type = get("type");
        if (!(type instanceof String)) {
            return false;
        }
        if (!type.equals("list_files")) {
            return false;
        }
        Object files = get("files");
        if (files != null) {
            if (!(files instanceof List)) {
                return false;
            }
            List<Object> arr = (List<Object>) files;
            for (Object o : arr) {
                if (!(o instanceof Map)) {
                    return false;
                }
                for (Object key : ((Map<?, ?>) o).keySet()) {
                    if (!(key instanceof String)) {
                        return false;
                    }
                }
                Map<String, Object> fileObj = (Map<String, Object>) o;
                for (Object data : fileObj.values()){
                    if (!(data instanceof Map)){
                        return false;
                    }
                    Map<Object, Object> dataMap = (Map<Object, Object>) data;
                    for (Object key : dataMap.keySet()){
                        if (!(key instanceof String) || !(dataMap.get(key) instanceof Boolean)){
                            return false;
                        }
                    }
                }
                if (!fileObj.containsKey("path") || !fileObj.containsKey("is_dir")) {
                    return false;
                }
                Object path = fileObj.get("path");
                if (!(path instanceof String)) {
                    return false;
                }
                if (((String) path).length() == 0) {
                    return false;
                }
                Object isDir = fileObj.get("is_dir");
                if (!(isDir instanceof Boolean)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Add file to list.
     *
     * @param path path to file.
     * @param host host of file.
     * @param isDir is file a dir.
     */
    public void addFile(String path, String host, boolean isDir) {
        List<Map<String, Object>> fileList = getFiles();
        if (fileList == null){
            fileList = new ArrayList<>();
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("path", path);
        dataMap.put("host", host);
        dataMap.put("is_dir", isDir);
        fileList.add(dataMap);
        put("files", fileList);
    }

    /**
     * Get file list.
     *
     * @return list
     */
    public List<Map<String, Object>> getFiles() {
        return (List<Map<String, Object>>) get("files");
    }
}
