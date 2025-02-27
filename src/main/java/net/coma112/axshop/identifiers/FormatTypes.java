package net.coma112.axshop.identifiers;

import net.coma112.axshop.identifiers.keys.ConfigKeys;
import net.coma112.axshop.utils.RegisterUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum FormatTypes {
    BASIC,
    COMMAS,
    DOT;

    public static String format(int price) {
        if (!ConfigKeys.FORMATTING_ENABLED.getBoolean()) return String.valueOf(price);

        return switch (FormatTypes.valueOf(ConfigKeys.FORMATTING_TYPE.getString().toUpperCase())) {
            case DOT -> String
                    .format("%,d", price)
                    .replace(",", ".");
            case COMMAS -> String.format("%,d", price);
            case BASIC -> {
                List<Map.Entry<Long, String>> sortedEntries = Collections.synchronizedList(new ArrayList<>(RegisterUtils.getBasicFormatOverrides().entrySet()));

                sortedEntries.sort(Collections.reverseOrder(Map.Entry.comparingByKey()));

                yield sortedEntries
                        .stream()
                        .filter(entry -> price >= entry.getKey())
                        .findFirst()
                        .map(entry -> {
                            double formattedPrice = (double) price / entry.getKey();

                            return new DecimalFormat("#.#").format(formattedPrice) + entry.getValue();
                        })
                        .orElse(String.valueOf(price));
            }
        };
    }
}