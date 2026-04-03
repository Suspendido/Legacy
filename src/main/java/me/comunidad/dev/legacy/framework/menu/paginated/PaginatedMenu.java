package me.comunidad.dev.legacy.framework.menu.paginated;

import me.comunidad.dev.legacy.framework.menu.Menu;
import me.comunidad.dev.legacy.framework.menu.MenuManager;
import me.comunidad.dev.legacy.framework.menu.button.Button;
import me.comunidad.dev.legacy.utils.ItemBuilder;
import me.comunidad.dev.legacy.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class PaginatedMenu extends Menu {

    protected final Map<Integer, Map<Integer, Button>> pages;
    protected final Map<Integer, Button> defaultButtons;
    protected int currentPage;

    public PaginatedMenu(MenuManager manager, Player player, String title, int size, boolean update) {
        super(manager, player, title, size, update);
        this.pages = new HashMap<>();
        this.defaultButtons = getDefaultButtons();
        this.currentPage = 1;
    }

    @Override
    public void open() {
        // Calculate pages based on current buttons
        this.calcPages(getButtons(player));
        this.buttons = pages.get(currentPage);
        this.inventory = Bukkit.createInventory(null, size, title);

        // Paginated buttons
        for (int i = 1; i < 8; i++) {
            ItemStack item = new ItemBuilder(ItemUtils.getMatItem(getConfig().getString("PAGINATED_MENUS.FILLER.MATERIAL")))
                    .setName(getConfig().getString("PAGINATED_MENUS.FILLER.NAME"))
                    .data(getManager(), getConfig().getInt("PAGINATED_MENUS.FILLER.DATA"))
                    .setLore(getConfig().getStringList("PAGINATED_MENUS.FILLER.LORE"))
                    .toItemStack();
            ItemMeta meta = item.getItemMeta();
            meta.setHideTooltip(true);
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
        }

        // Open and add to map
        player.openInventory(inventory);
        getManager().getMenus().put(player.getUniqueId(), this);
    }

    @Override
    public void update() {
        if (!player.isOnline()) {
            destroy(); // They aren't online so just destroy it.
            return;
        }

        // Clear
        inventory.clear();
        pages.clear();

        // "Re-Fetch" the buttons and cache them again
        this.calcPages(getButtons(player));
        this.buttons = pages.get(currentPage);

        // Update the items in our inventory
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey() - 1, entry.getValue().getItemStack());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        pages.clear();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return null;
    }

    private void calcPages(Map<Integer, Button> buttons) {
        int totalPage = 1;
        int i = 9;

        if (buttons.isEmpty()) {
            pages.computeIfAbsent(totalPage, k -> new HashMap<>(defaultButtons));
            return;
        }

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            Map<Integer, Button> pageButtons = pages.computeIfAbsent(totalPage, k -> new HashMap<>(defaultButtons));

            int add = totalPage * 9;
            int minus = (totalPage > 1 ? (totalPage - 1) * inventory.getSize() : 0);
            pageButtons.put((entry.getKey() + add) - minus, entry.getValue());
            i++;

            if (i >= inventory.getSize()) {
                i = 9;
                totalPage++;
            }
        }
    }

    private Map<Integer, Button> getDefaultButtons() {
        Map<Integer, Button> map = new HashMap<>();

        for (int i = 2; i <= 8; i++) {
            map.put(i, new Button() {
                @Override
                public void onClick(InventoryClickEvent e) {
                    e.setCancelled(true);
                }

                @Override
                public ItemStack getItemStack() {
                    ItemStack item = new ItemBuilder(ItemUtils.getMatItem(getConfig().getString("PAGINATED_MENUS.FILLER.MATERIAL")))
                            .setName(getConfig().getString("PAGINATED_MENUS.FILLER.NAME"))
                            .data(getManager(), getConfig().getInt("PAGINATED_MENUS.FILLER.DATA"))
                            .setLore(getConfig().getStringList("PAGINATED_MENUS.FILLER.LORE"))
                            .toItemStack();
                    ItemMeta meta = item.getItemMeta();
                    meta.setHideTooltip(true);
                    item.setItemMeta(meta);
                    return item;
                }
            });
        }

        map.put(1, new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);

                // At first page no going back
                if (currentPage > 1) {
                    currentPage--;
                    update();
                    getManager().playSound(player, getConfig().getString("PAGINATED_MENUS.LAST_PAGE.SOUND"), false);
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(ItemUtils.getMatItem(getConfig().getString("PAGINATED_MENUS.LAST_PAGE.MATERIAL")))
                        .setName(getConfig().getString("PAGINATED_MENUS.LAST_PAGE.NAME"))
                        .data(getManager(), getConfig().getInt("PAGINATED_MENUS.LAST_PAGE.DATA"))
                        .setLore(getConfig().getStringList("PAGINATED_MENUS.LAST_PAGE.LORE"))
                        .toItemStack();
            }
        });

        map.put(9, new Button() {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.setCancelled(true);

                // Check if the next page exists
                if (pages.containsKey(currentPage + 1)) {
                    currentPage++;
                    update();
                    getManager().playSound((Player) e.getWhoClicked(), getConfig().getString("PAGINATED_MENUS.NEXT_PAGE.SOUND"), false);
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemBuilder(ItemUtils.getMatItem(getConfig().getString("PAGINATED_MENUS.NEXT_PAGE.MATERIAL")))
                        .setName(getConfig().getString("PAGINATED_MENUS.NEXT_PAGE.NAME"))
                        .data(getManager(), getConfig().getInt("PAGINATED_MENUS.NEXT_PAGE.DATA"))
                        .setLore(getConfig().getStringList("PAGINATED_MENUS.NEXT_PAGE.LORE"))
                        .toItemStack();
            }
        });

        return map;
    }
}