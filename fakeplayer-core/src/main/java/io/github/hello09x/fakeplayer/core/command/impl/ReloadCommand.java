package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.i18n.Adventure5Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class ReloadCommand extends AbstractCommand {

    private final FakeplayerConfig cfg;
    private final Adventure5Translator translator;

    @Inject
    public ReloadCommand(FakeplayerConfig cfg, Adventure5Translator translator) {
        this.cfg = cfg;
        this.translator = translator;
    }

    public void reload(@NotNull CommandSender sender) {
        cfg.reload();
        translator.reload();
        sender.sendMessage(translatable("fakeplayer.command.generic.success", GRAY));
        if (cfg.isConfigFileOutOfDate()) {
            sender.sendMessage(translatable("fakeplayer.configuration.out-of-date", GRAY));
        }
    }

}
