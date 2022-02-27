package com.mauricio.sync.packets.wrappers;

import com.mauricio.sync.packets.IPacket;

public class PacketWrapperFactory {

    public static PacketWrapper createPacketWrapper(String type, Class<? extends IPacket> packetClass){
        PacketWrapper wrapper = null;
        try {
            switch (type){
                case "ping":
                    wrapper = new PingPacketWrapper(packetClass.newInstance());
                    break;
                case "disconnect":
                    wrapper = new DisconnectPacketWrapper(packetClass.newInstance());
                    break;
                case "error":
                    wrapper = new ErrorPacketWrapper(packetClass.newInstance());
                    break;
                case "auth":
                    wrapper = new AuthPacketWrapper(packetClass.newInstance());
                    break;
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return wrapper;
    }
}
