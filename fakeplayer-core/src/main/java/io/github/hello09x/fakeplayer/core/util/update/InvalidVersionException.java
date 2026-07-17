package io.github.hello09x.fakeplayer.core.util.update;

import org.jetbrains.annotations.NotNull;

public class InvalidVersionException extends RuntimeException {

    public InvalidVersionException(@NotNull String version) {
        super("Invalid version: " + version);
    }

}
