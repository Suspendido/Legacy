package me.comunidad.dev.legacy.framework.menu.button;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Button {

    public abstract void onClick(InventoryClickEvent e);

    public abstract ItemStack getItemStack();

}