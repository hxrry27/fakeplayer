package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.util.EntityUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class TeleportCommand extends AbstractCommand {

    public void tpswap(@NotNull Player sender, @NotNull Player fake) {
        var l1 = sender.getLocation();
        var l2 = fake.getLocation();
        EntityUtils.teleportAndSound(fake, l1);
        EntityUtils.teleportAndSound(sender, l2);
    }

}
