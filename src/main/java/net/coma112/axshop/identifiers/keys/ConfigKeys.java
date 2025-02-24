package net.coma112.axshop.identifiers.keys;

import com.artillexstudios.axapi.config.Config;
import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum ConfigKeys {
    ALIASES("aliases");

    private final String path;
    private static final Config config = AxShop.getInstance().getConfiguration();

    ConfigKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getString() {
        return MessageProcessor.process(config.getString(path));
    }

    public static @NotNull String getString(@NotNull String path) {
        return config.getString(path);
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public int getInt() {
        return config.getInt(path);
    }

    public List<String> getList() {
        return config.getList(path);
    }
}
