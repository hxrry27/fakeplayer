package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class MetadataUtils {

    private MetadataUtils() {
    }

    public static <T> @NotNull Optional<MetadataValue> find(
            @NotNull Plugin plugin,
            @NotNull Metadatable target,
            @NotNull String key,
            @NotNull Class<T> type
    ) {
        return target.getMetadata(key)
                .stream()
                .filter(value -> plugin.equals(value.getOwningPlugin()))
                .filter(value -> type.isInstance(value.value()))
                .findFirst();
    }

}
