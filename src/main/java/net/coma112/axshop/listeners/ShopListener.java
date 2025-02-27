package net.coma112.axshop.listeners;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.handlers.CurrencyHandler;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.ShopManager;
import net.coma112.axshop.registry.CurrencyRegistry;
import net.coma112.axshop.utils.InventoryUtils;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShopListener implements Listener {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");
    private static final NamespacedKey BUY_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-buy-price");
    private static final NamespacedKey SELL_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-sell-price");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-currency");

    @EventHandler
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        InventoryHolder holder = clickedInventory.getHolder();
        if (!(holder instanceof ShopInventoryHolder)) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        try {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            String categoryId = meta.getPersistentDataContainer().get(CATEGORY_KEY, PersistentDataType.STRING);

            if (categoryId != null) {
                ShopManager.getInstance().getCategory(categoryId).ifPresent(category -> {
                    event.getWhoClicked().openInventory(category.getInventory());
                });
                return;
            }

            String currency = meta.getPersistentDataContainer().get(CURRENCY_KEY, PersistentDataType.STRING);
            Integer buyPrice = meta.getPersistentDataContainer().get(BUY_PRICE_KEY, PersistentDataType.INTEGER);
            Integer sellPrice = meta.getPersistentDataContainer().get(SELL_PRICE_KEY, PersistentDataType.INTEGER);

            if (buyPrice == null || sellPrice == null) {
                LoggerUtils.warn("Currency or price not found in clicked item!");
                return;
            }

            Player player = (Player) event.getWhoClicked();

            if (event.isLeftClick()) {
                if (InventoryUtils.isInventoryFull(player)) {
                    player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
                    return;
                }

                CurrencyHandler.deduct(player, buyPrice, Objects.requireNonNull(currency));
                player.getInventory().addItem(clickedItem);
            } else if (event.isRightClick()) {
                CurrencyHandler.add(player, sellPrice, Objects.requireNonNull(currency));
                InventoryUtils.hasAndRemoveOne(player, clickedItem);
            }
        } catch (Exception exception) {
            LoggerUtils.warn("Error handling menu click: " + exception.getMessage());
        }
    }

    @EventHandler
    public void onInventoryDrag(@NotNull final InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ShopInventoryHolder) event.setCancelled(true);
    }
}