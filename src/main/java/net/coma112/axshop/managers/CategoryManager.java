package net.coma112.axshop.managers;

import lombok.Getter;
import net.coma112.axshop.holder.ShopHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("deprecation")
public final class CategoryManager {
    private final String id;
    private final String displayName;
    private final ConcurrentHashMap<String, ItemStack> items = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ItemStack> fillers = new ConcurrentHashMap<>();
    private final Inventory inventory;

    public CategoryManager(@NotNull String id, @NotNull String displayName, int size) {
        this.id = id;
        this.displayName = displayName;
        ShopHolder holder = new ShopHolder(id);
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