package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RideCommand extends AbstractCommand {

    public void mount(@NotNull CommandSender sender, @NotNull Player fake) {
        var entity = fake.getNearbyEntities(4.5, 4.5, 4.5)
                .stream()
                .filter(e -> e != fake)
                .findFirst()
                .orElse(null);
        if (entity == null) {
            return;
        }
        bridge.fromPlayer(fake).startRiding(entity, true);
    }

    public void dismount(@NotNull Player fake) {
        bridge.fromPlayer(fake).stopRiding();
    }

}
