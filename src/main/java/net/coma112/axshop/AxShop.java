package net.coma112.axshop;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.coma112.axshop.managers.ShopManager;
import net.coma112.axshop.utils.LoggerUtils;
import net.coma112.axshop.utils.RegisterUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperJavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public final class AxShop extends ZapperJavaPlugin {
    @Getter private static AxShop instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Config language;
    @Getter private Config webhook;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();

        CompletableFuture.runAsync(() -> ShopManager.getInstance().initialize())
                .thenRun(() -> {
                    RegisterUtils.registerListeners();
                    RegisterUtils.loadBasicFormatOverrides();
                    RegisterUtils.registerHooks();
                })
                .exceptionally(exception -> {
                    LoggerUtils.error("Failed to initialize shop system: " + exception.getMessage());
                    return null;
                });

        RegisterUtils.registerCommands();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        final GeneralSettings generalSettings = GeneralSettings.builder()
                .setUseDefaults(false)
                .build();

        final LoaderSettings loaderSettings = LoaderSettings.builder()
                .setAutoUpdate(true)
                .build();

        final UpdaterSettings updaterSettings = UpdaterSettings.builder()
                .setKeepAll(true)
                .build();

        config = loadConfig("config.yml", generalSettings, loaderSettings, updaterSettings);
        language = loadConfig("messages.yml", generalSettings, loaderSettings, updaterSettings);
        webhook = loadConfig("webhooks.yml", generalSettings, loaderSettings, updaterSettings);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private Config loadConfig(@NotNull String fileName, @NotNull GeneralSettings generalSettings, @NotNull LoaderSettings loaderSettings, @NotNull UpdaterSettings updaterSettings) {
        return new Config(
                new File(getDataFolder(), fileName),
                getResource(fileName),
                generalSettings,
                loaderSettings,
                DumperSettings.DEFAULT,
                updaterSettings
        );
    }
}