package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class StatusCommand extends AbstractCommand {

    private final static Component LINE_SPLITTER = text(StringUtils.repeat("-", 20), GRAY);

    private static @NotNull NamedTextColor color(double current, double max) {
        var p = current / max;
        if (p >= 0.75) return GREEN;
        if (p >= 0.5) return YELLOW;
        if (p >= 0.25) return GOLD;
        if (p >= 0.125) return RED;
        return DARK_RED;
    }

    public void status(@NotNull CommandSender sender, @NotNull Player fake) {
        var lines = new ArrayList<Component>(4);
        lines.add(translatable("fakeplayer.command.status.title", text(fake.getName(), WHITE)).color(GRAY));
        lines.add(this.getHealthLine(fake));
        lines.add(this.getFoodLine(fake));
        lines.add(LINE_SPLITTER);
        lines.add(getFeatureLine(fake));
        sender.sendMessage(join(newlines(), lines));
    }

    private @NotNull Component getFoodLine(@NotNull Player target) {
        var food = target.getFoodLevel();
        var max = 20.0;
        return translatable(
                "fakeplayer.command.status.food",
                textOfChildren(
                        text(Mth.floor(food, 0.5), color(food, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                )
        ).color(WHITE);
    }

    private @NotNull Component getHealthLine(@NotNull Player target) {
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(Attributes.maxHealth()))
                .map(AttributeInstance::getValue)
                .orElse(20D);
        return translatable(
                "fakeplayer.command.status.health",
                textOfChildren(
                        text(Mth.floor(health, 0.5), color(health, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ).color(WHITE)
        );
    }

    private @NotNull Component getFeatureLine(@NotNull Player faker) {
        var messages = new ArrayList<Component>();
        for (var feature : Feature.values()) {
            var detector = feature.getDetector();
            if (detector == null) {
                continue;
            }
            var status = detector.apply(faker);
            messages.add(textOfChildren(
                    translatable(feature, WHITE),
                    space(),
                    join(separator(space()), feature.getOptions().stream().map(option -> {
                        var color = option.equals(status) ? GREEN : GRAY;
                        return text("[" + option + "]", color);
                    }).toList())
            ));
        }
        return join(newlines(), messages);
    }

}
