package com.mauricio.sync.packets;

import org.json.simple.JSONObject;

public class JSONPacket implements IPacket{
    private JSONObject obj;

    public JSONPacket(JSONObject obj){
        this.obj = obj;
    }

    public JSONPacket(){
        obj = new JSONObject();
    }

    @Override
    public Object get(Object key) {
        return obj.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        obj.put(key, value);
    }

    @Override
    public boolean containsKey(Object key) {
        return obj.containsKey(key);
    }

    @Override
    public String stringify() {
        return obj.toJSONString();
    }
}
