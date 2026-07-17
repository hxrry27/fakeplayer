package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class ActionCommand {

    private final ActionManager actionManager;
    private final FakeplayerAutofishManager autofishManager;

    @Inject
    public ActionCommand(ActionManager actionManager, FakeplayerAutofishManager autofishManager) {
        this.actionManager = actionManager;
        this.autofishManager = autofishManager;
    }

    // Stops all actions on the fake player
    public void stop(@NotNull CommandSender sender, @NotNull Player fake) {
        actionManager.stop(fake);
    }

    public void dispatch(
            @NotNull CommandSender sender,
            @NotNull Player fake,
            @NotNull ActionType action,
            @NotNull ActionSetting setting
    ) {
        if (action == ActionType.USE
                && fake.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD
                && autofishManager.isAutofish(fake)
        ) {
            setting = ActionSetting.once();
        }
        actionManager.setAction(fake, action, setting);
        if (!setting.equals(ActionSetting.once()) || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(translatable("fakeplayer.command.generic.success"));
        }
    }

}
