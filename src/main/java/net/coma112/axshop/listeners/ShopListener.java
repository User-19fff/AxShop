package net.coma112.axshop.listeners;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.handlers.CurrencyHandler;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.ShopCategory;
import net.coma112.axshop.managers.ShopManager;
import net.coma112.axshop.registry.CurrencyRegistry;
import net.coma112.axshop.utils.InventoryUtils;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        if (!(holder instanceof ShopInventoryHolder shopHolder)) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        try {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            // Ellenőrizzük, hogy a kattintott elem egy kategória-e
            String categoryId = meta.getPersistentDataContainer().get(CATEGORY_KEY, PersistentDataType.STRING);
            if (categoryId != null) {
                // Megnyitjuk a kategóriát
                ShopManager.getInstance().getCategory(categoryId).ifPresent(category -> {
                    event.getWhoClicked().openInventory(category.getInventory());
                });
                return;
            }

            // Ha nem kategória, akkor normál elemként kezeljük
            ShopCategory category = ShopManager.getInstance().getCategory(shopHolder.getShopType()).orElse(null);
            if (category == null) return;

            int slot = event.getSlot();

            // Ha a kattintott slot kitöltő elem, akkor nem csinálunk semmit
            if (category.isFiller(slot)) {
                return;
            }

            CurrencyTypes currency = Objects.requireNonNull(CurrencyTypes.valueOf(meta.getPersistentDataContainer().get(CURRENCY_KEY, PersistentDataType.STRING)));
            Integer buyPrice = meta.getPersistentDataContainer().get(BUY_PRICE_KEY, PersistentDataType.INTEGER);
            Integer sellPrice = meta.getPersistentDataContainer().get(SELL_PRICE_KEY, PersistentDataType.INTEGER);

            Player player = (Player) event.getWhoClicked();

            switch (event.getClick()) {
                case LEFT -> {
                    if (buyPrice == null) return;

                    if (InventoryUtils.isInventoryFull(player)) {
                        player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
                        return;
                    }

                    CurrencyHandler.deduct(player, buyPrice, currency);
                    player.getInventory().addItem(ItemStack.of(clickedItem.getType()));
                }

                case RIGHT -> {
                    if (sellPrice == null) return;

                    int amount = InventoryUtils.countItems(player, clickedItem.getType());

                    if (amount == 0) {
                        player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
                        return;
                    }

                    CurrencyHandler.add(player, sellPrice, currency);
                    InventoryUtils.hasAndRemove(player, clickedItem.getType(), 1);
                }

                case SHIFT_RIGHT -> {
                    if (sellPrice == null) return;

                    int amount = InventoryUtils.countItems(player, clickedItem.getType());

                    if (amount == 0) {
                        player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
                        return;
                    }

                    int allSellPrice = sellPrice * amount;

                    InventoryUtils.hasAndRemove(player, clickedItem.getType(), amount);
                    CurrencyHandler.add(player, allSellPrice, currency);
                }
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

    @EventHandler
    public void onInventoryClose(@NotNull final InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ShopInventoryHolder shopHolder) {
            Player player = (Player) event.getPlayer();

            if (!shopHolder.getShopType().equals("main-menu")) AxShop.getInstance().getScheduler().runTaskLater(() -> player.openInventory(ShopManager.getInstance().getMainMenu()), 1L);
        }
    }
}