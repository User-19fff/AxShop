package net.coma112.axshop.currency;

import net.coma112.axshop.hooks.plugins.VaultService;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class VaultCurrency implements CurrencyProvider {
    @Override
    public boolean hasEnough(@NotNull Player player, double amount) {
        return VaultService.getInstance().hasEnoughMoney(player, amount);
    }

    @Override
    public void deduct(@NotNull Player player, double amount) {
        VaultService.getInstance().deductMoney(player, amount);
    }

    @Override
    public void add(@NotNull Player player, double amount) {
        VaultService.getInstance().addMoney(player, amount);
    }
}
