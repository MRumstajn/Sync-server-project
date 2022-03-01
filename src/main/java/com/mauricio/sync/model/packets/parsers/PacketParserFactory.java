package com.mauricio.sync.model.packets.parsers;

public class PacketParserFactory {

    public static IPacketParser createParser(String type){
        switch (type){
            case "json_packet_parser":
                return new JSONPacketParser();
            default:
                return null;
        }
    }
}
