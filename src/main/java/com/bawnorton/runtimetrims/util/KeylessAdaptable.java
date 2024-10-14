package com.bawnorton.runtimetrims.util;

import org.apache.commons.lang3.Validate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class KeylessAdaptable<T> {
    protected final Set<T> adapters = new HashSet<>();

    public void registerAdapter(T adapter) {
        Validate.notNull(adapter, "adapter cannot be null");
        if (adapters.contains(adapter)) {
            throw new IllegalArgumentException("Adapter: \"%s\" already registered".formatted(adapter.getClass().getSimpleName()));
        }
        adapters.add(adapter);
    }

    protected List<T> getAdapters() {
        return new ArrayList<>(adapters);
    }
}
