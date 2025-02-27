package net.coma112.axshop.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CurrencyProvider {
    boolean hasEnough(@NotNull Player player, double amount);
    void deduct(@NotNull Player player, double amount);
    void add(@NotNull Player player, double amount);
}
