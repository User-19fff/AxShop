package net.coma112.axshop.managers;

import com.artillexstudios.axapi.config.Config;
import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.processor.LoreProcessor;
import net.coma112.axshop.processor.MessageProcessor;
import net.coma112.axshop.utils.InventoryUtils;
import net.coma112.axshop.utils.ItemBuilder;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unchecked", "deprecation"})
public class ShopManager {
    @Getter private static final ShopManager instance = new ShopManager();
    private final Map<String, Inventory> menus = new HashMap<>();
    private final Map<String, ShopCategory> categories = new HashMap<>();
    private Inventory inventory;

    public void initialize() {
        loadMainMenu();
        loadShops();
    }

    public Optional<Inventory> getMenu(@NotNull String menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    public Inventory getMainMenu() {
        return inventory;
    }

    public Optional<ShopCategory> getCategory(@NotNull String name) {
        return Optional.ofNullable(categories.get(name));
    }

    private void loadMainMenu() {
        Config config = AxShop.getInstance().getConfiguration();
        int size = config.getInt("main-menu.size", 27);
        String title = MessageProcessor.process(config.getString("main-menu.name", "Shop Menu"));
        ShopInventoryHolder holder = new ShopInventoryHolder("main-menu");
        inventory = Bukkit.createInventory(holder, size, title);
        List<Map<Object, Object>> categories = config.getMapList("main-menu.categories");

        for (Map<Object, Object> category : categories) {
            processCategoryItem(inventory, category);
        }

        menus.put("main-menu", inventory);
    }

    private void processCategoryItem(Inventory inventory, @NotNull Map<Object, Object> categoryData) {
        categoryData.forEach((rawKey, rawValue) -> {
            try {
                String categoryKey = rawKey.toString();
                Map<String, Object> itemData = convertToGenericMap(rawValue);

                ItemStack item = ItemBuilder.createCategoryItem(
                        Material.valueOf(((String) itemData.get("material")).toUpperCase()),
                        MessageProcessor.process((String) itemData.get("name")),
                        LoreProcessor.processLore((List<String>) itemData.get("lore")),
                        categoryKey
                );

                inventory.setItem((int) itemData.get("slot"), item);
            } catch (Exception exception) {
                LoggerUtils.warn("Error processing category item: " + exception.getMessage());
            }
        });
    }

    private void loadShops() {
        Config config = AxShop.getInstance().getConfiguration();
        List<Map<Object, Object>> shops = config.getMapList("shops");

        for (Map<Object, Object> shop : shops) {
            shop.forEach((rawKey, rawValue) -> {
                try {
                    String categoryKey = rawKey.toString();
                    Map<String, Object> categoryData = convertToGenericMap(rawValue);

                    ShopCategory category = new ShopCategory(
                            categoryKey,
                            MessageProcessor.process((String) categoryData.get("name")),
                            (int) categoryData.get("size")
                    );

                    loadCategoryItems(category, categoryData);
                    categories.put(categoryKey, category);
                } catch (Exception exception) {
                    LoggerUtils.warn("Error loading shop category: " + exception.getMessage());
                }
            });
        }
    }

    private void loadCategoryItems(@NotNull ShopCategory category, @NotNull Map<String, Object> categoryData) {
        Map<String, Object> items = (Map<String, Object>) categoryData.get("items");

        items.forEach((itemId, itemData) -> {
            Map<String, Object> itemMap = (Map<String, Object>) itemData;
            int slot = (int) itemMap.get("slot");
            ItemStack item = createItemStack(itemMap);

            category.addItem(itemId, item, slot);
        });
    }

    @NotNull
    private ItemStack createItemStack(@NotNull Map<String, Object> itemMap) {
        Map<String, Object> prices = (Map<String, Object>) itemMap.get("prices");
        int buyPrice = (int) prices.get("buy");
        int sellPrice = (int) prices.get("sell");
        CurrencyTypes currency = CurrencyTypes.valueOf(((String) itemMap.get("currency")).toUpperCase());

        return ItemBuilder.createShopItem(
                Material.valueOf((String) itemMap.get("material")),
                MessageProcessor.process((String) itemMap.get("name")),
                LoreProcessor.processItemLore((List<String>) itemMap.get("lore"), buyPrice, sellPrice, currency),
                buyPrice,
                sellPrice,
                currency.name()
        );
    }

    @NotNull
    @Contract("null -> fail")
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToGenericMap(@Nullable Object data) {
        if (data instanceof Map) {
            Map<Object, Object> original = (Map<Object, Object>) data;
            Map<String, Object> converted = new HashMap<>();
            original.forEach((key, value) -> converted.put(key.toString(), value));
            return converted;
        }

        throw new IllegalArgumentException("Invalid data format for conversion!");
    }
}