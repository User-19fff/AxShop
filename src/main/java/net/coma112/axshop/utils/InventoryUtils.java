package net.coma112.axshop.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@UtilityClass
public class InventoryUtils {
    public boolean isInventoryFull(@NotNull Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public boolean hasAndRemove(@NotNull Player player, @NotNull Material item, int amount) {
        var inventory = player.getInventory();
        var contents = inventory.getContents();

        int totalAmount = 0;

        for (ItemStack content : contents) {
            if (content != null && content.getType() == item) totalAmount += content.getAmount();
        }

        if (totalAmount < amount) return false;

        for (int i = 0; i < contents.length; i++) {
            var content = contents[i];

            if (content != null && content.getType() == item) {
                if (content.getAmount() > amount) {
                    content.setAmount(content.getAmount() - amount);
                    return true;
                } else {
                    amount -= content.getAmount();
                    inventory.setItem(i, null);
                }
            }
        }

        return true;
    }

    public int countItems(@NotNull Player player, @NotNull Material item) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(invItem -> invItem != null && invItem.getType() == item)
                .mapToInt(ItemStack::getAmount)
                .sum();
    }
}