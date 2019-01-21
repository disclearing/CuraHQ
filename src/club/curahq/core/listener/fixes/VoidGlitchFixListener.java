package club.curahq.core.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class VoidGlitchFixListener
  implements Listener
{
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPlayerDamage(EntityDamageEvent event)
  {
    if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
    {
      Entity entity = event.getEntity();
      if ((entity instanceof Player))
      {
        if (entity.getWorld().getEnvironment() == World.Environment.THE_END) {
          return;
        }
        Location destination = Bukkit.getWorld("world").getSpawnLocation();
        if (destination == null) {
          return;
        }
        if (entity.teleport(destination, TeleportCause.PLUGIN))
        {
          event.setCancelled(true);
          ((Player)entity).sendMessage(ChatColor.YELLOW + "You were saved from the void.");
        }
      }
    }
  }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event)
    {
    Player p = event.getPlayer();
    Location loc = p.getLocation();
    if (p.getWorld().getEnvironment() == Environment.NETHER) {
    	return;
    }
    if (p.getWorld().getEnvironment() == Environment.THE_END) {
    	return;
    }
    Location destination = Bukkit.getWorld("world").getSpawnLocation();
    if ((p.getWorld().getEnvironment() == Environment.NORMAL) && (loc.getBlockY() <= -20)) {
        event.setCancelled(true);
        p.teleport(destination);
        p.sendMessage(ChatColor.YELLOW + "You were saved from the void.");
      }
   }
}
