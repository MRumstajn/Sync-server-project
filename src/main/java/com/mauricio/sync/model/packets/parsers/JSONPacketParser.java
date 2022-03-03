package com.mauricio.sync.model.packets.parsers;

import com.mauricio.sync.model.packets.JSONPacket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Mauricio Rumštajn
 */
public class JSONPacketParser implements IPacketParser<JSONPacket>{
    private JSONParser parser;

    public JSONPacketParser(){
        parser = new JSONParser();
    }


    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JSONPacket> getPacketClass() {
        return JSONPacket.class;
    }
}
