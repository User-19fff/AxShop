package net.coma112.axshop.listeners;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.managers.ShopManager;
import org.bukkit.NamespacedKey;
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

public class ShopListener implements Listener {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");

    @EventHandler
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        InventoryHolder holder = clickedInventory.getHolder();
        if (!(holder instanceof ShopInventoryHolder shopHolder)) return;

        String shopType = shopHolder.getShopType();

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (shopType.equals("main-menu")) {
            ItemMeta meta = clickedItem.getItemMeta();
            String categoryName = meta.getPersistentDataContainer().get(CATEGORY_KEY, PersistentDataType.STRING);

            if (categoryName == null) return;

            ShopManager.getInstance().getCategory(categoryName).ifPresentOrElse(
                    category -> event.getWhoClicked().openInventory(category.getInventory()),
                    () -> AxShop.getInstance().getLogger().warning("Category not found: " + categoryName)
            );
        } else handleCategoryClick(event, clickedItem);
    }

    @EventHandler
    public void onInventoryDrag(@NotNull final InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof ShopInventoryHolder) event.setCancelled(true);
    }

    private void handleCategoryClick(InventoryClickEvent event, ItemStack clickedItem) {
        // Handle item buy/sell logic here
    }
}