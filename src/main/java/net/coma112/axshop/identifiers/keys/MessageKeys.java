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

    NO_PERMISSION("messages.no-permission");

    private final String path;

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getMessage() {
        return MessageProcessor.process(AxShop.getInstance().getLanguage().getString(path))
                .replace("%prefix%", AxShop.getInstance().getLanguage().getString("prefix"));
    }

    public List<String> getMessages() {
        return AxShop.getInstance().getLanguage().getStringList(path)
                .stream()
                .toList();

    }
}
