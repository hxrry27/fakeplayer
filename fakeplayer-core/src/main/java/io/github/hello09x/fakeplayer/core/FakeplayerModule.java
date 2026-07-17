package io.github.hello09x.fakeplayer.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.hello09x.fakeplayer.core.repository.jdbc.JdbcTemplate;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.i18n.Adventure5Translator;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansion;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansionImpl;
import io.github.hello09x.fakeplayer.core.util.ClassUtils;
import io.github.hello09x.fakeplayer.core.util.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class FakeplayerModule extends AbstractModule {

    private final static Logger log = Main.getInstance().getLogger();

    @Override
    protected void configure() {
        super.bind(Plugin.class).toInstance(Main.getInstance());
    }

    @Provides
    @Singleton
    public @NotNull HikariDataSource dataSource() {
        var folder = Main.getInstance().getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Failed to create data folder for " + Main.getInstance().getName());
        }

        var config = new HikariConfig();
        config.setPoolName(Main.getInstance().getName() + "-datasource");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + new File(folder, "data.db").getAbsolutePath());
        config.setMaximumPoolSize(1); 
        return new HikariDataSource(config);
    }

    @Provides
    @Singleton
    public @NotNull JdbcTemplate jdbcTemplate(HikariDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Provides
    @Singleton
    public @NotNull Adventure5Translator translator() {
        var translator = new Adventure5Translator();
        translator.register();
        return translator;
    }

    @Provides
    @Singleton
    private @NotNull NMSBridge nmsBridge() {
        var bridge = ServiceLoader
                .load(NMSBridge.class, NMSBridge.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(NMSBridge::isSupported)
                .findAny()
                .orElse(null);

        if (bridge == null) {
            throw new ExceptionInInitializerError("Unsupported Minecraft version: " + VersionUtils.getMinecraftVersion());
        }
        return bridge;
    }

    @Singleton
    @Provides
    private @Nullable FakeplayerPlaceholderExpansion fakeplayerPlaceholderExpansion(FakeplayerManager fakeplayerManager, ActionManager actionManager) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") || !ClassUtils.isClassExists("me.clip.placeholderapi.expansion.PlaceholderExpansion")) {
            return null;
        }
        return new FakeplayerPlaceholderExpansionImpl(fakeplayerManager, actionManager);
    }

}
