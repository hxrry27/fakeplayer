package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WorldUtils {

    private WorldUtils() {
    }

    public static @NotNull World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static @Nullable World getOtherWorld(@NotNull World world) {
        for (var other : Bukkit.getWorlds()) {
            if (!other.equals(world)) {
                return other;
            }
        }
        return null;
    }

}
