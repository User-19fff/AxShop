package net.coma112.axshop.commands;

import net.coma112.axshop.identifiers.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.locales.LocaleReader;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class CommandErrorHandler implements LocaleReader {
    @Override
    public boolean containsKey(@NotNull String string) {
        return true;
    }

    @Override
    public String get(@NotNull String string) {
        String result;

        switch (string) {
            case "missing-argument": {
                result = MessageKeys.MISSING_ARGUMENT.getMessage();
                break;
            }
            case "no-permission": {
                result = MessageKeys.NO_PERMISSION.getMessage();
                break;
            }
            case "must-be-player": {
                result = MessageKeys.PLAYER_REQUIRED.getMessage();
                break;
            }

            default: {
                result = "";
                break;
            }
        }

        return result;
    }

    private final Locale LOCALE = new Locale("en", "US");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }
}
