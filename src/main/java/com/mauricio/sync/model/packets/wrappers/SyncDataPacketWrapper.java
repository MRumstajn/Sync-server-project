package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * Wrapper used to send file data.
 *
 * @author Mauricio Rumštajn
 */
public class SyncDataPacketWrapper extends PacketWrapper {

    public SyncDataPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "sync_data");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Get sync data.
     *
     * @return (String) base64 encoded byte array
     */
    public String getData(){
        return (String) get("data");
    }

    /**
     * Set sync data.
     *
     * @param data (String) base64 encoded byte array
     */
    public void setData(String data){
        put("data", data);
    }

    /**
     * Set file path.
     *
     * @param path
     */
    public void setPath(String path){
        put("path", path);
    }

    /**
     * Get file path.
     *
     * @return path
     */
    public String getPath(){
        return (String) get("path");
    }
}
