package com.mauricio.sync.model.packets.wrappers;

import com.mauricio.sync.model.packets.IPacket;

/**
 *
 * @author Mauricio Rumštajn
 */
public class PacketWrapperFactory {

    /**
     * Produces a {@link PacketWrapper} that wraps the specified packet class.
     *
     * @param type id of packet.
     * @param packetClass packet class to wrap.
     * @return the packet wrapper or null if type is invalid.
     */
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
                case "sync":
                    wrapper = new SyncFilePacketWrapper(packetClass.newInstance());
                    break;
                case "sync_data":
                    wrapper = new SyncDataPacketWrapper(packetClass.newInstance());
                    break;
                case "add_files":
                    wrapper = new AddFilesPacketWrapper(packetClass.newInstance());
                    break;
                case "remove_files":
                    wrapper = new RemoveFilesPacketWrapper(packetClass.newInstance());
                    break;
                case "list_files":
                    wrapper = new ListFilesPacketWrapper(packetClass.newInstance());
                    break;
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return wrapper;
    }
}
