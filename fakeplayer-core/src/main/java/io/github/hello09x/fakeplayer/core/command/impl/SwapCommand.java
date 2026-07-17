package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SwapCommand extends AbstractCommand {

    public void swap(@NotNull Player fake) {
        bridge.fromPlayer(fake).swapItemWithOffhand();
    }

}
