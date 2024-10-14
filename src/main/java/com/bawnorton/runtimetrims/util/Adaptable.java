package com.bawnorton.runtimetrims.util;

import org.apache.commons.lang3.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Adaptable<K, T> {
    protected final Map<K, T> adapters = new HashMap<>();

    public void registerAdapter(T adpater, Set<K> keys) {
        keys.forEach(key -> registerAdapter(adpater, key));
    }

    public void registerAdapter(T adapter, K key) {
        Validate.notNull(key, "key cannot be null");
        Validate.notNull(adapter, "adapter cannot be null");
        if(adapters.containsKey(key)) {
            throw new IllegalArgumentException("Adapter: \"%s\" for key \"%s\" already registered".formatted(adapters.get(key).getClass().getSimpleName(), key));
        }
        adapters.put(key, adapter);
    }

    protected boolean hasAdapter(K key) {
        return adapters.containsKey(key);
    }

    protected T getAdapter(K key) {
        Validate.notNull(key, "key cannot be null");
        return adapters.get(key);
    }

    protected List<T> getAdapters() {
        return new ArrayList<>(this.adapters.values());
    }
}
