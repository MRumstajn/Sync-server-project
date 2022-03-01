package com.mauricio.sync.model.events;

import java.util.ArrayList;
import java.util.List;

public abstract class EventEmitter<T> {
    private List<T> listeners = new ArrayList<>();

    public void addListener(T listener){
        listeners.add(listener);
    }

    public void removeListener(T listener){
        listeners.remove(listener);
    }

    public List<T> getListeners() {
        return listeners;
    }
}
