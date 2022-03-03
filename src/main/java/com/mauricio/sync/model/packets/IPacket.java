package com.mauricio.sync.model.packets;

/**
 * Wrapper used to request files from the server.
 *
 * @author Mauricio Rumštajn
 */
public interface IPacket {
    /**
     * Get packet field.
     *
     * @param key
     * @return value
     */
    Object get(Object key);
    /**
     * Set packet field.
     *
     * @param key
     * @param  value
     */
    void put(Object key, Object value);
    /**
     * Does packet contain key.
     *
     * @param key
     * @return contains key or not
     */
    boolean containsKey(Object key);
    String stringify();
}
