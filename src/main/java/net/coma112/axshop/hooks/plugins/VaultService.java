package net.coma112.axshop.hooks.plugins;

import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultService {
    @Getter private static VaultService instance = new VaultService();
    @Getter private static Economy economy = null;
    private static boolean isRegistered = false;

    public void registerHook() {
        RegisteredServiceProvider<Economy> rsp = AxShop.getInstance().getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            economy = rsp.getProvider();
            isRegistered = true;
        }
    }

    public boolean hasEnoughMoney(@NotNull Player player, double amount) {
        return isRegistered && economy.getBalance(player) >= amount;
    }

    public void deductMoney(@NotNull Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void addMoney(@NotNull Player player, double amount) {
        if (isRegistered) economy.depositPlayer(player, amount);
    }
}