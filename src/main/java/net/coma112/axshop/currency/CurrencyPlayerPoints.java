package net.coma112.axshop.currency;

import net.coma112.axshop.interfaces.CurrencyProvider;
import net.coma112.axshop.hooks.plugins.PlayerPoints;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CurrencyPlayerPoints implements CurrencyProvider {
    @Override
    public boolean hasEnough(@NotNull Player player, double amount) {
        return PlayerPoints.getInstance().hasEnoughMoney(player, amount);
    }

    @Override
    public void deduct(@NotNull Player player, double amount) {
        PlayerPoints.getInstance().deductMoney(player, amount);
    }

    @Override
    public void add(@NotNull Player player, double amount) {
        PlayerPoints.getInstance().addMoney(player, amount);
    }
}
