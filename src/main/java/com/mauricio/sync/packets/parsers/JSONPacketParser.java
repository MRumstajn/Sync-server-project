package com.mauricio.sync.packets.parsers;

import com.mauricio.sync.packets.JSONPacket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONPacketParser implements IPacketParser<JSONPacket>{
    private JSONParser parser;

    public JSONPacketParser(){
        parser = new JSONParser();
    }

    @Override
    public JSONPacket parse(String rawPacket) {
        JSONPacket packet = null;
        try {
            JSONObject obj = (JSONObject) parser.parse(rawPacket);
            packet = new JSONPacket(obj);
        } catch (ParseException e){
        }
        return packet;
    }

    @Override
    public Class<JSONPacket> getPacketClass() {
        return JSONPacket.class;
    }
}
