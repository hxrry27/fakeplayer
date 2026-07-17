package io.github.hello09x.fakeplayer.core.command;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class CommandException extends RuntimeException {

    private final transient Component component;

    public CommandException(@NotNull Component component) {
        super(Component.text("").append(component).toString());
        this.component = component;
    }

    public @NotNull Component component() {
        return this.component;
    }

}
