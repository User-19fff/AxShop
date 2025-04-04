package net.coma112.axshop.utils.shop;

import lombok.experimental.UtilityClass;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.handlers.CurrencyHandler;
import net.coma112.axshop.holder.QuantitySelectorHolder;
import net.coma112.axshop.holder.ShopHolder;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.managers.CategoryManager;
import net.coma112.axshop.managers.QuantitySelectorManager;
import net.coma112.axshop.managers.ShopService;
import net.coma112.axshop.utils.InventoryHelper;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class ListenerUtils {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");
    private static final NamespacedKey BUY_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-buy-price");
    private static final NamespacedKey SELL_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-sell-price");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-currency");

    public void handleSellAction(@NotNull Player player, @NotNull ItemStack item, @Nullable Integer sellPrice, @NotNull CurrencyTypes currency, int amount) {
        if (sellPrice == null) return;

        if (amount <= 0) {
            player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
            return;
        }

        CompletableFuture.runAsync(() -> {
            if (InventoryHelper.hasAndRemove(player, item.getType(), amount)) {
                final int totalSellPrice = sellPrice * amount;
                CurrencyHandler.add(player, totalSellPrice, currency);
                player.sendMessage(MessageKeys.SUCCESS_SELL.getMessage());
            } else player.sendMessage(MessageKeys.NO_ITEM_FOUND.getMessage());
        });
    }

    public void handleShopItemClick(@NotNull ItemStack clickedItem, @NotNull InventoryClickEvent event, @NotNull ShopHolder shopHolder) {
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
                    if (buyPrice != null) {
                        NamespacedKey commandsKey = new NamespacedKey(AxShop.getInstance(), "shop-commands");
                        String commandsString = container.get(commandsKey, PersistentDataType.STRING);

                        if (commandsString != null && !commandsString.isEmpty()) {
                            int quantity = 1;
                            int totalPrice = buyPrice * quantity;

                            if (CurrencyHandler.deduct(player, totalPrice, currency)) {
                                String[] commands = commandsString.split(";;");

                                for (String cmd : commands) {
                                    String processedCmd = cmd.replace("%player%", player.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCmd);
                                }

                                player.sendMessage(MessageKeys.SUCCESS_BUY.getMessage());
                            } else player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
                        } else QuantitySelectorManager.getInstance().openQuantitySelector(player, clickedItem, buyPrice, currencyStr);

                    }
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

    public void handleQuantitySelectorClick(int slot, @NotNull Player player, @NotNull QuantitySelectorHolder holder) {
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

    public void handleBuyConfirmation(@NotNull Player player, @NotNull QuantitySelectorHolder holder) {
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
                    player.sendMessage(MessageKeys.SUCCESS_BUY.getMessage());

                    final String shopType = holder.getShopType();
                    Optional<CategoryManager> category = ShopService.getInstance().getCategory(shopType);
                    if (category.isPresent()) player.openInventory(category.get().getInventory());
                    else player.openInventory(ShopService.getInstance().getMainMenu());
                });
            } else player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
        });
    }
}
