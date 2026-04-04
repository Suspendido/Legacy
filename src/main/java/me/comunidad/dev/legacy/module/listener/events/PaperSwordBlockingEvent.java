package me.comunidad.dev.legacy.module.listener.events;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * Copyright (c) 2026. @Comunidad, made since 2/4/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter
public class PaperSwordBlockingEvent {

    private final MethodHandle nmsApplyComponents;
    private final Object addConsumablePatch;
    private final Object removeConsumablePatch;
    private volatile Field craftItemStackHandleField;

    private final MethodHandle craftPlayerGetHandle;
    private final MethodHandle nmsGetUseItem;
    private final MethodHandle nmsItemStackIs;
    private final Object nmsSwordTag;

    private final boolean supported;

    public PaperSwordBlockingEvent() {
        boolean init = false;
        MethodHandle apply = null;
        Object addPatch = null;
        Object removePatch = null;
        MethodHandle getHandle = null;
        MethodHandle getUseItem = null;
        MethodHandle itemStackIs = null;
        Object swordTag = null;

        try {
            // Crear componente CONSUMABLE: Consumable.builder().consumeSeconds(MAX).animation(BLOCK).build()
            Class<?> nmsConsumable = Class.forName("net.minecraft.world.item.component.Consumable");
            Object builder = nmsConsumable.getMethod("builder").invoke(null);

            Class<?> nmsUseAnim = Class.forName("net.minecraft.world.item.ItemUseAnimation");
            Object blockAnim = nmsUseAnim.getField("BLOCK").get(null);

            Object temp1 = builder.getClass().getMethod("consumeSeconds", float.class)
                    .invoke(builder, Float.MAX_VALUE);
            Object temp2 = temp1.getClass().getMethod("animation", nmsUseAnim)
                    .invoke(temp1, blockAnim);
            Object consumableComponent = temp2.getClass().getMethod("build").invoke(temp2);

            // Crear patches
            Class<?> nmsDataComponents = Class.forName("net.minecraft.core.component.DataComponents");
            Object consumableType = nmsDataComponents.getField("CONSUMABLE").get(null);

            Class<?> nmsPatch = Class.forName("net.minecraft.core.component.DataComponentPatch");

            // Patch para agregar consumable
            Object patchBuilderAdd = nmsPatch.getMethod("builder").invoke(null);
            Method setMethod = findPatchSetMethod(patchBuilderAdd.getClass());
            setMethod.invoke(patchBuilderAdd, consumableType, consumableComponent);
            addPatch = patchBuilderAdd.getClass().getMethod("build").invoke(patchBuilderAdd);

            // Patch para remover consumable
            Object patchBuilderRemove = nmsPatch.getMethod("builder").invoke(null);
            Method removeMethod = findPatchRemoveMethod(patchBuilderRemove.getClass());
            removeMethod.invoke(patchBuilderRemove, consumableType);
            removePatch = patchBuilderRemove.getClass().getMethod("build").invoke(patchBuilderRemove);

            // ItemStack#applyComponents
            Class<?> nmsItemStack = Class.forName("net.minecraft.world.item.ItemStack");
            Method applyMethod = nmsItemStack.getMethod("applyComponents", nmsPatch);
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            apply = lookup.unreflect(applyMethod);

            // Para detectar sword blocking
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
            getHandle = lookup.unreflect(craftPlayer.getMethod("getHandle"));

            Class<?> nmsPlayer = Class.forName("net.minecraft.world.entity.player.Player");
            getUseItem = lookup.unreflect(nmsPlayer.getMethod("getUseItem"));

            swordTag = Class.forName("net.minecraft.tags.ItemTags").getField("SWORDS").get(null);
            itemStackIs = lookup.unreflect(findItemStackIsMethod(nmsItemStack, swordTag));

            init = true;
        } catch (Throwable e) {
            System.err.println("[Legacy] Paper sword blocking no disponible: " + e.getMessage());
        }

        this.supported = init;
        this.nmsApplyComponents = apply;
        this.addConsumablePatch = addPatch;
        this.removeConsumablePatch = removePatch;
        this.craftPlayerGetHandle = getHandle;
        this.nmsGetUseItem = getUseItem;
        this.nmsItemStackIs = itemStackIs;
        this.nmsSwordTag = swordTag;
    }

    public void applyComponents(ItemStack stack) {
        if (!supported || stack == null || stack.getType() == Material.AIR) return;
        if (!isSword(stack.getType())) return;

        try {
            Field handleField = resolveCraftItemStackHandleField(stack);
            Object nms = handleField.get(stack);
            if (nms != null) {
                nmsApplyComponents.invoke(nms, addConsumablePatch);
            }
        } catch (Throwable ignored) {
        }
    }

    public void clearComponents(ItemStack stack) {
        if (!supported || stack == null) return;

        try {
            Field handleField = resolveCraftItemStackHandleField(stack);
            Object nms = handleField.get(stack);
            if (nms != null) {
                nmsApplyComponents.invoke(nms, removeConsumablePatch);
            }
        } catch (Throwable ignored) {
        }
    }

    public boolean isBlockingSword(Player player) {
        if (!supported || player == null) return false;

        try {
            Object nmsPlayer = craftPlayerGetHandle.invoke(player);
            if (nmsPlayer == null) return false;

            Object useItem = nmsGetUseItem.invoke(nmsPlayer);
            if (useItem == null) return false;

            return (boolean) nmsItemStackIs.invoke(useItem, nmsSwordTag);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Field resolveCraftItemStackHandleField(ItemStack stack) throws NoSuchFieldException {
        Field cached = craftItemStackHandleField;
        if (cached != null) return cached;

        Class<?> c = stack.getClass();
        while (c != null && c != Object.class) {
            try {
                Field f = c.getDeclaredField("handle");
                f.setAccessible(true);
                craftItemStackHandleField = f;
                return f;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException("No 'handle' field found");
    }

    private Method findPatchSetMethod(Class<?> builderClass) throws NoSuchMethodException {
        for (Method m : builderClass.getMethods()) {
            if (m.getName().equals("set") && m.getParameterCount() == 2) return m;
        }
        throw new NoSuchMethodException("DataComponentPatch.Builder#set not found");
    }

    private Method findPatchRemoveMethod(Class<?> builderClass) throws NoSuchMethodException {
        for (Method m : builderClass.getMethods()) {
            if (m.getName().equals("remove") && m.getParameterCount() == 1) return m;
        }
        throw new NoSuchMethodException("DataComponentPatch.Builder#remove not found");
    }

    private Method findItemStackIsMethod(Class<?> nmsItemStackClass, Object tagInstance) throws NoSuchMethodException {
        for (Method m : nmsItemStackClass.getMethods()) {
            if (!m.getName().equals("is")) continue;
            if (m.getParameterCount() != 1) continue;
            if (m.getReturnType() != boolean.class) continue;

            Class<?> param = m.getParameterTypes()[0];
            if (param.isInstance(tagInstance) || param.getName().contains("TagKey")) return m;
        }
        throw new NoSuchMethodException("ItemStack#is(tag) not found");
    }

    private boolean isSword(Material mat) {
        return mat != null && mat.name().endsWith("_SWORD");
    }
}