package club.curahq.core.commands.essentials;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;

public class EndPortalCommand
  implements CommandExecutor, Listener
{
  private Core mainPlugin;
  private final String ITEM_DISPLAYNAME = ChatColor.RED.toString() + ChatColor.BOLD + "Endportal Maker";
  private Map<String, LocationPair> playerSelections;
  
  public EndPortalCommand(Core plugin)
  {
    this.mainPlugin = plugin;
    this.playerSelections = new HashMap<String, LocationPair>();
  }
  
  @EventHandler
  public void onInteract(final PlayerInteractEvent e)
  {
    if ((e.hasItem()) && (e.getClickedBlock() != null))
    {
      ItemStack itemStack = e.getItem();
      Block b = e.getClickedBlock();
      if ((itemStack.getItemMeta().hasDisplayName()) && (itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME)))
      {
        LocationPair locationPair = (LocationPair)this.playerSelections.get(e.getPlayer().getName());
        if (locationPair == null)
        {
          locationPair = new LocationPair(null, null);
          this.playerSelections.put(e.getPlayer().getName(), locationPair);
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK)
        {
          if (b.getType() != Material.ENDER_PORTAL_FRAME)
          {
            e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
            return;
          }
          locationPair.setFirstLoc(b.getLocation());
          e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the first location.");
        }
        else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
          if (b.getType() != Material.ENDER_PORTAL_FRAME)
          {
            e.getPlayer().sendMessage(ChatColor.RED + "You must select an end portal frame.");
            return;
          }
          if (locationPair.getFirstLoc() == null)
          {
            e.getPlayer().sendMessage(ChatColor.RED + "Please set the first location (by left clicking the end portal frame).");
            return;
          }
          locationPair.setSecondLoc(b.getLocation());
          e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set the second location.");
          Location firstLoc = locationPair.getFirstLoc();
          Location secondLoc = locationPair.getSecondLoc();
          if (firstLoc.distance(secondLoc) > 6.0D)
          {
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot create an end portal that big.");
            return;
          }
          if (firstLoc.getBlockY() != secondLoc.getBlockY())
          {
            e.getPlayer().sendMessage(ChatColor.RED + "Make sure that the portals have the same elevation.");
            return;
          }
          int minX = Math.min(firstLoc.getBlockX(), secondLoc.getBlockX());
          int minY = Math.min(firstLoc.getBlockY(), secondLoc.getBlockY());
          int minZ = Math.min(firstLoc.getBlockZ(), secondLoc.getBlockZ());
          int maxX = Math.max(firstLoc.getBlockX(), secondLoc.getBlockX());
          int maxY = Math.max(firstLoc.getBlockY(), secondLoc.getBlockY());
          int maxZ = Math.max(firstLoc.getBlockZ(), secondLoc.getBlockZ());
          int x = minX;
          while (x <= maxX)
          {
            int y = minY;
            while (y <= maxY)
            {
              int z = minZ;
              while (z <= maxZ)
              {
                Block block = b.getWorld().getBlockAt(x, y, z);
                if (block.isEmpty()) {
                  block.setType(Material.ENDER_PORTAL);
                }
                z++;
              }
              y++;
            }
            x++;
          }
          e.setCancelled(true);
          new BukkitRunnable()
          {
            public void run()
            {
              e.getPlayer().setItemInHand(null);
              e.getPlayer().updateInventory();
            }
          }.runTask(this.mainPlugin);
          e.getPlayer().sendMessage(ChatColor.GREEN + "You have successfully created an End Portal.");
          this.playerSelections.remove(e.getPlayer().getName());
        }
      }
    }
  }
  
  @EventHandler
  public void onDrop(PlayerDropItemEvent e)
  {
    ItemStack itemStack = e.getItemDrop().getItemStack();
    if ((itemStack.getItemMeta().hasDisplayName()) && (itemStack.getItemMeta().getDisplayName().equals(this.ITEM_DISPLAYNAME))) {
      e.getItemDrop().remove();
    }
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e)
  {
    this.playerSelections.remove(e.getPlayer().getName());
  }
  
  @EventHandler
  public void onKick(PlayerKickEvent e)
  {
    this.playerSelections.remove(e.getPlayer().getName());
  }
  
  public boolean onCommand(CommandSender s, Command c, String alias, String[] args)
  {
    if (!s.hasPermission("command.endportal"))
    {
      s.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
      return true;
    }
    if (!(s instanceof Player))
    {
      s.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
      return true;
    }
    Player p = (Player)s;
    if (p.getInventory().firstEmpty() == -1)
    {
      p.sendMessage(ChatColor.RED + "Please clear up your hotbar, and then perform this command again.");
      return true;
    }
    ItemStack portalMaker = new ItemStack(Material.BLAZE_ROD);
    ItemMeta itemMeta = portalMaker.getItemMeta();
    itemMeta.setDisplayName(this.ITEM_DISPLAYNAME);
    portalMaker.setItemMeta(itemMeta);
    p.getInventory().addItem(new ItemStack[] { portalMaker });
    p.sendMessage(ChatColor.GRAY + "You must select two points.");
    return true;
  }
  
  private class LocationPair
  {
    private Location firstLoc;
    private Location secondLoc;
    
    public LocationPair(Location firstLoc, Location secondLoc)
    {
      this.firstLoc = firstLoc;
      this.secondLoc = secondLoc;
    }
    
    public Location getFirstLoc()
    {
      return this.firstLoc;
    }
    
    public Location getSecondLoc()
    {
      return this.secondLoc;
    }
    
    public void setFirstLoc(Location firstLoc)
    {
      this.firstLoc = firstLoc;
    }
    
    public void setSecondLoc(Location secondLoc)
    {
      this.secondLoc = secondLoc;
    }
  }
}
