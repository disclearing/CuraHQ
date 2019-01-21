package club.curahq.core.listener;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import club.curahq.core.Core;

public class FoundDiamondsListener
  implements Listener
{
  public static final Material SEARCH_TYPE = Material.DIAMOND_ORE;
  public final Set<String> foundLocations;
  private final Core plugin;
  
  public FoundDiamondsListener(Core plugin)
  {
    this.foundLocations = new HashSet<String>();
    this.plugin = plugin;
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPistonExtend(BlockPistonExtendEvent event)
  {
    for (Block block : event.getBlocks()) {
      if (block.getType() == SEARCH_TYPE) {
        this.foundLocations.add(block.getLocation().toString());
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onBlockPlace(BlockPlaceEvent event)
  {
    Block block = event.getBlock();
    if (block.getType() == SEARCH_TYPE) {
      this.foundLocations.add(block.getLocation().toString());
    }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onBlockBreak(BlockBreakEvent event)
  {
    Player player = event.getPlayer();
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
      return;
    }
    Block block = event.getBlock();
    Location blockLocation = block.getLocation();
    if ((block.getType() == SEARCH_TYPE) && (this.foundLocations.add(blockLocation.toString())))
    {
      int count = 1;
      int z;
      for (int x = -5; x < 5; x++) {
        for (int y = -5; y < 5; y++) {
          for (z = -5; z < 5; z++)
          {
            Block otherBlock = blockLocation.clone().add(x, y, z).getBlock();
            if ((!otherBlock.equals(block)) && (otherBlock.getType() == SEARCH_TYPE) && (this.foundLocations.add(otherBlock.getLocation().toString()))) {
              count++;
            }
          }
        }
      }
      for (Player on : Bukkit.getOnlinePlayers()) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) != null)
        {
          String message = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getRelation(on).toChatColour() + player.getName() + ChatColor.GRAY + " has found " + ChatColor.AQUA + count + ChatColor.GRAY + " diamond(s).";
          on.sendMessage(message);
        }
        else
        {
          String message = ChatColor.RED + player.getName() + ChatColor.GRAY + " has found " + ChatColor.AQUA + count + ChatColor.GRAY + " diamond(s).";
          on.sendMessage(message);
        }
      }
    }
  }
}
