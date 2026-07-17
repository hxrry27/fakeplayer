package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ExperienceUtils {

    private ExperienceUtils() {
    }

    public static int getExp(@NotNull Player player) {
        var level = player.getLevel();
        return getExpToLevel(level) + Math.round(getExpAtLevel(level) * player.getExp());
    }

    public static void clean(@NotNull Player player) {
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);
    }

    private static int getExpAtLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        }
        if (level <= 30) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }

    private static int getExpToLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }
        if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        }
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

}
