package com.jiangnane.abus.core.cache;

import android.os.Bundle;

import com.jiangnane.abus.core.EventListener;
import com.jiangnane.abus.utils.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public enum SubscriberCacheManager {
    INS;

    private static final String TAG = SubscriberCacheManager.class.getSimpleName();

    private ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>> mSubscribersCache = new ConcurrentHashMap<>();

    SubscriberCacheManager() {
    }

    public synchronized void subscribe(String key, EventListener listener) {
        CopyOnWriteArrayList<EventListener> eventListeners = mSubscribersCache.get(key);
        if (eventListeners == null) {
            eventListeners = new CopyOnWriteArrayList<>();
        }
        eventListeners.add(listener);
        mSubscribersCache.put(key, eventListeners);
    }

    public synchronized void unSubscribe(String key, EventListener listener) {
        CopyOnWriteArrayList<EventListener> eventListeners = mSubscribersCache.get(key);
        if(eventListeners == null || eventListeners.isEmpty()) {
            return;
        }

        eventListeners.remove(listener);

        if(eventListeners.isEmpty()) {
            mSubscribersCache.remove(key);
        }
    }

    public synchronized void unSubscribeAll() {
        mSubscribersCache.clear();
    }

    public synchronized void onEvent(final String key, final Bundle event) {
        if (event == null) {
            return;
        }

        Logger.i(TAG, "onEvent: key=" + key + ", event=" + event);

        if (key != null) {
            List<EventListener> listeners = mSubscribersCache.get(key);
            if (listeners != null) {
                for (int i = listeners.size() - 1; i >= 0; --i) {
                    final EventListener l = listeners.get(i);
                    if (l != null) {
                        l.onEvent(key, event);
                        Logger.i(TAG, "onEvent: l.onEvent(key, event)@" + l);
                    } else {
                        listeners.remove(i);
                    }
                }
            }
        }
    }
}
