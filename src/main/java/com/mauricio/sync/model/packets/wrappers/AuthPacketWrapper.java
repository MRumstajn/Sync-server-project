package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 * Wrapper used to authenticate client and send authentication response.
 *
 * @author Mauricio Rumštajn
 */
public class AuthPacketWrapper extends PacketWrapper {

    public AuthPacketWrapper(IPacket packet) {
        super(packet);
        put("type", "auth");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Get client username.
     *
     * @return username
     */
    public String getUsername(){
        return (String) get("username");
    }

    /**
     * Get client password.
     *
     * @return password
     */
    public String getPassword(){
        return (String) get("password");
    }

    /**
     * Set client username.
     *
     * @param username
     */
    public void setUsername(String username){
        put("username", username);
    }

    /**
     * Set client password.
     *
     * @param password
     */
    public void setPassword(String password){
        put("password", password);
    }

    /**
     * Set authentication status (used by server)
     *
     * @param status whether authentication was successful.
     */
    public void setStatus(boolean status){
        put("status", status);
    }

    /**
     * Set authentication status (used by client).
     *
     * @return whether authentication was successful.
     */
    public boolean getStatus(){
        return (boolean) get("status");
    }
}
