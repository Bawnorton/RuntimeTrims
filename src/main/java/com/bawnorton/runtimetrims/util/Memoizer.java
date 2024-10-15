package com.bawnorton.runtimetrims.util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Memoizer {
    public static <T, R> MemoizedFunction<T, R> memoize(Function<T, R> function) {
        return new MemoizedFunction<>(function);
    }

    public static <R> MemoizedSupplier<R> memoize(Supplier<R> supplier) {
        return new MemoizedSupplier<>(supplier);
    }
}
