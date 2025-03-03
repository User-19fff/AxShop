package net.coma112.axshop.item;

import lombok.experimental.UtilityClass;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.identifiers.FormatTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
@SuppressWarnings("deprecation")
public class ItemBuilder {
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-category");
    private static final NamespacedKey BUY_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-buy-price");
    private static final NamespacedKey SELL_PRICE_KEY = new NamespacedKey(AxShop.getInstance(), "shop-sell-price");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey(AxShop.getInstance(), "shop-currency");

    @NotNull
    public static ItemStack createCategoryItem(@NotNull Material material, @NotNull String name, List<String> lore, @NotNull String categoryId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(CATEGORY_KEY, PersistentDataType.STRING, categoryId);

        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack createShopItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore, int buyPrice, int sellPrice, @NotNull String currency) {
        List<String> processedLore = lore.stream()
                .map(line -> line
                        .replace("{buyPrice}", FormatTypes.format(buyPrice))
                        .replace("{sellPrice}", FormatTypes.format(sellPrice)))
                .collect(Collectors.toList());

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(processedLore);

        meta.getPersistentDataContainer().set(BUY_PRICE_KEY, PersistentDataType.INTEGER, buyPrice);
        meta.getPersistentDataContainer().set(SELL_PRICE_KEY, PersistentDataType.INTEGER, sellPrice);
        meta.getPersistentDataContainer().set(CURRENCY_KEY, PersistentDataType.STRING, currency);

        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ItemStack createFillerItem(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}