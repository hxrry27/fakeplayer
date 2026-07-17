package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class EntityUtils {

    private EntityUtils() {
    }


    public static boolean teleportAndSound(@NotNull Entity entity, @NotNull Location to) {
        var from = entity.getLocation().clone();
        if (!entity.teleport(to)) {
            return false;
        }

        playTeleportSound(from);
        playTeleportSound(to);
        return true;
    }

    private static void playTeleportSound(@NotNull Location at) {
        var world = at.getWorld();
        if (world == null) {
            return;
        }
        world.playSound(at, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

}
