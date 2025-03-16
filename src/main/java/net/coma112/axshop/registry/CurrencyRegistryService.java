package net.coma112.axshop.registry;

import net.coma112.axshop.currency.BeastTokenCurrency;
import net.coma112.axshop.currency.PlayerPointsCurrency;
import net.coma112.axshop.currency.VaultCurrency;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.interfaces.CurrencyProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyRegistryService {
    private static final ConcurrentHashMap<CurrencyTypes, CurrencyProvider> providers = new ConcurrentHashMap<>();

    static {
        register(CurrencyTypes.VAULT, new VaultCurrency());
        register(CurrencyTypes.PLAYERPOINTS, new PlayerPointsCurrency());
        register(CurrencyTypes.BEASTTOKEN, new BeastTokenCurrency());
    }

    public static void register(@NotNull CurrencyTypes name, @NotNull CurrencyProvider provider) {
        providers.put(name, provider);
    }

    @Nullable
    public static CurrencyProvider get(@NotNull CurrencyTypes currency) {
        return providers.get(currency);
    }
}