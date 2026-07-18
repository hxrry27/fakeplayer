package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.command.CommandException;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.entity.FakeplayerTicker;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.github.hello09x.fakeplayer.core.util.WorldUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class SpawnCommand extends AbstractCommand {

    private final static DateTimeFormatter REMOVE_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final static BukkitScheduler scheduler = Bukkit.getScheduler();

    private static String toLocationString(@NotNull Location location) {
        return location.getWorld().getName()
                + ": "
                + StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    public void spawn(@NotNull CommandSender sender, @Nullable String name, @Nullable Location at, @Nullable float[] facing) {
        if (name != null && name.isEmpty()) {
            name = null;
        }

        Location spawnpoint = at != null
                ? at.clone()
                : (sender instanceof Player p ? p.getLocation().clone() : WorldUtils.getMainWorld().getSpawnLocation().clone());

        if (facing != null) {
            spawnpoint.setYaw(facing[0]);
            spawnpoint.setPitch(facing[1]);
        }

        var removedAt = Optional.ofNullable(config.getLifespan()).map(lifespan -> LocalDateTime.now().plus(lifespan)).orElse(null);
        var finalSpawn = spawnpoint;

        CompletableFuture<Player> future;
        try {
            future = manager.spawnAsync(sender, name, spawnpoint, Optional.ofNullable(config.getLifespan()).map(Duration::toMillis).orElse(FakeplayerTicker.NON_REMOVE_AT));
        } catch (CommandException ce) {
            sender.sendMessage(ce.component());
            return;
        }

        future
                .thenAcceptAsync(player -> {
                    if (player == null) {
                        return;
                    }
                    Component message;
                    if (removedAt == null) {
                        message = translatable(
                                "fakeplayer.command.spawn.success.without-lifespan",
                                text(player.getName(), WHITE),
                                text(toLocationString(finalSpawn), WHITE)
                        ).color(GRAY);
                    } else {
                        message = translatable(
                                "fakeplayer.command.spawn.success.with-lifespan",
                                text(player.getName(), WHITE),
                                text(toLocationString(finalSpawn), WHITE),
                                text(REMOVE_AT_FORMATTER.format(removedAt))
                        ).color(GRAY);
                    }
                    scheduler.runTask(Main.getInstance(), () -> {
                        sender.sendMessage(message);
                        if (sender instanceof Player p && manager.countByCreator(sender) == 1) {
                            p.updateCommands();
                        }
                    });
                }).exceptionally(e -> {
                    if (Throwables.getRootCause(e) instanceof CommandException ce) {
                        scheduler.runTask(Main.getInstance(), () -> sender.sendMessage(ce.component()));
                    } else {
                        scheduler.runTask(Main.getInstance(), () -> sender.sendMessage(translatable("fakeplayer.command.spawn.error.unknown", RED)));
                        log.severe(Throwables.getStackTraceAsString(e));
                    }
                    return null;
                });
    }

}
