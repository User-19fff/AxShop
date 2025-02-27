package net.coma112.axshop.registry;

import net.coma112.axshop.currency.CurrencyBeastToken;
import net.coma112.axshop.currency.CurrencyPlayerPoints;
import net.coma112.axshop.currency.CurrencyVault;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyRegistry {
    private static final Map<String, CurrencyProvider> providers = new ConcurrentHashMap<>();

    static {
        register("vault", new CurrencyVault());
        register("playerpoints", new CurrencyPlayerPoints());
        register("beasttokens", new CurrencyBeastToken());
    }

    public static void register(@NotNull String name, @NotNull CurrencyProvider provider) {
        providers.put(name.toLowerCase(), provider);
    }

    @Nullable
    public static CurrencyProvider get(@NotNull String name) {
        return providers.get(name.toLowerCase());
    }
}