package net.coma112.axshop.identifiers.keys;

import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum MessageKeys {
    RELOAD("messages.reload"),

    MISSING_ARGUMENT("messages.missing-argument"),
    PLAYER_REQUIRED("messages.player-required"),

    NO_PERMISSION("messages.no-permission"),
    MENU_NOT_LOADED("messages.menu-not-loaded"),
    NOT_ENOUGH_MONEY("messages.not-enough-money"),
    NO_ITEM_FOUND("messages.no-item-found"),

    FULL_INVENTORY("messages.full-inventory"),

    DISABLED_WORLD("messages.disabled-world"),

    WORTH_ITEM("messages.worth.item"),
    WORTH_HEADER("messages.worth.header"),
    WORTH_TOTAL("messages.worth.total"),

    SUCCESS_BUY("messages.success-buy"),
    SUCCESS_SELL("messages.success-sell");

    private final String path;
    private static final AxShop PLUGIN = AxShop.getInstance();

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getMessage() {
        return MessageProcessor.process(PLUGIN.getLanguage().getString(path))
                .replace("%prefix%", MessageProcessor.process(PLUGIN.getLanguage().getString("prefix")));
    }

    public List<String> getMessages() {
        return PLUGIN.getLanguage().getStringList(path)
                .stream()
                .toList();

    }
}
