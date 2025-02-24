package net.coma112.axshop.commands;

import com.artillexstudios.axapi.config.Config;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.managers.ShopManager;
import net.coma112.axshop.utils.LoggerUtils;
import net.coma112.axshop.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "deprecation"})
public class CommandShop implements OrphanCommand {
    private static final NamespacedKey CATEGORY_KELY = new NamespacedKey(AxShop.getInstance(), "shop-category");

    @Subcommand("reload")
    @CommandPermission("axshop.reload")
    @Usage("/alias reload")
    public void reload(@NotNull CommandSender sender) {
        AxShop.getInstance().getConfiguration().reload();
        AxShop.getInstance().getLanguage().reload();
        AxShop.getInstance().getWebhook().reload();
        ShopManager.getInstance().initialize();
        sender.sendMessage("§aPlugin reloaded!");
    }

    @Subcommand("menu")
    @CommandPermission("axshop.menu")
    @Usage("/alias menu")
    public void menu(@NotNull Player player) {
        Config config = AxShop.getInstance().getConfiguration();
        List<Map<Object, Object>> mainMenuList = config.getMapList("main-menu.categories");
        Map<String, Object> mainMenuMap = MapUtils.convertListToMap(mainMenuList);
        Inventory inventory = Bukkit.createInventory(new ShopInventoryHolder("main-menu", 9), 9, "Shop Menu");

        mainMenuMap.forEach((categoryKey, categoryData) -> {
            Map<String, Object> data = (Map<String, Object>) categoryData;

            try {
                ItemStack item = createMenuItem(data, categoryKey);
                int slot = data.containsKey("slot") ? (int) data.get("slot") : 0;
                inventory.setItem(slot, item);
            } catch (Exception e) {
                LoggerUtils.warn("Hiba a kategória betöltésében: " + categoryKey + " - " + e.getMessage());
            }
        });

        player.openInventory(inventory);
    }

    @NotNull
    private ItemStack createMenuItem(@NotNull Map<String, Object> categoryData, String categoryName) {
        Material material = Material.valueOf((String) categoryData.get("material"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName((String) categoryData.get("name"));
        meta.setLore((List<String>) categoryData.get("lore"));
        meta.getPersistentDataContainer().set(CATEGORY_KELY, PersistentDataType.STRING, categoryName);

        item.setItemMeta(meta);
        return item;
    }
}