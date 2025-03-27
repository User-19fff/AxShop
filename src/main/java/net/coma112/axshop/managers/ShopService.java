package net.coma112.axshop.managers;

import lombok.Getter;
import net.coma112.axshop.utils.shop.ServiceUtils;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public final class ShopService {
    @Getter private static final ShopService instance = new ShopService();

    public void initialize() {
        ServiceUtils.loadMainMenu();
        ServiceUtils.loadShops();
    }

    @NotNull
    public Optional<Inventory> getMenu(@NotNull String menuId) {
        return Optional.ofNullable(ServiceUtils.menus.get(menuId));
    }

    public Inventory getMainMenu() {
        return ServiceUtils.mainMenuInventory;
    }

    @NotNull
    public Optional<CategoryManager> getCategory(@NotNull String name) {
        return Optional.ofNullable(ServiceUtils.categories.get(name));
    }
}