package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RotationCommand {

    public void lookDirection(@NotNull Player fake, @NotNull Direction direction) {
        switch (direction) {
            case NORTH -> lookRotation(fake, 180, 0);
            case SOUTH -> lookRotation(fake, 0, 0);
            case EAST -> lookRotation(fake, -90, 0);
            case WEST -> lookRotation(fake, 90, 0);
            case UP -> lookRotation(fake, fake.getLocation().getYaw(), -90);
            case DOWN -> lookRotation(fake, fake.getLocation().getYaw(), 90);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void lookAt(@NotNull Player fake, double x, double y, double z) {
        fake.lookAt(new Location(fake.getWorld(), x, y, z), LookAnchor.EYES);
    }

    public void lookRotation(@NotNull Player fake, float yaw, float pitch) {
        fake.setRotation(yaw % 360, Mth.clamp(pitch, -90, 90));
    }

    public void turn(@NotNull Player fake, float yaw, float pitch) {
        var pos = fake.getLocation();
        lookRotation(fake, pos.getYaw() + yaw, pos.getPitch() + pitch);
    }

}
