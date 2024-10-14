package com.bawnorton.runtimetrims.util;

import java.util.Set;

public abstract class KeylessAdaptable<T> extends Adaptable<Long, T> {
    public void registerAdapter(T adapter) {
        adapters.put((long) adapter.hashCode(), adapter);
    }

    @Override
    public void registerAdapter(T adapter, Long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerAdapter(T adpater, Set<Long> keys) {
        throw new UnsupportedOperationException();
    }
}
