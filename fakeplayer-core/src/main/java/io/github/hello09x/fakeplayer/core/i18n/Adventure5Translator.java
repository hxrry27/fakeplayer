package io.github.hello09x.fakeplayer.core.i18n;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Adventure5Translator {

    private final static Key NAME = Key.key("fakeplayer", "translations");

    private final static String BASE_NAME = "message/message";

    private @Nullable Translator source;

    public void register() {
        this.unregister();

        var store = TranslationStore.messageFormat(NAME);
        store.defaultLocale(Locale.ENGLISH);
        store.registerAll(Locale.ENGLISH, this.bundle(), false);

        GlobalTranslator.translator().addSource(store);
        this.source = store;
    }

    public void unregister() {
        if (this.source != null) {
            GlobalTranslator.translator().removeSource(this.source);
            this.source = null;
        }
    }

    public void reload() {
        ResourceBundle.clearCache(Adventure5Translator.class.getClassLoader());
        this.register();
    }

    private @NotNull ResourceBundle bundle() {
        return ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH, Adventure5Translator.class.getClassLoader());
    }

}
