package com.bawnorton.runtimetrims.util;

import org.apache.commons.lang3.Validate;

public abstract class DefaultedAdaptable<K, T> extends Adaptable<K, T> {
    private T defaultAdapter;

    public void setDefaultAdapter(T adapter) {
        this.defaultAdapter = adapter;
    }

    public T getDefaultAdapter() {
        return defaultAdapter;
    }

    @Override
    protected T getAdapter(K key) {
        T adapter = super.getAdapter(key);
        if (adapter == null) {
            Validate.notNull(defaultAdapter, "Adapter for \"%s\" is not registered and no default adapter set".formatted(key));
            return defaultAdapter;
        }
        return adapter;
    }
}
