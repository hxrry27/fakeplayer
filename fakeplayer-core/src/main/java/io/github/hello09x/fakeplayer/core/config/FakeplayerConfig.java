package io.github.hello09x.fakeplayer.core.config;


import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.translatable;

@Getter
@ToString
@Singleton
public class FakeplayerConfig {

    private final static Logger log = Main.getInstance().getLogger();

    private final static String defaultNameChars = "^[a-zA-Z0-9_]+$";

    /**
     * 每位玩家最多多少个假人
     */
    private int playerLimit;

    /**
     * 服务器最多多少个假人
     */
    private int serverLimit;

    /**
     * 命名模版
     */
    private String nameTemplate;

    /**
     * 假人名称前缀
     */
    private String namePrefix;

    /**
     * 名称样式, 颜色
     */
    private NamedTextColor nameStyleColor;

    /**
     * 名称样式, 格式
     */
    private List<TextDecoration> nameStyleDecorations;

    /**
     * 创建者玩家下线时是否跟随下线
     */
    private boolean followQuiting;

    /**
     * 是否探测 IP
     */
    private boolean detectIp;

    /**
     * 服务器 tps 低于这个值移除所有假人
     */
    private int kaleTps;

    /**
     * 创建前执行命令
     */
    private List<String> preSpawnCommands;

    /**
     * 创建时执行命令
     */
    private List<String> postSpawnCommands;

    /**
     * 创建后执行命令
     */
    private List<String> afterSpawnCommands;

    /**
     * 退出前执行命令
     */
    private List<String> postQuitCommands;

    /**
     * 退出后命令
     */
    private List<String> afterQuitCommands;

    /**
     * 自执行命令
     */
    private List<String> selfCommands;

    /**
     * 退出时是否丢弃背包物品
     */
    private boolean dropInventoryOnQuiting;

    /**
     * 是否保存假人存档
     */
    private boolean persistData;

    /**
     * 死亡时是否踢出游戏
     */
    private boolean kickOnDead;

    /**
     * 自定义名称规则
     */
    private Pattern namePattern;

    private NameGate nameGate;

    private Set<String> nameAllowlistNames;

    private Set<UUID> nameAllowlistIds;

    /**
     * 检测更新
     */
    private boolean checkForUpdates;

    /**
     * 允许执行的命令
     */
    @Deprecated
    private Set<String> allowCommands;

    /**
     * 默认假人存活时间
     */
    @Nullable
    private Duration lifespan;

    /**
     * 开发者调试模式
     */
    private boolean debug;

    /**
     * 防止踢出
     */
    private PreventKicking preventKicking;

    /**
     * invsee 实现方式
     */
    private InvseeImplement invseeImplement;

    /**
     * 真实皮肤
     */
    @Beta
    private boolean defaultOnlineSkin;

    private Map<Feature, String> defaultFeatures;

    @Inject
    public FakeplayerConfig() {
    }

    public void reload() {
        var plugin = Main.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.reload(plugin.getConfig());
    }

    public boolean isConfigFileOutOfDate() {
        var config = Main.getInstance().getConfig();
        var defaults = config.getDefaults();
        if (defaults == null) {
            return false;
        }

        var bundled = defaults.getString("version");
        return bundled != null && !bundled.equals(config.getString("version"));
    }

    private static int maxIfZero(int value) {
        return value <= 0 ? Integer.MAX_VALUE : value;
    }

    private static <T extends Enum<T>> @NotNull T getEnum(
            @NotNull FileConfiguration file,
            @NotNull String path,
            @NotNull Class<T> type,
            @NotNull T def
    ) {
        var value = file.getString(path);
        if (value == null || value.isBlank()) {
            return def;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warning("Invalid %s: %s".formatted(path, value));
            return def;
        }
    }

    private void reload(@NotNull FileConfiguration file) {
        this.playerLimit = maxIfZero(file.getInt("player-limit", 1));
        this.serverLimit = maxIfZero(file.getInt("server-limit", 1000));
        this.followQuiting = file.getBoolean("follow-quiting", true);
        this.detectIp = file.getBoolean("detect-ip", false);
        this.kaleTps = file.getInt("kale-tps", 0);
        this.selfCommands = file.getStringList("self-commands");
        this.preSpawnCommands = file.getStringList("pre-spawn-commands");
        this.postSpawnCommands = file.getStringList("post-spawn-commands");
        this.afterSpawnCommands = file.getStringList("after-spawn-commands");
        this.postQuitCommands = file.getStringList("post-quit-commands");
        this.afterQuitCommands = file.getStringList("after-quit-commands");
        this.nameTemplate = file.getString("name-template", "");
        this.dropInventoryOnQuiting = file.getBoolean("drop-inventory-on-quiting", true);
        this.persistData = file.getBoolean("persist-data", true);
        this.kickOnDead = file.getBoolean("kick-on-dead", true);
        this.checkForUpdates = file.getBoolean("check-for-updates", false);
        this.namePattern = getNamePattern(file);
        this.nameGate = getEnum(file, "name-gate", NameGate.class, NameGate.PLAYED_BEFORE);
        this.setNameAllowlist(file.getStringList("name-allowlist"));
        this.preventKicking = this.getPreventKicking(file);
        this.nameTemplate = getNameTemplate(file);
        this.namePrefix = file.getString("name-prefix", "");
        this.lifespan = getLifespan(file);
        this.allowCommands = file.getStringList("allow-commands")
                .stream()
                .map(c -> c.startsWith("/") ? c.substring(1) : c)
                .filter(c -> !c.isBlank())
                .collect(Collectors.toSet());

        this.defaultOnlineSkin = file.getBoolean("default-online-skin", false);
        this.defaultFeatures = Arrays.stream(Feature.values())
                .collect(Collectors.toMap(Function.identity(), key -> file.getString("default-features." + key.name(), key.getDefaultOption())));
        this.invseeImplement = getEnum(file, "invsee-implement", InvseeImplement.class, InvseeImplement.AUTO);
        this.debug = file.getBoolean("debug", false);
        this.nameStyleColor = this.getNameStyleColor(file);
        this.nameStyleDecorations = this.getNameStyleDecorations(file);

        if (this.isConfigFileOutOfDate()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (Main.getInstance().isEnabled()) {
                    Main.getInstance().getComponentLogger().warn(translatable("fakeplayer.configuration.out-of-date"));
                }
            }, 1);
        }

        if (!this.allowCommands.isEmpty()) {
            log.warning("allow-commands is deprecated which will be removed at 0.4.0, you should use Permissions Plugin to assign permission groups to fake players.");
        }

        var preparingCommands = file.getStringList("preparing-commands");
        if (!preparingCommands.isEmpty()) {
            log.warning("preparing-commands is deprecated, use post-spawn-commands instead.");
            this.postSpawnCommands.addAll(preparingCommands);
        }

        var destroyCommands = file.getStringList("destroy-commands");
        if (!destroyCommands.isEmpty()) {
            log.warning("destroy-commands is deprecated, use post-quit-commands instead.");
            this.postQuitCommands.addAll(destroyCommands);
        }

    }

    private @Nullable Duration getLifespan(@NotNull FileConfiguration file) {
        var minutes = file.getLong("lifespan");
        if (minutes <= 0) {
            return null;
        }
        return Duration.ofMinutes(minutes);
    }


    private void setNameAllowlist(@NotNull List<String> entries) {
        var names = new HashSet<String>();
        var ids = new HashSet<UUID>();
        for (var entry : entries) {
            var value = entry.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                ids.add(UUID.fromString(value));
            } catch (IllegalArgumentException e) {
                names.add(value.toLowerCase(Locale.ROOT));
            }
        }
        this.nameAllowlistNames = Set.copyOf(names);
        this.nameAllowlistIds = Set.copyOf(ids);
    }

    public boolean isNameAllowed(@NotNull String name, @NotNull UUID uuid) {
        return this.nameAllowlistIds.contains(uuid)
                || this.nameAllowlistNames.contains(name.toLowerCase(Locale.ROOT));
    }

    private @NotNull Pattern getNamePattern(@NotNull FileConfiguration file) {
        try {
            return Pattern.compile(file.getString("name-pattern", defaultNameChars));
        } catch (PatternSyntaxException e) {
            log.warning("Invalid name-pattern: " + file.getString("name-chars"));
            return Pattern.compile(defaultNameChars);
        }
    }

    private @NotNull String getNameTemplate(@NotNull FileConfiguration file) {
        var tmpl = file.getString("name-template", "");
        if (tmpl.startsWith("-") || tmpl.startsWith("@")) {
            log.warning("Invalid name template: " + this.nameTemplate);
            return "";
        }
        return tmpl;
    }

    private @NotNull PreventKicking getPreventKicking(@NotNull FileConfiguration file) {
        if (file.getBoolean("prevent-kicked-on-spawning", false)) {
            log.warning("prevent-kicked-on-spawning is deprecated which will be removed at 0.4.0, use prevent-kick instead");
            return PreventKicking.ON_SPAWNING;
        }

        return getEnum(file, "prevent-kicking", PreventKicking.class, PreventKicking.ON_SPAWNING);
    }

    private @NotNull NamedTextColor getNameStyleColor(@NotNull FileConfiguration file) {
        var styles = Objects.requireNonNullElse(file.getString("name-style"), "").split(",\\s*");
        var color = NamedTextColor.WHITE;
        for (var style : styles) {
            var c = NamedTextColor.NAMES.value(style);
            if (c != null) {
                color = c;
            }
        }
        return color;
    }

    private @NotNull List<TextDecoration> getNameStyleDecorations(@NotNull FileConfiguration file) {
        var styles = Objects.requireNonNullElse(file.getString("name-style"), "").split(",\\s*");
        var decorations = new ArrayList<TextDecoration>();
        for (var style : styles) {
            var decoration = TextDecoration.NAMES.value(style);
            if (decoration != null) {
                decorations.add(decoration);
            }
        }
        return decorations;
    }

}
