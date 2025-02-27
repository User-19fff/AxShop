package net.coma112.axshop.handlers;

import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.identifiers.keys.MessageKeys;
import net.coma112.axshop.registry.CurrencyRegistry;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CurrencyHandler {
    public static void deduct(@NotNull Player player, double amount, @NotNull CurrencyTypes currency) {
        var provider = CurrencyRegistry.get(currency);

        if (provider == null) {
            LoggerUtils.error(currency + " is not supported!");
            return;
        }

        if (!provider.hasEnough(player, amount)) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return;
        }

        provider.deduct(player, amount);
    }

    public static void add(@NotNull Player player, double amount, @NotNull CurrencyTypes currency) {
        var provider = CurrencyRegistry.get(currency);

        if (provider == null) {
            LoggerUtils.error(currency + " is not supported!");
            return;
        }

        provider.add(player, amount);
    }
}
