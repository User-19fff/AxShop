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
    private final String id;
    private final String displayName;
    private final Map<String, ItemStack> items = new HashMap<>();
    private final Map<Integer, ItemStack> fillers = new HashMap<>();
    private final Inventory inventory;

    public ShopCategory(@NotNull String id, @NotNull String displayName, int size) {
        this.id = id;
        this.displayName = displayName;
        ShopInventoryHolder holder = new ShopInventoryHolder(id);
        this.inventory = Bukkit.createInventory(holder, size, displayName);

        holder.setInventory(this.inventory);
    }

    public void addItem(@NotNull String itemId, @NotNull ItemStack item, int slot) {
        items.put(itemId, item);
        inventory.setItem(slot, item);
    }

    public void addFiller(int slot, @NotNull ItemStack filler) {
        fillers.put(slot, filler);
        inventory.setItem(slot, filler);
    }

    public boolean isFiller(int slot) {
        return fillers.containsKey(slot);
    }
}