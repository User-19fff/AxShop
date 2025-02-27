package net.coma112.axshop.registry;

import net.coma112.axshop.currency.CurrencyBeastToken;
import net.coma112.axshop.currency.CurrencyPlayerPoints;
import net.coma112.axshop.currency.CurrencyVault;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyRegistry {
    private static final Map<CurrencyTypes, CurrencyProvider> providers = new ConcurrentHashMap<>();

    static {
        register(CurrencyTypes.VAULT, new CurrencyVault());
        register(CurrencyTypes.PLAYERPOINTS, new CurrencyPlayerPoints());
        register(CurrencyTypes.BEASTTOKEN, new CurrencyBeastToken());
    }

    public static void register(@NotNull CurrencyTypes name, @NotNull CurrencyProvider provider) {
        providers.put(name, provider);
    }

    @Nullable
    public static CurrencyProvider get(@NotNull CurrencyTypes currency) {
        return providers.get(currency);
    }
}