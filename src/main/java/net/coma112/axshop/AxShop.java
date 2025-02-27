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
import net.coma112.axshop.utils.RegisterUtils;
import revxrsal.zapper.ZapperJavaPlugin;

import java.io.File;

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
        ShopManager.getInstance().initialize();

        RegisterUtils.registerCommands();
        RegisterUtils.registerListeners();
        RegisterUtils.loadBasicFormatOverrides();
        RegisterUtils.registerHooks();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings
                .builder()
                .setUseDefaults(false)
                .build(),

                LoaderSettings
                        .builder()
                        .setAutoUpdate(true)
                        .build(), DumperSettings.DEFAULT,

                UpdaterSettings
                        .builder()
                        .setKeepAll(true)
                        .build());

        language = new Config(new File(getDataFolder(), "messages.yml"), getResource("messages.yml"), GeneralSettings
                .builder()
                .setUseDefaults(false)
                .build(),

                LoaderSettings
                        .builder()
                        .setAutoUpdate(true)
                        .build(), DumperSettings.DEFAULT,

                UpdaterSettings
                        .builder()
                        .setKeepAll(true)
                        .build());

        webhook = new Config(new File(getDataFolder(), "webhooks.yml"), getResource("webhooks.yml"), GeneralSettings
                .builder()
                .setUseDefaults(false)
                .build(),

                LoaderSettings
                        .builder()
                        .setAutoUpdate(true)
                        .build(), DumperSettings.DEFAULT,

                UpdaterSettings
                        .builder()
                        .setKeepAll(true)
                        .build());
    }
}