package club.curahq.core.listener;

import net.minecraft.util.com.google.common.collect.ImmutableSet; 
import net.minecraft.util.com.google.common.collect.Sets;
import org.bukkit.event.block.*;
import org.bukkit.inventory.*;

import club.curahq.core.Core;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;

import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

public class PearlGlitchListener implements Listener
{
	private final ImmutableSet<Material> blockedPearlTypes;
    private final Core plugin;

	public PearlGlitchListener(final Core plugin) {
        this.blockedPearlTypes = (ImmutableSet<Material>)Sets.immutableEnumSet((Enum)Material.THIN_GLASS, (Enum[])new Material[] { Material.STEP, Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS });
        this.plugin = plugin;
    }
    Location previous;
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.ENDER_PEARL) {
            final Block block = event.getClickedBlock();
            if (block.getType().isSolid() && !(block.getState() instanceof InventoryHolder)) {
                final Faction factionAt = Core.getPlugin().getFactionManager().getFactionAt(block.getLocation());
                if (!(factionAt instanceof ClaimableFaction)) {
                    return;
                }
                event.setCancelled(true);
                final Player player = event.getPlayer();
                player.setItemInHand(event.getItem());
                
            }
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e)
    {
      if (((e.getMessage().startsWith("/minecraft:")) || (e.getMessage().startsWith("/bukkit:"))) && (!e.getPlayer().isOp()))
      {
        e.setCancelled(true);
      }
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void disableCommand23(PlayerCommandPreprocessEvent event)
    {
     	event.getPlayer();
      String message = event.getMessage().toLowerCase();
      String c = "/" + "me";
      if ((message.equals(c)) || (message.startsWith(c + " ")))
      {
       event.setCancelled(true);
      }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e)
    {
      Player p = e.getPlayer();
      {
        if ((e.isCancelled()) || (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
          return;
        }
        e.getTo();
        if ((e.getTo().getBlock().getType() == Material.FENCE_GATE))
        {
          p.sendMessage(ChatColor.RED + "Invalid Pearl!");
          this.plugin.getTimerManager().enderPearlTimer.refund(p);
          e.setCancelled(true);
        }
        Location target = e.getTo();
        target.setX(target.getBlockX() + 0.5D);
        target.setZ(target.getBlockZ() + 0.5D);
        e.setTo(target);
      }
    }
    
    
    
    @EventHandler
    public void onMove(PlayerInteractEvent e){
        if(e.getPlayer().getLocation().getBlock() != null){
            if(e.getPlayer().getLocation().getBlock().getType() == Material.TRAP_DOOR){
                    if(!Core.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).equals(Core.getPlugin().getFactionManager().getPlayerFaction(e.getPlayer().getUniqueId()))) {
                        e.getPlayer().teleport(e.getPlayer().getLocation().add(0, 1, 0));
                    }
        }
        }
    }

}