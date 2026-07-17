package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class DebugCommand {

    private final FakeplayerManager manager;
    private final NMSBridge bridge;

    @Inject
    public DebugCommand(FakeplayerManager manager, NMSBridge bridge) {
        this.manager = manager;
        this.bridge = bridge;
    }

    public void sendPluginMessage(@NotNull CommandSender sender, @NotNull String channel, @NotNull String message) {
        var carrier = Bukkit.getOnlinePlayers().stream().filter(manager::isNotFake).findFirst().orElse(null);
        if (carrier == null) {
            sender.sendMessage("No real player online to carry the message");
            return;
        }
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), channel);
        var out = ByteStreams.newDataOutput();
        out.writeUTF(message);
        carrier.sendPluginMessage(Main.getInstance(), channel, out.toByteArray());
    }

}
