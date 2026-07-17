package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class SchedulerUtils {

    private SchedulerUtils() {
    }

    public static @NotNull CompletableFuture<Void> runTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        return runTask(plugin, () -> {
            task.run();
            return null;
        });
    }

    public static <T> @NotNull CompletableFuture<T> runTask(@NotNull Plugin plugin, @NotNull Supplier<T> task) {
        var future = new CompletableFuture<T>();
        Bukkit.getScheduler().runTask(plugin, () -> complete(future, task));
        return future;
    }

    public static @NotNull CompletableFuture<Void> runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task) {
        return runTaskAsynchronously(plugin, () -> {
            task.run();
            return null;
        });
    }

    public static <T> @NotNull CompletableFuture<T> runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Supplier<T> task) {
        var future = new CompletableFuture<T>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> complete(future, task));
        return future;
    }

    private static <T> void complete(@NotNull CompletableFuture<T> future, @NotNull Supplier<T> task) {
        try {
            future.complete(task.get());
        } catch (Throwable e) {
            future.completeExceptionally(e);
        }
    }

}
