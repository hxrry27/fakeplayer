package io.github.hello09x.fakeplayer.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SingletonSupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;

    private volatile boolean initialized;

    private T value;

    public SingletonSupplier(@NotNull Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    this.value = this.delegate.get();
                    this.initialized = true;
                }
            }
        }
        return this.value;
    }

}
