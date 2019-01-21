package club.curahq.core.listener.fixes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

import club.curahq.core.Core;
import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.util.config.WorldData;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

public class PortalListener
  implements Listener
{
  private final TObjectLongMap<UUID> messageDelays = new TObjectLongHashMap<UUID>();
  private final Core plugin;
  WorldData data = WorldData.getInstance();
  
  public PortalListener(Core plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onEntityPortal(EntityPortalEvent event)
  {
    if ((event.getEntity() instanceof EnderDragon)) {
      event.setCancelled(true);
    }
  }
  
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onWorldChanged(PlayerChangedWorldEvent event)
  {
    Player player = event.getPlayer();
    World from = event.getFrom();
    World to = player.getWorld();
    if ((from.getEnvironment() != World.Environment.THE_END) && (to.getEnvironment() == World.Environment.THE_END) && (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))) {
      player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPortalEnter(PlayerPortalEvent event)
  {
    if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      return;
    }
    Location to = event.getTo();
    World toWorld = to.getWorld();
    if (toWorld == null) {
      return;
    }
    if (toWorld.getEnvironment() == World.Environment.THE_END)
    {
      Player player = event.getPlayer();
      PlayerTimer timer = this.plugin.getTimerManager().combatTimer;
      long remaining;
      if ((remaining = timer.getRemaining(player)) > 0L)
      {
        message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + 
          Core.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        
        event.setCancelled(true);
        return;
      }
      timer = this.plugin.getTimerManager().invincibilityTimer;
      if ((remaining = timer.getRemaining(player)) > 0L)
      {
        message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + 
          Core.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        
        event.setCancelled(true);
        return;
      }
      event.useTravelAgent(false);
      double x = data.getConfig().getDouble("world.end.entrace.x");
      double y = data.getConfig().getDouble("world.end.entrace.y");
      double z = data.getConfig().getDouble("world.end.entrace.z");
      Location des = new Location(Bukkit.getWorld("world_the_end"), Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
      event.setTo(des);
    }
  }
  
  @EventHandler
  public void onLeave(PlayerMoveEvent e)
  {
    Player p = e.getPlayer();
    Block b = p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
    if ((b.getType() == Material.STATIONARY_WATER) && (p.getWorld().getEnvironment() == World.Environment.THE_END)) {
        double x = data.getConfig().getDouble("world.exit.x");
        double y = data.getConfig().getDouble("world.exit.y");
        double z = data.getConfig().getDouble("world.exit.z");
        Location des = new Location(Bukkit.getWorld("world"), Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
        p.teleport(des);
    }
  }
  
  
  
  private void message(Player player, String message)
  {
    long last = this.messageDelays.get(player.getUniqueId());
    long millis = System.currentTimeMillis();
    if ((last != this.messageDelays.getNoEntryValue()) && (last + 2500L - millis > 0L)) {
      return;
    }
    this.messageDelays.put(player.getUniqueId(), millis);
    player.sendMessage(message);
  }
}
