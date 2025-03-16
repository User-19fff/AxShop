package net.coma112.axshop.commands;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.identifiers.FormatTypes;
import net.coma112.axshop.identifiers.keys.ConfigKeys;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.ShopService;
import net.coma112.axshop.utils.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class ShopCommand implements OrphanCommand {
    private static final AxShop plugin = AxShop.getInstance();

    @Subcommand("reload")
    @CommandPermission("axshop.reload")
    public void reload(@NotNull CommandSender sender) {
        plugin.getConfiguration().reload();
        plugin.getLanguage().reload();
        ShopService.getInstance().initialize();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @CommandPermission("axshop.menu")
    @DefaultFor("~")
    public void menu(@NotNull Player player) {
        if (ConfigKeys.DISABLED_WORLDS.getList().contains(player.getWorld().getName())) {
            player.sendMessage(MessageKeys.DISABLED_WORLD.getMessage());
            return;
        }

        ShopService.getInstance().getMenu("main-menu").ifPresentOrElse(
                player::openInventory,
                () -> player.sendMessage(MessageKeys.MENU_NOT_LOADED.getMessage())
        );
    }

    @Subcommand("worth")
    @CommandPermission("axshop.worth")
    public void worth(@NotNull Player player) {
        List<Map<String, Object>> shops = plugin.getConfiguration().getList("shops");
        if (shops == null || shops.isEmpty()) return;

        var worthMessage = new StringBuilder(MessageKeys.WORTH_HEADER.getMessage() + "\n\n");
        var totalWorth = new AtomicInteger();

        for (Map<String, Object> shop : shops) {
            for (String shopType : shop.keySet()) {
                Map<String, Object> shopData = (Map<String, Object>) shop.get(shopType);
                if (shopData == null) continue;

                Map<String, Object> items = (Map<String, Object>) shopData.get("items");
                if (items == null) continue;

                for (String itemKey : items.keySet()) {
                    Map<String, Object> itemData = (Map<String, Object>) items.get(itemKey);
                    if (itemData == null) continue;

                    String materialName = (String) itemData.get("material");
                    Material material = Material.matchMaterial(materialName);
                    if (material == null) continue;

                    Map<String, Object> prices = (Map<String, Object>) itemData.get("prices");
                    if (prices == null || !prices.containsKey("sell")) continue;
                    int sellPrice = (int) prices.get("sell");

                    int itemCount = InventoryHelper.countItems(player, material);
                    if (itemCount == 0) continue;

                    int itemWorth = itemCount * sellPrice;
                    totalWorth.addAndGet(itemWorth);

                    String currencyKey = (String) itemData.getOrDefault("currency", "vault");
                    String currencyName = plugin.getConfiguration().getString("currencies." + currencyKey, currencyKey);

                    worthMessage.append(MessageKeys.WORTH_ITEM.getMessage()
                            .replace("{item}", material.name())
                            .replace("{amount}", String.valueOf(itemCount))
                            .replace("{value}", String.valueOf(itemWorth))
                            .replace("{currency}", currencyName)
                    ).append("\n\n");
                }
            }
        }

        worthMessage.append(MessageKeys.WORTH_TOTAL.getMessage()
                .replace("{total}", FormatTypes.format(Integer.parseInt(totalWorth.toString()))));

        player.sendMessage(worthMessage.toString());
    }
}