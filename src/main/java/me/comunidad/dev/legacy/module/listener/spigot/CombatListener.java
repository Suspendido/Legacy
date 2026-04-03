package me.comunidad.dev.legacy.module.listener.spigot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.comunidad.dev.legacy.framework.Module;
import me.comunidad.dev.legacy.module.combat.ProfileManager;
import me.comunidad.dev.legacy.module.listener.events.PaperSwordBlockingEvent;
import me.comunidad.dev.legacy.module.listener.ListenerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CombatListener extends Module<ListenerManager> {

    private final Set<String> blockedSounds = new HashSet<>();
    private final Set<UUID> sweepPrimedAttackers = new HashSet<>();
    private final PaperSwordBlockingEvent swordBlocking;
    private final EntityDamageEvent.DamageCause sweepCause;
    private static final int DEFAULT_NO_DAMAGE_TICKS = 20;

    public CombatListener(ListenerManager manager) {
        super(manager);

        blockedSounds.add("entity.player.attack.strong");
        blockedSounds.add("entity.player.attack.sweep");
        blockedSounds.add("entity.player.attack.nodamage");
        blockedSounds.add("entity.player.attack.knockback");
        blockedSounds.add("entity.player.attack.crit");
        blockedSounds.add("entity.player.attack.weak");

        EntityDamageEvent.DamageCause tmp;
        try {
            tmp = EntityDamageEvent.DamageCause.valueOf("ENTITY_SWEEP_ATTACK");
        } catch (Exception e) {
            tmp = null;
        }
        this.sweepCause = tmp;

        PaperSwordBlockingEvent temp = null;
        try {
            temp = new PaperSwordBlockingEvent();
            if (!temp.isSupported()) temp = null;
        } catch (Exception ignored) {}
        this.swordBlocking = temp;

        // Apply no-damage ticks to all already-loaded entities using current profile
        applyNoDamageTicksToWorld();
        cancelAttackSound();
        cancelSweep();
    }

    private void applyNoDamageTicksToWorld() {
        int playerTicks = getInstance().getProfileManager().playerNoDamageTicks;
        int mobTicks = getInstance().getProfileManager().mobNoDamageTicks;
        Bukkit.getWorlds().forEach(world ->
                world.getLivingEntities().forEach(entity -> {
                    if (entity instanceof Player) {
                        entity.setMaximumNoDamageTicks(playerTicks);
                    } else {
                        entity.setMaximumNoDamageTicks(mobTicks);
                    }
                })
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(getInstance(), () -> setPlayerAttackSpeed(e.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(getInstance(), () -> setPlayerAttackSpeed(e.getPlayer()), 1L);
    }

    private void setPlayerAttackSpeed(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr != null) {
            attr.setBaseValue(getInstance().getProfileManager().attackSpeed);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSweep(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;

        if (sweepCause != null && e.getCause() == sweepCause) {
            e.setCancelled(true);
            return;
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (!isSword(weapon.getType())) return;

        if (sweepPrimedAttackers.contains(attacker.getUniqueId())) {
            e.setCancelled(true);
        } else {
            sweepPrimedAttackers.add(attacker.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getInstance(), sweepPrimedAttackers::clear, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent e) {
        if (swordBlocking == null) return;
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isSword(item.getType())) return;

        swordBlocking.applyComponents(item);
        player.getInventory().setItemInMainHand(item);
        startUsingItem(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (swordBlocking == null) return;
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!swordBlocking.isBlockingSword(victim)) return;

        double dmg = e.getDamage();
        e.setDamage(Math.max(0, dmg - (dmg * 0.5)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (swordBlocking == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack current = e.getCurrentItem();
        if (current != null && isSword(current.getType())) swordBlocking.clearComponents(current);

        ItemStack cursor = e.getCursor();
        if (isSword(cursor.getType())) swordBlocking.clearComponents(cursor);

        Bukkit.getScheduler().runTask(getInstance(), () -> {
            ItemStack main = player.getInventory().getItemInMainHand();
            if (isSword(main.getType())) {
                swordBlocking.applyComponents(main);
                player.getInventory().setItemInMainHand(main);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (swordBlocking == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;

        Bukkit.getScheduler().runTask(getInstance(), () -> {
            ItemStack main = player.getInventory().getItemInMainHand();
            if (isSword(main.getType())) {
                swordBlocking.applyComponents(main);
                player.getInventory().setItemInMainHand(main);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHotbar(PlayerItemHeldEvent e) {
        if (swordBlocking == null) return;

        PlayerInventory inv = e.getPlayer().getInventory();
        ItemStack prev = inv.getItem(e.getPreviousSlot());
        if (prev != null && isSword(prev.getType())) swordBlocking.clearComponents(prev);

        ItemStack next = inv.getItem(e.getNewSlot());
        if (next != null && isSword(next.getType())) swordBlocking.applyComponents(next);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (swordBlocking == null) return;
        ItemStack item = e.getItemDrop().getItemStack();
        if (isSword(item.getType())) swordBlocking.clearComponents(item);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        if (swordBlocking == null) return;
        e.getDrops().forEach(i -> {
            if (isSword(i.getType())) swordBlocking.clearComponents(i);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (swordBlocking == null) return;
        ItemStack main = e.getPlayer().getInventory().getItemInMainHand();
        if (isSword(main.getType())) swordBlocking.clearComponents(main);
    }

    // ── Tool damage (reads from active profile) ───────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOldToolDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;

        attacker.resetCooldown();

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        Material type = weapon.getType();

        double baseDamage = getProfileDamage(type);
        if (baseDamage <= 0) return;

        e.setDamage(baseDamage);

        int sharp = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);
        if (sharp > 0) {
            e.setDamage(e.getDamage() + sharp * 1.25);
        }
    }

    /**
     * Resolves damage for a weapon from the active profile.
     * Falls back to 0 (= don't override) if the material isn't a tracked weapon.
     */
    private double getProfileDamage(Material mat) {
        ProfileManager p = getInstance().getProfileManager();
        return switch (mat) {
            case WOODEN_SWORD    -> p.dmgSwordWood;
            case GOLDEN_SWORD    -> p.dmgSwordGold;
            case STONE_SWORD     -> p.dmgSwordStone;
            case IRON_SWORD      -> p.dmgSwordIron;
            case DIAMOND_SWORD   -> p.dmgSwordDiamond;
            case NETHERITE_SWORD -> p.dmgSwordNetherite;
            case WOODEN_AXE      -> p.dmgAxeWood;
            case STONE_AXE       -> p.dmgAxeStone;
            case IRON_AXE        -> p.dmgAxeIron;
            case DIAMOND_AXE     -> p.dmgAxeDiamond;
            case WOODEN_PICKAXE,
                 STONE_PICKAXE,
                 IRON_PICKAXE,
                 DIAMOND_PICKAXE,
                 NETHERITE_PICKAXE -> p.dmgPickaxe;
            case WOODEN_SHOVEL,
                 STONE_SHOVEL,
                 IRON_SHOVEL,
                 DIAMOND_SHOVEL,
                 NETHERITE_SHOVEL  -> p.dmgShovel;
            case WOODEN_HOE,
                 STONE_HOE,
                 IRON_HOE,
                 DIAMOND_HOE,
                 NETHERITE_HOE     -> p.dmgHoe;
            case TRIDENT           -> p.dmgTrident;
            default                -> 0;
        };
    }

    @EventHandler
    public void onPlayerJoinAttackDelay(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(getInstance(), () ->
                e.getPlayer().setMaximumNoDamageTicks(getInstance().getProfileManager().playerNoDamageTicks), 1L);
    }

    @EventHandler
    public void onPlayerRespawnAttackDelay(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(getInstance(), () ->
                e.getPlayer().setMaximumNoDamageTicks(getInstance().getProfileManager().playerNoDamageTicks), 1L);
    }

    @EventHandler
    public void onPlayerWorldChangeAttackDelay(PlayerChangedWorldEvent e) {
        e.getPlayer().setMaximumNoDamageTicks(getInstance().getProfileManager().playerNoDamageTicks);
    }

    @EventHandler
    public void onPlayerQuitAttackDelay(PlayerQuitEvent e) {
        e.getPlayer().setMaximumNoDamageTicks(DEFAULT_NO_DAMAGE_TICKS);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        e.getEntity().setMaximumNoDamageTicks(getInstance().getProfileManager().mobNoDamageTicks);
    }

    @EventHandler
    public void onEntityTeleportAttackDelay(EntityTeleportEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.LivingEntity living)) return;
        if (e.getTo() == null) return;
        if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            living.setMaximumNoDamageTicks(getInstance().getProfileManager().mobNoDamageTicks);
        }
    }

    private boolean isSword(Material mat) {
        return mat != null && mat.name().endsWith("_SWORD");
    }

    private void startUsingItem(Player player) {
        try {
            Class<?> clazz = Class.forName("org.bukkit.entity.LivingEntity");
            clazz.getMethod("startUsingItem", org.bukkit.inventory.EquipmentSlot.class)
                    .invoke(player, org.bukkit.inventory.EquipmentSlot.HAND);
        } catch (Throwable ignored) {}
    }

    private void cancelSweep() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_PARTICLES) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        try {
                            if (event.getPacket().getNewParticles().size() == 0) return;
                            var particle = event.getPacket().getNewParticles().read(0);
                            if (particle.getParticle() == org.bukkit.Particle.SWEEP_ATTACK) {
                                event.setCancelled(true);
                            }
                        } catch (Exception ignored) {}
                    }
                }
        );
    }

    private void cancelAttackSound() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        try {
                            if (event.getPacket().getSoundEffects().size() == 0) return;
                            String soundKey = event.getPacket()
                                    .getSoundEffects()
                                    .read(0)
                                    .getKey()
                                    .getKey();
                            if (blockedSounds.contains(soundKey)) {
                                event.setCancelled(true);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
    }
}