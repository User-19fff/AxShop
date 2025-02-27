package net.coma112.axshop.utils;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.commands.CommandShop;
import net.coma112.axshop.handlers.CommandExceptionHandler;
import net.coma112.axshop.hooks.plugins.BeastToken;
import net.coma112.axshop.hooks.plugins.PlayerPoints;
import net.coma112.axshop.hooks.plugins.Vault;
import net.coma112.axshop.identifiers.keys.ConfigKeys;
import net.coma112.axshop.listeners.ShopListener;
import org.bukkit.Bukkit;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
@SuppressWarnings("deprecation")
public class RegisterUtils {
    @Getter public static final Map<Long, String> basicFormatOverrides = new ConcurrentHashMap<>();

    public void loadBasicFormatOverrides() {
        getBasicFormatOverrides().clear();

        Section section = AxShop.getInstance().getConfiguration().getSection("formatting.basic");

        if (section == null) return;

        section.getRoutesAsStrings(true).forEach(route -> {
            String value = section.getString(route);
            if (value == null) return;

            try {
                long parsedKey = Long.parseLong(route);
                getBasicFormatOverrides().put(parsedKey, value);
            } catch (NumberFormatException ignored) {
                LoggerUtils.warn("Skipping invalid key in formatting.basic: " + route);
            }
        });
    }

    public void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        Bukkit.getPluginManager().registerEvents(new ShopListener(), AxShop.getInstance());

        LoggerUtils.info("### Successfully registered 1 listener. ###");
    }

    public void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(AxShop.getInstance());


        handler.getTranslator().add(new CommandExceptionHandler());
        handler.setLocale(new Locale("en", "US"));
        handler.register(Orphans.path(ConfigKeys.ALIASES.getList().toArray(String[]::new)).handler(new CommandShop()));
        handler.registerBrigadier();

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }

    public void registerHooks() {
        PlayerPoints.getInstance().registerHook();
        Vault.getInstance().registerHook();
        BeastToken.getInstance().registerHook();
    }
}
