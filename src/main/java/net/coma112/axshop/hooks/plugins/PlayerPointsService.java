package net.coma112.axshop.hooks.plugins;

import lombok.Getter;
import net.coma112.axshop.utils.LoggerUtils;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerPointsService {
    @Getter private static PlayerPointsService instance = new PlayerPointsService();
    private PlayerPointsAPI api;
    private static boolean isRegistered = false;

    public void registerHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            api = org.black_ixx.playerpoints.PlayerPoints.getInstance().getAPI();
            isRegistered = true;
        }
    }

    public boolean hasEnoughMoney(@NotNull Player player, double amount) {
        return api != null && api.look(player.getUniqueId()) >= amount;
    }

    public void deductMoney(@NotNull Player player, double amount) {
        if (!isRegistered || api == null) {
            LoggerUtils.error("PlayerPoints is detected BUT not running!");
            return;
        }

        api.take(player.getUniqueId(), (int) amount);
    }

    public void addMoney(@NotNull Player player, double amount) {
        if (!isRegistered || api == null) {
            LoggerUtils.error("PlayerPoints is detected BUT not running!");
            return;
        }

        api.give(player.getUniqueId(), (int) amount);
    }
}