package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BlockUtils {

    private BlockUtils() {
    }

    public static @NotNull List<Block> getNearbyBlocks(@NotNull Location center, int radius, @NotNull Material material) {
        var world = center.getWorld();
        if (world == null) {
            return List.of();
        }

        var blocks = new ArrayList<Block>();
        var cx = center.getBlockX();
        var cy = center.getBlockY();
        var cz = center.getBlockZ();
        for (var x = cx - radius; x <= cx + radius; x++) {
            for (var y = cy - radius; y <= cy + radius; y++) {
                for (var z = cz - radius; z <= cz + radius; z++) {
                    var block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

}
