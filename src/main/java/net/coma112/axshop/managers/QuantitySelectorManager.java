package net.coma112.axshop.managers;

import com.artillexstudios.axapi.config.Config;
import lombok.Getter;
import net.coma112.axshop.AxShop;
import net.coma112.axshop.holder.QuantitySelectorHolder;
import net.coma112.axshop.item.ItemFactory;
import net.coma112.axshop.processor.MessageProcessor;
import net.coma112.axshop.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public final class QuantitySelectorManager {
    @Getter private static final QuantitySelectorManager instance = new QuantitySelectorManager();

    private final Config config;
    private final ConcurrentHashMap<Integer, Integer> decreaseAmounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> increaseAmounts = new ConcurrentHashMap<>();
    private int confirmSlot;
    private int cancelSlot;
    private int quantityDisplaySlot;
    private int previewSlot;

    private QuantitySelectorManager() {
        this.config = AxShop.getInstance().getConfiguration();
        loadConfig();
    }

    private void loadConfig() {
        config.getSection("quantity-selector.decrease-buttons").getRoutesAsStrings(false).forEach(key -> {
            try {
                String path = "quantity-selector.decrease-buttons." + key;
                int slot = config.getInt(path + ".slot");
                int amount = Integer.parseInt(key.split("-")[1]);
                decreaseAmounts.put(slot, amount);
            } catch (Exception exception) {
                LoggerUtils.warn(exception.getMessage());
            }
        });

        config.getSection("quantity-selector.increase-buttons").getRoutesAsStrings(false).forEach(key -> {
            try {
                String path = "quantity-selector.increase-buttons." + key;
                int slot = config.getInt(path + ".slot");
                int amount = Integer.parseInt(key.split("-")[1]);
                increaseAmounts.put(slot, amount);
            } catch (Exception exception) {
                LoggerUtils.warn(exception.getMessage());
            }
        });

        this.confirmSlot = config.getInt("quantity-selector.confirm.slot");
        this.cancelSlot = config.getInt("quantity-selector.cancel.slot");
        this.quantityDisplaySlot = config.getInt("quantity-selector.quantity-display.slot");
        this.previewSlot = config.getInt("quantity-selector.preview-slot");
    }

    public void openQuantitySelector(@NotNull Player player, @NotNull ItemStack item, int buyPrice, @NotNull String currency) {
        String title = MessageProcessor.process(config.getString("quantity-selector.name"));
        int size = config.getInt("quantity-selector.size");
        QuantitySelectorHolder holder = new QuantitySelectorHolder("quantity-selector", item, buyPrice, currency);
        Inventory inventory = Bukkit.createInventory(holder, size, title);

        holder.setInventory(inventory);
        updateInventory(holder);
        player.openInventory(inventory);
    }

    public void updateInventory(@NotNull QuantitySelectorHolder holder) {
        Inventory inventory = holder.getInventory();
        inventory.clear();

        ItemStack previewItem = holder.getItem().clone();
        int quantity = holder.getQuantity();

        previewItem.setAmount(Math.min(quantity, 64));

        if (quantity > 64) {
            ItemMeta meta = previewItem.getItemMeta();
            if (meta != null) {
                String itemName = meta.hasDisplayName() ? meta.getDisplayName() : "";
                itemName = MessageProcessor.process(itemName + " &7(x" + quantity + ")");
                meta.setDisplayName(itemName);

                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(MessageProcessor.process("&7Total quantity: &e" + quantity));
                meta.setLore(lore);
                previewItem.setItemMeta(meta);
            }
        }

        inventory.setItem(previewSlot, previewItem);

        ItemStack quantityDisplay = createQuantityDisplay(holder);
        inventory.setItem(quantityDisplaySlot, quantityDisplay);

        decreaseAmounts.forEach((slot, amount) -> {
            String key = "decrease-" + amount;
            String path = "quantity-selector.decrease-buttons." + key;
            ItemStack button = createButton(path, holder);
            inventory.setItem(slot, button);
        });

        increaseAmounts.forEach((slot, amount) -> {
            String key = "increase-" + amount;
            String path = "quantity-selector.increase-buttons." + key;
            ItemStack button = createButton(path, holder);
            inventory.setItem(slot, button);
        });

        ItemStack confirmButton = createButton("quantity-selector.confirm", holder);
        inventory.setItem(confirmSlot, confirmButton);

        ItemStack cancelButton = createButton("quantity-selector.cancel", holder);
        inventory.setItem(cancelSlot, cancelButton);

        if (!config.getString("quantity-selector.filler").isEmpty()) {
            Material fillerMaterial = Material.valueOf(config.getString("quantity-selector.filler.material").toUpperCase());
            String fillerName = MessageProcessor.process(config.getString("quantity-selector.filler.name"));
            ItemStack filler = ItemFactory.createFillerItem(fillerMaterial, fillerName, new ArrayList<>());

            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) inventory.setItem(i, filler);
            }
        }
    }

    @NotNull
    private ItemStack createQuantityDisplay(@NotNull QuantitySelectorHolder holder) {
        String path = "quantity-selector.quantity-display";
        Material material = Material.valueOf(config.getString(path + ".material").toUpperCase());

        String name = config.getString(path + ".name", "")
                .replace("{quantity}", String.valueOf(holder.getQuantity()))
                .replace("{total_price}", String.valueOf(holder.getTotalPrice()));
        name = MessageProcessor.process(name);

        List<String> lore = config.getStringList(path + ".lore");
        List<String> processedLore = new ArrayList<>();
        for (String line : lore) {
            line = line.replace("{quantity}", String.valueOf(holder.getQuantity()))
                    .replace("{total_price}", String.valueOf(holder.getTotalPrice()));
            processedLore.add(MessageProcessor.process(line));
        }

        return ItemFactory.createFillerItem(material, name, processedLore);
    }

    @NotNull
    private ItemStack createButton(String path, @NotNull QuantitySelectorHolder holder) {
        Material material = Material.valueOf(config.getString(path + ".material").toUpperCase());

        String name = config.getString(path + ".name", "")
                .replace("{quantity}", String.valueOf(holder.getQuantity()))
                .replace("{total_price}", String.valueOf(holder.getTotalPrice()));
        name = MessageProcessor.process(name);

        List<String> lore = config.getStringList(path + ".lore");
        List<String> processedLore = new ArrayList<>();
        for (String line : lore) {
            line = line.replace("{quantity}", String.valueOf(holder.getQuantity()))
                    .replace("{total_price}", String.valueOf(holder.getTotalPrice()));
            processedLore.add(MessageProcessor.process(line));
        }

        return ItemFactory.createFillerItem(material, name, processedLore);
    }

    public boolean isDecreaseButton(int slot) {
        return decreaseAmounts.containsKey(slot);
    }

    public boolean isIncreaseButton(int slot) {
        return increaseAmounts.containsKey(slot);
    }

    public int getDecreaseAmount(int slot) {
        return decreaseAmounts.getOrDefault(slot, 0);
    }

    public int getIncreaseAmount(int slot) {
        return increaseAmounts.getOrDefault(slot, 0);
    }

    public boolean isConfirmButton(int slot) {
        return slot == confirmSlot;
    }

    public boolean isCancelButton(int slot) {
        return slot == cancelSlot;
    }
}