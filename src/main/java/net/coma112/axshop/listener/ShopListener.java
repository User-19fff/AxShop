package net.coma112.axshop.listener;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.holder.QuantitySelectorHolder;
import net.coma112.axshop.holder.ShopHolder;
import net.coma112.axshop.managers.ShopService;
import net.coma112.axshop.utils.shop.ListenerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ShopListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (clickedInventory.getHolder() instanceof ShopHolder shopHolder) {
            event.setCancelled(true);

            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) return;

            ListenerUtils.handleShopItemClick(clickedItem, event, shopHolder);
        } else if (clickedInventory.getHolder() instanceof QuantitySelectorHolder holder) {
            event.setCancelled(true);

            final int slot = event.getSlot();
            final Player player = (Player) event.getWhoClicked();

            ListenerUtils.handleQuantitySelectorClick(slot, player, holder);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(@NotNull final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ShopHolder ||
                event.getInventory().getHolder() instanceof QuantitySelectorHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(@NotNull final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof ShopHolder shopHolder) {
            if (shopHolder.getShopType().equals("main-menu") || event.getInventory().getHolder() instanceof QuantitySelectorHolder) return;

            final Player player = (Player) event.getPlayer();
            AxShop.getInstance().getScheduler().runTaskLater(() -> {
                if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof QuantitySelectorHolder)) player.openInventory(ShopService.getInstance().getMainMenu());
            }, 1L);
        }
    }
}