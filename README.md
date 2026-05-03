## 🛹 LegacyCombat

> [!NOTE]
> Legacy its a Paper plugin designed to bring back the old combat system (1.7-1.8) to newer minecraft versions.

## ⚠️ Caution
> [!WARNING]
> Thanks to minecraft adding a new way to use items, blockhit animations will only be seen if the player client's version is 1.21.5 - 1.21.*    
> All other versions under the 1.21.5 will not show the blockhit animation, it will just delay you as if you were doing it.

## ✨ **Why would you need this plugin**
- 🧩 **Customizable:** change only what you need to change: cooldown, tool damage, knockback, potions, projectile physics, sounds, & more.
- 🚀 **Performant:** lean listeners only enabled as needed; reflection lookups are cached and recurring tasks are minimised (shared where possible) to keep tick time low on busy PvP servers.
- 🗺️ **Modesets:** ship different rules for different worlds. Perfect for mixed PvP/PvE, minigames, or duels.
- ✅ **Tested for you:** live integration tests run real Paper servers across multiple versions every build.
- 💸 **Semi Free:** Freemium source, if you need support about any issue, you first need to support us with a donation.

## ⚡ **Quick start**
1. Drop the jar into `plugins/`.
2. Restart and edit `profiles.yml` to start tuning the values to your favourite choice.
3. Or just do `/combat menu` and start managing between all the values that you can change.
4. Use `/legacy reload` to apply changes instantly (if changes were made directly on .yml, if not, it's not necessary).
5. Manage Knockback with `/kb` to manage the values if you don't want to open the editor.

# 🪐 Gameplay

#### 🔥 Tools & Swords
*Control block and sweep behaviour.*
- **Sword blocking:** Restore Old BlockHit (Right-Clicking a sword) into Paper 1.21.5+, we also add the native sword blocking animation via the consumable component
- **Sword sweep:** enable or disable sweeps
- **Sword sweep particles:** hide or show sweep visuals
- **Tools Damage:** Adjust to any damage that you want.

> [!WARNING]
> As I'm writing this, disabling sword sweep and its particles it's not a thing but will be added soon.

### ⚔️ Knockback
- **Player Knockback:** Adjust with a variety of values to tune.
- **Proyectile & Rod Knockback:** Adjust with a variety of values to tune.

### 🛡️ Projectile Physics
- **Projectile Physics:** You can adjust all the physics to each projectile.

> [!IMPORTANT]
> Egg proyectile use the same physics as the Snowball.

## 💫 Customization
- Change anything you want inside the ingame editor.
- Almost anything its customizable ingame.

## 🔌 Compatibility & Testing
- It's only dependency is ProtocolLib, because of the sword sweep particles & hit sounds.
- We target Paper 1.21.5 and runs on Java 21.
- We stick to Spigot/Paper APIs for forward compatibility; NMS/reflection is used only when necessary.

## 🐛 Bug Reports
- If you find any bug or if you just have a suggestion:
- Verify if there is an existing open issue.
- Please give a detailed explanation about your problem with video if you can.
- Be clear about any bug so we can reproduce it and fix it.
---

<a href="https://bstats.org/plugin/bukkit/LegacyCombat">
    <img src="https://bstats.org/signatures/bukkit/LegacyCombat.svg" alt="bStats">
</a>
