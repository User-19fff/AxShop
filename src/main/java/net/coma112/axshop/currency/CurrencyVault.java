package net.coma112.axshop.currency;

import net.coma112.axshop.interfaces.CurrencyProvider;
import net.coma112.axshop.hooks.plugins.Vault;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CurrencyVault implements CurrencyProvider {
    @Override
    public boolean hasEnough(@NotNull Player player, double amount) {
        return Vault.getInstance().hasEnoughMoney(player, amount);
    }

    @Override
    public void deduct(@NotNull Player player, double amount) {
        Vault.getInstance().deductMoney(player, amount);
    }

    @Override
    public void add(@NotNull Player player, double amount) {
        Vault.getInstance().addMoney(player, amount);
    }
}
