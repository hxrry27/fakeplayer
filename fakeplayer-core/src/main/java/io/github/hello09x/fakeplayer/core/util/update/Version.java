package io.github.hello09x.fakeplayer.core.util.update;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A `1.2.3` style version, used only to compare the local and remote versions.
 */
public record Version(int major, int minor, int patch) implements Comparable<Version> {

    private final static Pattern PATTERN = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?");

    public static @NotNull Version parse(@NotNull String value) throws InvalidVersionException {
        var matcher = PATTERN.matcher(value.trim());
        if (!matcher.find()) {
            throw new InvalidVersionException(value);
        }

        try {
            return new Version(
                    Integer.parseInt(matcher.group(1)),
                    matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2)),
                    matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3))
            );
        } catch (NumberFormatException e) {
            throw new InvalidVersionException(value);
        }
    }

    @Override
    public int compareTo(@NotNull Version other) {
        var result = Integer.compare(this.major, other.major);
        if (result != 0) {
            return result;
        }
        result = Integer.compare(this.minor, other.minor);
        if (result != 0) {
            return result;
        }
        return Integer.compare(this.patch, other.patch);
    }

}
