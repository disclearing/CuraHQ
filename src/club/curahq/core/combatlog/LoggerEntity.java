package club.curahq.core.combatlog;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.base.Function;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityAgeable;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;


public class LoggerEntity extends EntityVillager {
    private static final Function<Double, Double> DAMAGE_FUNCTION;

    static {
        DAMAGE_FUNCTION = (f1 -> 0.0);
    }

    private final UUID playerUUID;

    public LoggerEntity(final World world, final Location location, final Player player) {
        super(((CraftWorld) world).getHandle());
        this.lastDamager = ((CraftPlayer) player).getHandle().lastDamager;
        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();
        final String playerName = player.getName();
        final boolean hasSpawned = ((CraftWorld) world).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Combat Logger for [" + playerName + "] " + (hasSpawned ? (ChatColor.GREEN + "successfully spawned") : (ChatColor.RED + "failed to spawn")) + ChatColor.GOLD + " at (" + String.format("%.1f", x) + ", " + String.format("%.1f", y) + ", " + String.format("%.1f", z) + ')');
        this.playerUUID = player.getUniqueId();
        if(hasSpawned) {
            this.setCustomName(playerName);
            this.setCustomNameVisible(true);
            this.setPositionRotation(x, y, z, location.getYaw(), location.getPitch());
            this.setCustomNameVisible(true);
        }
    }

    private static PlayerNmsResult getResult(final World world, final UUID playerUUID) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        if(offlinePlayer.hasPlayedBefore()) {
            final WorldServer worldServer = ((CraftWorld) world).getHandle();
            final EntityPlayer entityPlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), worldServer, new GameProfile(playerUUID, offlinePlayer.getName()), new PlayerInteractManager((net.minecraft.server.v1_7_R4.World) worldServer));
            final Player player = (Player) entityPlayer.getBukkitEntity();
            if(player != null) {
                player.loadData();
                return new PlayerNmsResult(player, entityPlayer);
            }
        }
        return null;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void move(final double d0, final double d1, final double d2) {
    }

    public void b(final int i) {
    }

    public void dropDeathLoot(final boolean flag, final int i) {
    }

    public Entity findTarget() {
        return null;
    }

    public boolean damageEntity(final DamageSource damageSource, final float amount) {
        final PlayerNmsResult nmsResult = getResult((World) this.world.getWorld(), this.playerUUID);
        if(nmsResult == null) {
            return true;
        }
        final EntityPlayer entityPlayer = nmsResult.entityPlayer;
        if(entityPlayer != null) {
            entityPlayer.setPosition(this.locX, this.locY, this.locZ);
            final EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent((Entity) entityPlayer, damageSource, (double) amount, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION, (Function<Double, Double>) LoggerEntity.DAMAGE_FUNCTION);
            if(event.isCancelled()) {
                return false;
            }
        }
        return super.damageEntity(damageSource, amount);
    }

    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return null;
    }

    public boolean a(final EntityHuman entityHuman) {
        return false;
    }

    public void h() {
        super.h();
    }

    public void collide(final Entity entity) {
    }

    public void die(final DamageSource damageSource) {
        final PlayerNmsResult playerNmsResult = getResult((World) this.world.getWorld(), this.playerUUID);
        if(playerNmsResult == null) {
            return;
        }
        final Player player = playerNmsResult.player;
        final PlayerInventory inventory =  player.getInventory();
        final boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
        final List<ItemStack> drops = new ArrayList<>();
        if(!keepInventory) {
            ItemStack[] deathMessage = inventory.getContents();
            int entityPlayer = deathMessage.length;

            int event;
            ItemStack loggerDeathEvent;
            for(event = 0; event < entityPlayer; ++event) {
                loggerDeathEvent = deathMessage[event];
                if(loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR) {
                    drops.add(loggerDeathEvent);
                }
            }

            deathMessage = inventory.getArmorContents();
            entityPlayer = deathMessage.length;

            for(event = 0; event < entityPlayer; ++event) {
                loggerDeathEvent = deathMessage[event];
                if(loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR) {
                    drops.add(loggerDeathEvent);
                }
            }
        }
        String deathMessage =  ChatColor.GRAY + "(Combat-Logger)" + ChatColor.YELLOW + this.combatTracker.b().c();
        final EntityPlayer entityPlayer = playerNmsResult.entityPlayer;
        entityPlayer.combatTracker = this.combatTracker;
        if(Bukkit.getPlayer(entityPlayer.getName()) != null){
            Bukkit.getPlayer(entityPlayer.getUniqueID()).getInventory().clear();
            Bukkit.getPlayer(entityPlayer.getUniqueID()).kickPlayer("error");
        }
        final PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(entityPlayer, drops, deathMessage, keepInventory);
        deathMessage = event.getDeathMessage();
        if(deathMessage != null && !deathMessage.isEmpty()) {
            Bukkit.broadcastMessage(deathMessage);
        }
        super.die(damageSource);
        final LoggerDeathEvent loggerDeathEvent = new LoggerDeathEvent(this);
        Bukkit.getPluginManager().callEvent(loggerDeathEvent);
        if(!event.getKeepInventory()) {
            inventory.clear();
            inventory.setArmorContents(new ItemStack[inventory.getArmorContents().length]);
        }
        entityPlayer.setLocation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        entityPlayer.setHealth(0.0f);
        player.saveData();
    }



    public CraftLivingEntity getBukkitEntity() {
        return (CraftLivingEntity) super.getBukkitEntity();
    }

    public static final class PlayerNmsResult {
        public final Player player;
        public final EntityPlayer entityPlayer;

        public PlayerNmsResult(final Player player, final EntityPlayer entityPlayer) {
            this.player = player;
            this.entityPlayer = entityPlayer;
        }
    }
}
