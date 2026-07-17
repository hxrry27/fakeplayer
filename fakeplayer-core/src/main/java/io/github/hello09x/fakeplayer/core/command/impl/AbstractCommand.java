package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.logging.Logger;

public abstract class AbstractCommand {

    protected final static Logger log = Main.getInstance().getLogger();

    @Inject
    protected NMSBridge bridge;

    @Inject
    protected FakeplayerManager manager;

    @Inject
    protected FakeplayerConfig config;

    protected static @NotNull Locale localeOf(@NotNull CommandSender sender) {
        return sender instanceof Player player ? player.locale() : Locale.ENGLISH;
    }

}
