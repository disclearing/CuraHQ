package club.curahq.core.combatlog;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import club.curahq.core.Core;

public class CombatLogListener
  implements Listener
{
  public CombatLogListener(Core plugin)
  {
    this.plugin = plugin;
  }
  
  public static void safelyDisconnect(Player player, String reason)
  {
    if (SAFE_DISCONNECTS.add(player.getUniqueId())) {
      player.kickPlayer(reason);
    }
  }
  
  public static void removeCombatLoggers()
  {
    Iterator<CombatLogEntry> iterator = LOGGERS.values().iterator();
    while (iterator.hasNext())
    {
      CombatLogEntry entry = (CombatLogEntry)iterator.next();
      entry.task.cancel();
      entry.loggerEntity.getBukkitEntity().remove();
      iterator.remove();
    }
    SAFE_DISCONNECTS.clear();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuitSafe(PlayerQuitEvent event)
  {
    SAFE_DISCONNECTS.remove(event.getPlayer().getUniqueId());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onLoggerInteract(EntityInteractEvent event)
  {
    Collection<CombatLogEntry> entries = LOGGERS.values();
    for (CombatLogEntry entry : entries) {
      if (entry.loggerEntity.getBukkitEntity().equals(event.getEntity()))
      {
        event.setCancelled(true);
        break;
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onLoggerDeath(LoggerDeathEvent event)
  {
    CombatLogEntry entry = (CombatLogEntry)LOGGERS.remove(event.getLoggerEntity().getPlayerUUID());
    if (entry != null) {
      entry.task.cancel();
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event)
  {
    CombatLogEntry combatLogEntry = (CombatLogEntry)LOGGERS.remove(event.getPlayer().getUniqueId());
    if (combatLogEntry != null)
    {
      CraftLivingEntity loggerEntity = combatLogEntry.loggerEntity.getBukkitEntity();
      Player player = event.getPlayer();
      event.setSpawnLocation(loggerEntity.getLocation());
      player.setFallDistance(loggerEntity.getFallDistance());
      player.setTicksLived(loggerEntity.getTicksLived());
      player.setRemainingAir(loggerEntity.getRemainingAir());
      loggerEntity.remove();
      combatLogEntry.task.cancel();
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    PlayerInventory inventory = player.getInventory();
    if ((player.getGameMode() != GameMode.CREATIVE) && (!player.isDead()) && (!SAFE_DISCONNECTS.contains(uuid)))
    {
      if ((this.plugin.getTimerManager().invincibilityTimer.getRemaining(uuid) > 0L)) {
        return;
      }
      if (this.plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, 64) <= 0) {
        return;
      }
      Location location = player.getLocation();
      if (this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
        return;
      }
      if (LOGGERS.containsKey(player.getUniqueId())) {
        return;
      }
      World world = location.getWorld();
      LoggerEntity loggerEntity = new LoggerEntity(world, location, player);
      LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
      Bukkit.getPluginManager().callEvent(calledEvent);
      LOGGERS.put(uuid, new CombatLogEntry(loggerEntity, new LoggerRemovable(uuid, loggerEntity).runTaskLater(this.plugin, 600L)));
      CraftEntity craftEntity = loggerEntity.getBukkitEntity();
      if (craftEntity != null)
      {
        CraftLivingEntity craftLivingEntity = (CraftLivingEntity)craftEntity;
        EntityEquipment entityEquipment = craftLivingEntity.getEquipment();
        entityEquipment.setItemInHand(inventory.getItemInHand());
        entityEquipment.setArmorContents(inventory.getArmorContents());
      }
    }
  }
  
  private static final Set<UUID> SAFE_DISCONNECTS = new HashSet<UUID>();
  private static final Map<UUID, CombatLogEntry> LOGGERS = new HashMap<UUID, CombatLogEntry>();
  private final Core plugin;
  
  private static class LoggerRemovable
    extends BukkitRunnable
  {
    private final UUID uuid;
    private final LoggerEntity loggerEntity;
    
    public LoggerRemovable(UUID uuid, LoggerEntity loggerEntity)
    {
      this.uuid = uuid;
      this.loggerEntity = loggerEntity;
    }
    
    public void run()
    {
      if (CombatLogListener.LOGGERS.remove(this.uuid) != null) {
        this.loggerEntity.dead = true;
      }
    }
  }
}
