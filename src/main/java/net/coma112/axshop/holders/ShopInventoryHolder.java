package net.coma112.axshop.holders;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ShopInventoryHolder implements InventoryHolder {
    @Getter
    private final String shopType;
    private final Inventory inventory;

    public ShopInventoryHolder(String shopType, int size) {
        this.shopType = shopType;
        this.inventory = Bukkit.createInventory(this, size, "Shop - " + shopType);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}