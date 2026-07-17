package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class KillCommand extends AbstractCommand {

    public void kill(@NotNull CommandSender sender, @NotNull Player fake) {
        if (manager.remove(fake.getName(), "command kill")) {
            sender.sendMessage(textOfChildren(
                    translatable("fakeplayer.command.kill.success.removed", GRAY),
                    space(),
                    text(fake.getName())
            ));
        }
    }

}
