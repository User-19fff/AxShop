package net.coma112.axshop.item;

import net.coma112.axshop.AxShop;
import net.coma112.axshop.identifiers.FormatTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class ItemFactory {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");
    private static final NamespacedKey BUY_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-buy-price");
    private static final NamespacedKey SELL_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-sell-price");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-currency");

    private static final ConcurrentHashMap<String, String> PRICE_CACHE = new ConcurrentHashMap<>();

    @NotNull
    public static ItemStack createCategoryItem(@NotNull Material material, @NotNull String name, List<String> lore, @NotNull String categoryId) {
        return modifyItem(new ItemStack(material), meta -> {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            meta.getPersistentDataContainer().set(CATEGORY_KEY, PersistentDataType.STRING, categoryId);
            return meta;
        });
    }

    @NotNull
    public static ItemStack createShopItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore, int buyPrice, int sellPrice, @NotNull String currency) {
        String buyPriceString = PRICE_CACHE.computeIfAbsent("buy_" + buyPrice,
                k -> FormatTypes.format(buyPrice));
        String sellPriceString = PRICE_CACHE.computeIfAbsent("sell_" + sellPrice,
                k -> FormatTypes.format(sellPrice));

        List<String> processedLore = lore.stream()
                .map(line -> line
                        .replace("{buyPrice}", buyPriceString)
                        .replace("{sellPrice}", sellPriceString))
                .collect(Collectors.toList());

        return modifyItem(new ItemStack(material), meta -> {
            meta.setDisplayName(name);
            meta.setLore(processedLore);

            final PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(BUY_PRICE_KEY, PersistentDataType.INTEGER, buyPrice);
            container.set(SELL_PRICE_KEY, PersistentDataType.INTEGER, sellPrice);
            container.set(CURRENCY_KEY, PersistentDataType.STRING, currency);

            return meta;
        });
    }

    @NotNull
    public static ItemStack createFillerItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
        return modifyItem(new ItemStack(material), meta -> {
            meta.setDisplayName(name);
            meta.setLore(lore);
            return meta;
        });
    }

    @NotNull
    private static ItemStack modifyItem(@NotNull ItemStack item, @NotNull UnaryOperator<ItemMeta> metaModifier) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        item.setItemMeta(metaModifier.apply(meta));
        return item;
    }
}