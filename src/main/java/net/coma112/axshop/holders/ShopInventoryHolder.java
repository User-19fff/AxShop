package net.coma112.axshop.holders;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public class ShopInventoryHolder implements InventoryHolder {
    private final String shopType;
    @Setter private Inventory inventory;

    public ShopInventoryHolder(@NotNull String shopType) {
        this.shopType = shopType;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}