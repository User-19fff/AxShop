package net.coma112.axshop.processor;

import lombok.experimental.UtilityClass;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.identifiers.CurrencyTypes;
import net.coma112.axshop.identifiers.FormatTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class LoreProcessor {
    public List<String> processLore(@NotNull List<String> lore) {
        return lore.stream()
                .map(MessageProcessor::process)
                .collect(Collectors.toList());
    }

    public List<String> processItemLore(@NotNull List<String> lore, int buyPrice, int sellPrice, @NotNull CurrencyTypes currency) {
        return lore.stream()
                .map(line -> line
                        .replace("{buyPrice}", FormatTypes.format(buyPrice))
                        .replace("{sellPrice}", FormatTypes.format(sellPrice))
                        .replace("{currency}", Objects.requireNonNull(AxShop.getInstance().getConfig().getString("currencies." + currency.name().toLowerCase()))))
                .map(MessageProcessor::process)
                .collect(Collectors.toList());
    }
}