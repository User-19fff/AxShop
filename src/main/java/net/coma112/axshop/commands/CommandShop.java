package net.coma112.axshop.commands;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.ShopManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

public class CommandShop implements OrphanCommand {
    private static final AxShop plugin = AxShop.getInstance();

    @Subcommand("reload")
    @CommandPermission("axshop.reload")
    @Usage("/alias reload")
    public void reload(@NotNull CommandSender sender) {
        plugin.getConfiguration().reload();
        plugin.getLanguage().reload();
        plugin.getWebhook().reload();
        ShopManager.getInstance().initialize();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("menu")
    @CommandPermission("axshop.menu")
    @Usage("/shop menu")
    @DefaultFor({"~"})
    public void menu(@NotNull Player player) {
        ShopManager.getInstance().getMenu("main-menu").ifPresentOrElse(
                player::openInventory,
                () -> player.sendMessage(MessageKeys.MENU_NOT_LOADED.getMessage())
        );
    }
}