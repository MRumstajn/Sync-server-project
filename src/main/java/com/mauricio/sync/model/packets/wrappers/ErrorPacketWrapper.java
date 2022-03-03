package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * Wrapper used to send error.
 *
 * @author Mauricio Rumštajn
 */
public class ErrorPacketWrapper extends PacketWrapper {

    public ErrorPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "error");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Set error message.
     *
     * @param msg
     */
    public void setErrorMsg(String msg){
        put("msg", msg);
    }

    /**
     * Get error message.
     *
     * @return msg
     */
    public String getErrorMsg(){
        return (String) get("msg");
    }
}
