package net.coma112.axshop.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class InventoryUtils {
    public boolean isInventoryFull(@NotNull Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void hasAndRemoveOne(@NotNull Player player, @NotNull ItemStack item) {
        var inventory = player.getInventory();
        var contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            var content = contents[i];

            if (content != null && content.isSimilar(item)) {
                if (content.getAmount() > 1) content.setAmount(content.getAmount() - 1);
                else inventory.setItem(i, null);
                return;
            }
        }
    }
}
