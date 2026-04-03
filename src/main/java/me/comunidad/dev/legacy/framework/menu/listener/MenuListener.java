package me.comunidad.dev.legacy.framework.menu.listener;

import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MenuListener extends Module<MenuManager> {

    public MenuListener(MenuManager manager) {
        super(manager);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        Menu menu = getManager().getMenus().get(player.getUniqueId());

        if (menu != null) {
            if (!menu.isAllowInteract()) {
                e.setCancelled(true);
            }

            menu.onClick(e);

            if (e.getClickedInventory() != player.getInventory()) {
                Button button = menu.getButtons().get(e.getSlot() + 1);
                if (button != null) button.onClick(e);
            }

            if (e.getClickedInventory() == player.getInventory()) {
                menu.onClickOwn(e);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Menu menu = getManager().getMenus().get(player.getUniqueId());

        if (menu != null) {
            menu.onClose();
            menu.destroy();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Menu menu = getManager().getMenus().remove(player.getUniqueId());

        if (menu != null) {
            menu.onClose();
            menu.destroy();
        }
    }
}