package net.coma112.axshop.listeners;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.handlers.CurrencyHandler;
import net.coma112.axshop.holder.QuantitySelectorHolder;
import net.coma112.axshop.holder.ShopHolder;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.CategoryManager;
import net.coma112.axshop.managers.QuantitySelectorManager;
import net.coma112.axshop.managers.ShopService;
import net.coma112.axshop.processor.MessageProcessor;
import net.coma112.axshop.utils.InventoryHelper;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ShopListener implements Listener {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");
    private static final NamespacedKey BUY_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-buy-price");
    private static final NamespacedKey SELL_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-sell-price");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-currency");

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (clickedInventory.getHolder() instanceof ShopHolder shopHolder) {
            event.setCancelled(true);

            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) return;

            handleShopItemClick(clickedItem, event, shopHolder);
        } else if (clickedInventory.getHolder() instanceof QuantitySelectorHolder holder) {
            event.setCancelled(true);

            final int slot = event.getSlot();
            final Player player = (Player) event.getWhoClicked();

            handleQuantitySelectorClick(slot, player, holder);
        }
    }

    private void handleQuantitySelectorClick(int slot, @NotNull Player player, @NotNull QuantitySelectorHolder holder) {
        QuantitySelectorManager manager = QuantitySelectorManager.getInstance();

        if (manager.isDecreaseButton(slot)) {
            int amount = manager.getDecreaseAmount(slot);
            holder.decreaseQuantity(amount);
            manager.updateInventory(holder);
        }

        else if (manager.isIncreaseButton(slot)) {
            int amount = manager.getIncreaseAmount(slot);
            holder.increaseQuantity(amount);
            manager.updateInventory(holder);
        }

        else if (manager.isConfirmButton(slot)) handleBuyConfirmation(player, holder);
        else if (manager.isCancelButton(slot)) {
            final String shopType = holder.getShopType();
            Optional<CategoryManager> category = ShopService.getInstance().getCategory(shopType);
            if (category.isPresent()) player.openInventory(category.get().getInventory());
            else player.openInventory(ShopService.getInstance().getMainMenu());
        }
    }

    private void handleBuyConfirmation(@NotNull Player player, @NotNull QuantitySelectorHolder holder) {
        if (InventoryHelper.isInventoryFull(player)) {
            player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
            return;
        }

        final int quantity = holder.getQuantity();
        final int totalPrice = holder.getTotalPrice();
        final CurrencyTypes currency = CurrencyTypes.valueOf(holder.getCurrency());
        final ItemStack item = holder.getItem();

        CompletableFuture.runAsync(() -> {
            if (CurrencyHandler.deduct(player, totalPrice, currency)) {
                AxShop.getInstance().getScheduler().runTask(() -> {
                    ItemStack purchasedItems = new ItemStack(item.getType(), quantity);
                    player.getInventory().addItem(purchasedItems)
                            .forEach((index, leftover) -> player.getWorld().dropItem(player.getLocation(), leftover));
                    player.sendMessage(MessageProcessor.process(
                            "&aSuccessfully purchased &e" + quantity + "x " + item.getType().name().toLowerCase() +
                                    " &afor &e" + totalPrice + " " + currency.name()));

                    final String shopType = holder.getShopType();
                    Optional<CategoryManager> category = ShopService.getInstance().getCategory(shopType);
                    if (category.isPresent()) player.openInventory(category.get().getInventory());
                    else player.openInventory(ShopService.getInstance().getMainMenu());
                });
            } //else player.sendMessage(MessageProcessor.process("&cNot enough currency to complete this purchase!"));
        });
    }

    private void handleShopItemClick(@NotNull ItemStack clickedItem, @NotNull InventoryClickEvent event, @NotNull ShopHolder shopHolder) {
        try {
            final ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;

            final PersistentDataContainer container = meta.getPersistentDataContainer();
            final String categoryId = container.get(CATEGORY_KEY, PersistentDataType.STRING);

            if (categoryId != null) {
                ShopService.getInstance().getCategory(categoryId).ifPresent(category -> event.getWhoClicked().openInventory(category.getInventory()));
                return;
            }

            final Optional<CategoryManager> categoryOpt = ShopService.getInstance().getCategory(shopHolder.getShopType());
            if (categoryOpt.isEmpty()) return;

            final CategoryManager category = categoryOpt.get();
            final int slot = event.getSlot();

            if (category.isFiller(slot)) return;

            final String currencyStr = container.get(CURRENCY_KEY, PersistentDataType.STRING);
            if (currencyStr == null) return;

            final CurrencyTypes currency = CurrencyTypes.valueOf(currencyStr);
            final Integer buyPrice = container.get(BUY_PRICE_KEY, PersistentDataType.INTEGER);
            final Integer sellPrice = container.get(SELL_PRICE_KEY, PersistentDataType.INTEGER);
            final Player player = (Player) event.getWhoClicked();
            final ClickType clickType = event.getClick();

            switch (clickType) {
                case LEFT -> {
                    if (buyPrice != null) QuantitySelectorManager.getInstance().openQuantitySelector(player, clickedItem, buyPrice, currencyStr);
                }

                case RIGHT -> handleSellAction(player, clickedItem, sellPrice, currency, 1);
                case SHIFT_RIGHT -> handleSellAction(player, clickedItem, sellPrice, currency,
                        InventoryHelper.countItems(player, clickedItem.getType()));
                default -> {}
            }
        } catch (Exception exception) {
            LoggerUtils.warn(exception.getMessage());
        }
    }

    private void handleSellAction(@NotNull Player player, @NotNull ItemStack item, @Nullable Integer sellPrice, @NotNull CurrencyTypes currency, int amount) {
        if (sellPrice == null) return;

        if (amount <= 0) {
            player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
            return;
        }

        CompletableFuture.runAsync(() -> {
            if (InventoryHelper.hasAndRemove(player, item.getType(), amount)) {
                final int totalSellPrice = sellPrice * amount;
                CurrencyHandler.add(player, totalSellPrice, currency);
                player.sendMessage(MessageProcessor.process(
                        "&aSuccessfully sold &e" + amount + "x " + item.getType().name().toLowerCase() +
                                " &afor &e" + totalSellPrice + " " + currency.name()));
            } else player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
        });
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