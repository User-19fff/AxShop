package net.coma112.axshop.managers;

import com.artillexstudios.axapi.config.Config;
import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.utils.MapUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unchecked", "deprecation"})
public class ShopManager {
    @Getter private static final ShopManager instance = new ShopManager();
    private final Map<String, ShopCategory> categories = new HashMap<>();

    public void initialize() {
        AxShop plugin = AxShop.getInstance();
        Config config = plugin.getConfiguration();

        Map<String, Object> shopsMap = MapUtils.convertListToMap(config.getMapList("shops"));
        loadCategories(shopsMap);
    }


    private void loadCategories(@NotNull Map<String, Object> shopsMap) {
        shopsMap.forEach((categoryName, categoryData) -> {
            ShopCategory category = new ShopCategory(categoryName);
            loadItems(category, (Map<String, Object>) categoryData);
            categories.put(categoryName, category);
        });
    }

    private void loadItems(@NotNull ShopCategory category, @NotNull Map<String, Object> categoryData) {
        categoryData.forEach((itemId, itemData) -> {
            Map<String, Object> itemMap = (Map<String, Object>) itemData;
            ItemStack item = createItemStack(itemMap);
            category.addItem(itemId, item);
        });
    }

    @NotNull
    private ItemStack createItemStack(@NotNull Map<String, Object> itemData) {
        Material material = Material.matchMaterial((String) itemData.get("material"));
        if (material == null) throw new IllegalArgumentException("Invalid material: " + itemData.get("material"));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName((String) itemData.get("name"));

            List<String> lore = (List<String>) itemData.get("lore");
            if (lore != null && !lore.isEmpty()) meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }


    public Optional<ShopCategory> getCategory(@NotNull String name) {
        return Optional.ofNullable(categories.get(name));
    }
}