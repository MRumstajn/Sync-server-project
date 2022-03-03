package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * Wrapper used to ping the server (for testing only).
 *
 * @author Mauricio Rumštajn
 */
public class PingPacketWrapper extends PacketWrapper {

    public PingPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "ping");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Get if this is a request or response.
     * @return status
     */
    public boolean isRequest(){
        return (boolean) get("is_request");
    }

    /**
     * Set if this is a request or response.
     * @param  status
     */
    public void setIsRequest(boolean status){
        put("is_request", status);
    }
}
