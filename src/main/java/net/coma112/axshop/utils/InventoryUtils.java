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

    public void hasAndRemove(@NotNull Player player, @NotNull Material item, int amount) {
        var inventory = player.getInventory();
        var contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            var content = contents[i];

            if (content != null && content.isSimilar(ItemStack.of(item))) {
                if (content.getAmount() > 1) content.setAmount(content.getAmount() - amount);
                else inventory.setItem(i, null);
                return;
            }
        }
    }

    public int countItems(@NotNull Player player, @NotNull Material item) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(invItem -> invItem != null && invItem.isSimilar(ItemStack.of(item)))
                .mapToInt(ItemStack::getAmount)
                .sum();
    }
}
