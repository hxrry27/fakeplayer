package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.feature.FakeplayerFeatureManager;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@Singleton
public class ConfigCommand extends AbstractCommand {

    private final FakeplayerFeatureManager featureManager;

    @Inject
    public ConfigCommand(FakeplayerFeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    public void listConfig(@NotNull Player sender) {
        CompletableFuture.runAsync(() -> {
            var lines = featureManager.getFeatures(sender).values().stream().map(feature -> textOfChildren(
                    translatable(feature.key(), GOLD),
                    text(": ", GRAY),
                    join(separator(space()), feature.key().getOptions().stream().map(option -> {
                        var style = option.equals(feature.value())
                                ? Style.style(GREEN, UNDERLINED)
                                : Style.style(GRAY);
                        return text("[" + option + "]").style(style);
                    }).toList())
            )).toList();
            var message = join(separator(newline()), lines);
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> sender.sendMessage(message));
        });
    }

}
