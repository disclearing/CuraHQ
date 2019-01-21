package club.curahq.core.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.EnderChest;

import club.curahq.core.Core;

public class WorldListener implements Listener {
    public static final String DEFAULT_WORLD_NAME = "world";
    private Map<Location, BrewingStand> activeStands = new HashMap<Location, BrewingStand>();
    @SuppressWarnings("unused")
	private final Core plugin;

    public WorldListener(final Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.blockList().clear();
        if(event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockChange(final BlockFromToEvent event) {
        if(event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPortalEnter(final EntityPortalEvent event) {
        if(event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onSneak(WeatherChangeEvent event)
    {
    	if (!(event.toWeatherState())) { return;
    	}
    	event.setCancelled(true);
    	event.getWorld().setWeatherDuration(0);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Beds are disabled on this server.");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWitherChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("incomplete-switch")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        switch(event.getBlock().getType()) {
            case SNOW:
            case ICE: {
                event.setCancelled(true);
                break;
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent paramPlayerInteractEvent)
    {
      if ((6 > 1) && (paramPlayerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK))
      {
        BlockState localBlockState = paramPlayerInteractEvent.getClickedBlock().getState();
        if ((localBlockState instanceof BrewingStand)) {
          this.activeStands.put(localBlockState.getLocation(), (BrewingStand)localBlockState);
        }
      }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if(event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if(event.getEntity() instanceof Squid) {
            event.setCancelled(true);
        }
    }
}
