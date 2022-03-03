package com.mauricio.sync.model.packets.parsers;

/**
 * @author Mauricio Rumštajn
 */
public class PacketParserFactory {

    /**
     * Create a {@link IPacketParser} based on the input string.
     *
     * @param type name/type of packet parser to create.
     * @return the packet parser or null if the type is invalid.
     */
    public static IPacketParser createParser(String type){
        switch (type){
            case "json_packet_parser":
                return new JSONPacketParser();
            default:
                return null;
        }
    }
}
