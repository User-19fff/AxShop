package net.coma112.axshop.currency;

import net.coma112.axshop.hooks.plugins.BeastToken;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CurrencyBeastToken implements CurrencyProvider {
    @Override
    public boolean hasEnough(@NotNull Player player, double amount) {
        return BeastToken.getInstance().hasEnoughMoney(player, amount);
    }

    @Override
    public void deduct(@NotNull Player player, double amount) {
        BeastToken.getInstance().deductMoney(player, amount);
    }

    @Override
    public void add(@NotNull Player player, double amount) {
        BeastToken.getInstance().addMoney(player, amount);
    }
}
