package com.mauricio.sync.model.packets;

import org.json.simple.JSONObject;

/**
 * {@link IPacket} implementation based on the JSON format.
 *
 * @author Mauricio Rumštajn
 */
public class JSONPacket implements IPacket{
    private JSONObject obj;

    public JSONPacket(JSONObject obj){
        this.obj = obj;
    }

    public JSONPacket(){
        obj = new JSONObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return obj.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Object key, Object value) {
        obj.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return obj.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String stringify() {
        return obj.toJSONString();
    }
}
