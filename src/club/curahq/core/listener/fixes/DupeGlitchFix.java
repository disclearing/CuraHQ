package club.curahq.core.listener.fixes;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class DupeGlitchFix
  implements Listener
{
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onBlockPlace(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    if ((p.getItemInHand() != null) && (p.getItemInHand().hasItemMeta()) && (p.getItemInHand().getType() != Material.SKULL)) {
      return;
    }
    ItemMeta meta = p.getItemInHand().getItemMeta();
    if ((meta != null) && (meta.hasDisplayName()) && (meta.getDisplayName().contains(" Key Reward")))
    {
      p.sendMessage(ChatColor.RED + "[Warning] " + ChatColor.GRAY + "This duping glitch has been patched.");
      e.setCancelled(true);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent e)
  {
    Player p = e.getPlayer();
    if ((!p.getItemInHand().hasItemMeta()) && (p.getItemInHand().getType() != Material.SKULL)) {
      return;
    }
    ItemMeta meta = p.getItemInHand().getItemMeta();
    if ((meta.hasDisplayName()) && (meta.getDisplayName().contains(" Key Reward")))
    {
      p.sendMessage(ChatColor.RED + "[Warning] " + ChatColor.GRAY + "This duping glitch has been patched.");
      e.setBuild(false);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onCraft(CraftItemEvent e)
  {
    ArrayList<Material> items = new ArrayList<Material>();
    ItemStack[] arrayOfItemStack;
    int j = (arrayOfItemStack = e.getInventory().getContents()).length;
    for (int i = 0; i < j; i++)
    {
      ItemStack item = arrayOfItemStack[i];
      if ((item.getType() == Material.MINECART) || (item.getType() == Material.CHEST) || (item.getType() == Material.TRAPPED_CHEST) || (item.getType() == Material.HOPPER)) {
        items.add(item.getType());
      }
    }
    if (((items.contains(Material.MINECART)) && (items.contains(Material.CHEST))) || ((items.contains(Material.MINECART)) && (items.contains(Material.TRAPPED_CHEST))) || ((items.contains(Material.MINECART)) && (items.contains(Material.HOPPER))))
    {
      ((Player)e.getWhoClicked()).sendMessage(ChatColor.RED + "[Warning] " + ChatColor.GRAY + "This duping glitch has been patched.");
      e.setCancelled(true);
    }
  }
}
