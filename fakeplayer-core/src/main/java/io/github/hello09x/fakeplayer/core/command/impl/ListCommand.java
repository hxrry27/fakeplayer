package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

@Singleton
public class ListCommand extends AbstractCommand {

    private static String toLocationString(@NotNull Location location) {
        return location.getWorld().getName()
                + ": "
                + StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    public void list(@NotNull CommandSender sender) {
        var fakes = sender.isOp() ? manager.getAll() : manager.getAll(sender);

        var lines = new ArrayList<Component>();
        lines.add(translatable("fakeplayer.command.list.title", AQUA, BOLD));
        for (var fake : fakes) {
            lines.add(textOfChildren(
                    text(fake.getName() + " (" + manager.getCreatorName(fake) + ")", GOLD),
                    text(" - ", GRAY),
                    text(toLocationString(fake.getLocation()), WHITE),
                    space(),
                    translatable("fakeplayer.command.list.button.kill", RED)
                            .clickEvent(runCommand("/player " + fake.getName() + " kill"))
            ));
        }
        sender.sendMessage(join(newlines(), lines));
    }

}
