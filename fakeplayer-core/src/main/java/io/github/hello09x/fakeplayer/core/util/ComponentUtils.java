package io.github.hello09x.fakeplayer.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ComponentUtils {

    private ComponentUtils() {
    }

    public static @NotNull String toString(@NotNull Component component) {
        return toString(component, null);
    }

    public static @NotNull String toString(@NotNull Component component, @Nullable Locale locale) {
        var rendered = GlobalTranslator.render(component, locale == null ? Locale.getDefault() : locale);
        return PlainTextComponentSerializer.plainText().serialize(rendered);
    }

}
