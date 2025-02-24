package net.coma112.axshop.item;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.processor.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("deprecation")
public interface ItemFactory {
    @Contract("_ -> new")
    static @NotNull ItemFactory create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    @Contract("_, _ -> new")
    static @NotNull ItemFactory create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    @Contract("_, _, _ -> new")
    static @NotNull ItemFactory create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    @Contract("_ -> new")
    static @NotNull ItemFactory create(ItemStack item) {
        return new ItemBuilder(item);
    }

    ItemFactory setType(@NotNull Material material);

    ItemFactory setCount(int newCount);

    ItemFactory setName(@NotNull String name);

    void addEnchantment(@NotNull Enchantment enchantment, int level);

    default ItemFactory addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    ItemBuilder addLore(@NotNull String... lores);

    ItemFactory setUnbreakable();

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays.stream(flags).forEach(this::addFlag);
    }

    ItemFactory removeLore(int line);

    ItemStack finish();

    boolean isFinished();

    private static Optional<ItemStack> buildItem(@NotNull Section section) {
        return Optional.ofNullable(section.getString("material"))
                .map(Material::valueOf)
                .map(material -> {
                    int amount = section.getInt("amount", 1);
                    String name = section.getString("displayname", "");
                    ItemStack item = ItemFactory.create(material, amount)
                            .setName(name)
                            .addLore(section.getStringList("lore")
                                    .stream()
                                    .map(MessageProcessor::process).toArray(String[]::new))
                            .finish();

                    if (section.contains("model-data")) item.editMeta(meta -> meta.setCustomModelData(section.getInt("model-data")));

                    if (section.contains("enchantments")) {
                        section.getStringList("enchantments").forEach(enchantString -> {
                            String[] parts = enchantString.split(":");

                            if (parts.length == 2) {
                                try {
                                    Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                                    int level = Integer.parseInt(parts[1]);
                                    if (enchantment != null) item.addUnsafeEnchantment(enchantment, level);
                                } catch (NumberFormatException ignored) {}
                            }
                        });
                    }

                    if (section.contains("flags")) {
                        item.editMeta(meta -> {
                            List<String> flagStrings = section.getStringList("flags");
                            flagStrings.forEach(flagString -> meta.addItemFlags(ItemFlag.valueOf(flagString.toUpperCase())));
                        });
                    }

                    String texture = section.getString("texture");

                    if (material == Material.PLAYER_HEAD && texture != null) applyTextureToSkullMeta(item, texture);
                    return item;
                });
    }

    static Optional<ItemStack> createItemFromString(@NotNull String path) {
        return Optional.ofNullable(AxShop.getInstance().getConfiguration().getSection(path))
                .flatMap(ItemFactory::buildItem);
    }

    private static void applyTextureToSkullMeta(@NotNull ItemStack item, @NotNull String texture) {
        item.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));

            playerProfile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(playerProfile);
        });
    }
}
