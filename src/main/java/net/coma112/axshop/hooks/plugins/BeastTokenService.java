package net.coma112.axshop.hooks.plugins;

import lombok.Getter;
import me.mraxetv.beasttokens.api.BeastTokensAPI;
import me.mraxetv.beasttokens.api.handlers.BTTokensManager;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BeastTokenService {
    @Getter private static final BeastTokenService instance = new BeastTokenService();
    private BTTokensManager api;
    private static boolean isRegistered = false;

    public void registerHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("BeastTokens")) {
            api = BeastTokensAPI.getTokensManager();
            isRegistered = true;
        }
    }

    public boolean hasEnoughMoney(@NotNull Player player, double amount) {
        return api != null && api.getTokens(player) >= amount;
    }

    public void deductMoney(@NotNull Player player, double amount) {
        if (!isRegistered || api == null) {
            LoggerUtils.error("BeastTokens is detected BUT not running!");
            return;
        }

        api.removeTokens(player, amount);
    }

    public void addMoney(@NotNull Player player, double amount) {
        if (!isRegistered || api == null) {
            LoggerUtils.error("BeastTokens is detected BUT not running!");
            return;
        }

        api.addTokens(player, amount);
    }
}