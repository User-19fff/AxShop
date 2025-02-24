package net.coma112.axshop.utils;

import lombok.experimental.UtilityClass;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.commands.CommandShop;
import net.coma112.axshop.handlers.CommandExceptionHandler;
import net.coma112.axshop.identifiers.keys.ConfigKeys;
import net.coma112.axshop.listeners.ShopListener;
import org.bukkit.Bukkit;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.Locale;

@UtilityClass
@SuppressWarnings("deprecation")
public class RegisterUtils {
    public void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        Bukkit.getPluginManager().registerEvents(new ShopListener(), AxShop.getInstance());

        LoggerUtils.info("### Successfully registered 1 listener. ###");
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(AxShop.getInstance());


        handler.getTranslator().add(new CommandExceptionHandler());
        handler.setLocale(new Locale("en", "US"));
        handler.register(Orphans.path(ConfigKeys.ALIASES.getList().toArray(String[]::new)).handler(new CommandShop()));
        handler.registerBrigadier();

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
