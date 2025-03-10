package net.coma112.axshop.holder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public final class QuantitySelectorHolder implements InventoryHolder {
    private final String shopType;
    private final ItemStack item;
    private final int buyPrice;
    private final String currency;

    @Setter
    private int quantity = 1;

    @Setter
    private Inventory inventory;

    public QuantitySelectorHolder(@NotNull String shopType, @NotNull ItemStack item, int buyPrice, @NotNull String currency) {
        this.shopType = shopType;
        this.item = item;
        this.buyPrice = buyPrice;
        this.currency = currency;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public int getTotalPrice() {
        return buyPrice * quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity = Math.max(1, Math.min(this.quantity + amount, 64 * 36));
    }

    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(1, this.quantity - amount);
    }
}
