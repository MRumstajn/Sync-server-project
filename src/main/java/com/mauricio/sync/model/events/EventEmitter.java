package com.mauricio.sync.model.events;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mauricio Rumštajn
 */
public abstract class EventEmitter<T> {
    private List<T> listeners = new ArrayList<>();

    /**
     * Add a listener.
     * @param listener
     */
    public void addListener(T listener){
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     * @param listener
     */
    public void removeListener(T listener){
        listeners.remove(listener);
    }

    /**
     * Get all listeners.
     * @return
     */
    public List<T> getListeners() {
        return listeners;
    }
}
