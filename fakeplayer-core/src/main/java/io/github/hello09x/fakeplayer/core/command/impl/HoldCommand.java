package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class HoldCommand extends AbstractCommand {

    // slot is 1-9 as the player sees it; the hotbar is 0-indexed internally
    public void hold(@NotNull Player fake, int slot) {
        fake.getInventory().setHeldItemSlot(slot - 1);
        fake.updateInventory();
    }

}
