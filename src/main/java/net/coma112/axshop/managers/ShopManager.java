package net.coma112.axshop.managers;

import com.artillexstudios.axapi.config.Config;
import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.holders.ShopInventoryHolder;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.item.ItemBuilder;
import net.coma112.axshop.processor.LoreProcessor;
import net.coma112.axshop.processor.MessageProcessor;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
public final class ShopManager {
    @Getter
    private static final ShopManager instance = new ShopManager();

    private final Map<String, Inventory> menus = new ConcurrentHashMap<>();
    private final Map<String, ShopCategory> categories = new ConcurrentHashMap<>();
    private Inventory mainMenuInventory;

    public void initialize() {
        loadMainMenu();
        loadShops();
    }

    @NotNull
    public Optional<Inventory> getMenu(@NotNull String menuId) {
        return Optional.ofNullable(menus.get(menuId));
    }

    public Inventory getMainMenu() {
        return mainMenuInventory;
    }

    @NotNull
    public Optional<ShopCategory> getCategory(@NotNull String name) {
        return Optional.ofNullable(categories.get(name));
    }

    private void loadMainMenu() {
        final Config config = AxShop.getInstance().getConfiguration();
        final int size = config.getInt("main-menu.size", 27);
        final String title = MessageProcessor.process(config.getString("main-menu.name", "Shop Menu"));

        final ShopInventoryHolder holder = new ShopInventoryHolder("main-menu");
        mainMenuInventory = Bukkit.createInventory(holder, size, title);

        holder.setInventory(mainMenuInventory);

        config.getMapList("main-menu.categories").forEach(category ->
                processCategoryItem(mainMenuInventory, category));

        menus.put("main-menu", mainMenuInventory);
    }

    private void processCategoryItem(Inventory inventory, @NotNull Map<Object, Object> categoryData) {
        categoryData.forEach((rawKey, rawValue) -> {
            try {
                final String categoryKey = rawKey.toString();
                final Map<String, Object> itemData = convertToGenericMap(rawValue);

                final String materialStr = ((String) itemData.get("material")).toUpperCase();
                final Material material = Material.valueOf(materialStr);

                final String name = MessageProcessor.process((String) itemData.get("name"));

                @SuppressWarnings("unchecked")
                final List<String> lore = LoreProcessor.processLore((List<String>) itemData.get("lore"));

                final ItemStack item = ItemBuilder.createCategoryItem(material, name, lore, categoryKey);
                inventory.setItem((int) itemData.get("slot"), item);
            } catch (Exception exception) {
                LoggerUtils.warn(exception.getMessage());
            }
        });
    }

    private void loadShops() {
        final Config config = AxShop.getInstance().getConfiguration();
        config.getMapList("shops").forEach(shop -> {
            shop.forEach((rawKey, rawValue) -> {
                try {
                    final String categoryKey = rawKey.toString();
                    final Map<String, Object> categoryData = convertToGenericMap(rawValue);

                    final ShopCategory category = new ShopCategory(
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
        });
    }

    private void loadCategoryItems(@NotNull ShopCategory category, @NotNull Map<String, Object> categoryData) {
        if (!categoryData.containsKey("items")) {
            return;
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> items = (Map<String, Object>) categoryData.get("items");

        items.forEach((key, value) -> {
            try {
                if (!(value instanceof Map)) return;

                final Map<String, Object> itemData = (Map<String, Object>) value;

                if (!itemData.containsKey("prices") && !itemData.containsKey("currency")) {
                    if (itemData.containsKey("material")) loadFiller(category, itemData, key);
                    else LoggerUtils.warn("Filler material is missing for key: " + key);
                } else loadItem(category, itemData, key);
            } catch (Exception exception) {
                LoggerUtils.warn(exception.getMessage());
            }
        });
    }

    private void loadFiller(@NotNull ShopCategory category, @NotNull Map<String, Object> fillerData, @NotNull String fillerKey) {
        try {
            final String materialName = (String) fillerData.get("material");

            if (materialName == null) {
                LoggerUtils.warn("Filler material is missing for key: " + fillerKey);
                return;
            }

            final Material material = Material.valueOf(materialName.toUpperCase());
            final String name = MessageProcessor.process((String) fillerData.get("name"));

            final List<String> lore = LoreProcessor.processLore((List<String>) fillerData.get("lore"));

            final String slotString = (String) fillerData.get("slot");
            final ItemStack fillerItem = ItemBuilder.createFillerItem(material, name, lore);

            parseSlots(slotString).forEach(slot -> category.addFiller(slot, fillerItem));
        } catch (Exception exception) {
            LoggerUtils.warn("Error loading filler item: " + exception.getMessage());
        }
    }

    private void loadItem(@NotNull ShopCategory category, @NotNull Map<String, Object> itemData, @NotNull String itemKey) {
        final int slot = (int) itemData.get("slot");
        final ItemStack item = createItemStack(itemData);
        category.addItem(itemKey, item, slot);
    }

    @NotNull
    private List<Integer> parseSlots(@NotNull String slotString) {
        final String[] parts = slotString.split(";");
        final List<Integer> slots = new ArrayList<>();

        for (final String part : parts) {
            if (part.contains("-")) {
                final String[] range = part.split("-");
                final int start = Integer.parseInt(range[0]);
                final int end = Integer.parseInt(range[1]);
                IntStream.rangeClosed(start, end).forEach(slots::add);
            } else if (part.contains("&")) {
                Arrays.stream(part.split("&"))
                        .map(Integer::parseInt)
                        .forEach(slots::add);
            } else slots.add(Integer.parseInt(part));
        }

        return slots;
    }

    @NotNull
    private ItemStack createItemStack(@NotNull Map<String, Object> itemMap) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> prices = (Map<String, Object>) itemMap.get("prices");

        final int buyPrice = (int) prices.get("buy");
        final int sellPrice = (int) prices.get("sell");

        final CurrencyTypes currency = CurrencyTypes.valueOf(
                ((String) itemMap.get("currency")).toUpperCase());

        final Material material = Material.valueOf((String) itemMap.get("material"));
        final String name = MessageProcessor.process((String) itemMap.get("name"));

        final List<String> lore = LoreProcessor.processItemLore(
                (List<String>) itemMap.get("lore"),
                buyPrice,
                sellPrice,
                currency);

        return ItemBuilder.createShopItem(
                material,
                name,
                lore,
                buyPrice,
                sellPrice,
                currency.name()
        );
    }

    @NotNull
    private Map<String, Object> convertToGenericMap(@NotNull Object data) {
        if (data instanceof Map map) {
            return ((Map<Object, Object>) map).entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().toString(),
                            Map.Entry::getValue,
                            (v1, v2) -> v2,
                            HashMap::new
                    ));
        }

        throw new IllegalArgumentException("Invalid data format for conversion!");
    }
}