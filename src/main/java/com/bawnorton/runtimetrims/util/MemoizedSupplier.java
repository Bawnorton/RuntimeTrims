package com.bawnorton.runtimetrims.util;

import java.util.function.Supplier;

public final class MemoizedSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T cached;

    public MemoizedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return cached == null ? (cached = supplier.get()) : cached;
    }

    public void clear() {
        cached = null;
    }
}
