package net.coma112.axshop.currency;

import net.coma112.axshop.hooks.plugins.PlayerPointsService;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlayerPointsCurrency implements CurrencyProvider {
    @Override
    public boolean hasEnough(@NotNull Player player, double amount) {
        return PlayerPointsService.getInstance().hasEnoughMoney(player, amount);
    }

    @Override
    public void deduct(@NotNull Player player, double amount) {
        PlayerPointsService.getInstance().deductMoney(player, amount);
    }

    @Override
    public void add(@NotNull Player player, double amount) {
        PlayerPointsService.getInstance().addMoney(player, amount);
    }
}
