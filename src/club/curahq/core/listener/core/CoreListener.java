 package club.curahq.core.listener.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.Core;
import club.curahq.core.util.config.WorldData;

public class CoreListener
  implements Listener
{
  private final Core plugin;
  
  public CoreListener(Core plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
	  e.setCancelled(true);
  }
  
  
  
  
@EventHandler(priority=EventPriority.MONITOR)
  public void onLeave(PlayerQuitEvent event)
  {
	
	for (Player on : Bukkit.getOnlinePlayers())
	if ((event.getPlayer().hasPermission("core.mod") && (on.hasPermission("core.mod"))));
  }
  
  private WorldData bz = WorldData.getInstance();
  
  @EventHandler
  public void onExplode(EntityTargetEvent e) { 
   if(e.getEntity() instanceof Creeper) {
     e.setCancelled(true);
   }
  }


  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onReSpawn(PlayerRespawnEvent event)
  {
  	  double x = bz.getConfig().getDouble("world-spawn.x");
  	  double y = bz.getConfig().getDouble("world-spawn.y");
  	  double z = bz.getConfig().getDouble("world-spawn.z");
      Location destination = new Location(event.getPlayer().getWorld(), Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
      event.getPlayer().teleport(destination);
      // Add money
  }
  
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    event.setJoinMessage(null);
    if (!(event.getPlayer().hasPlayedBefore())) {
    	double x = bz.getConfig().getDouble("world-spawn.x");
      	double y = bz.getConfig().getDouble("world-spawn.y");
      	double z = bz.getConfig().getDouble("world-spawn.z");
        Location destination = new Location(event.getPlayer().getWorld(), Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
        event.getPlayer().teleport(destination);
        ItemStack fish = new ItemStack(Material.FISHING_ROD, 1);
        event.getPlayer().getInventory().addItem(fish);
        
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);
        event.getPlayer().getInventory().addItem(food);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + event.getPlayer().getName() + " Starter 2");
        
        this.plugin.getEconomyManager().setBalance(event.getPlayer().getUniqueId(), 250);
        
        if (this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).getKills() > 1) {
        	this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).setKills(0);
        }
    }

  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerQuit(PlayerKickEvent event)
  {
    event.setLeaveMessage(null);
  }
  
@EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    event.setQuitMessage(null);
    Player player = event.getPlayer();
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
  }
  
@EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
  public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
  {
    Player player = event.getPlayer();
    this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
    this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
  }
}
