package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class Exceptions {

    private Exceptions() {
    }

    public static void suppress(@NotNull Plugin plugin, @NotNull Runnable task) {
        try {
            task.run();
        } catch (Throwable e) {
            plugin.getLogger().log(Level.SEVERE, "Suppressed exception", e);
        }
    }

}
