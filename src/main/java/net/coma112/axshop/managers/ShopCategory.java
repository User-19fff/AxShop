package net.coma112.axshop.managers;

import lombok.Getter;
import net.coma112.axshop.holders.ShopInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("deprecation")
public class ShopCategory {
    private final String name;
    private final Map<String, ItemStack> items = new HashMap<>();
    private Inventory inventory;

    public ShopCategory(@NotNull String name) {
        this.name = name;
    }

    public void addItem(@NotNull String id, @NotNull ItemStack item) {
        items.put(id, item);
        getInventory().addItem(item);
    }

    public ItemStack getItem(@NotNull String id) {
        return items.get(id);
    }

    public Inventory getInventory() {
        if (inventory == null) inventory = Bukkit.createInventory(new ShopInventoryHolder(name, 27), 27, "Shop - " + name);
        return inventory;
    }
}
