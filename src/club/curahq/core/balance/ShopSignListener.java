package club.curahq.core.balance;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.Core;
import club.curahq.core.crowbar.Crowbar;
import club.curahq.core.util.InventoryUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.core.BasePlugins;

public class ShopSignListener
  implements Listener
{
  public ShopSignListener(Core plugin)
  {
    this.plugin = plugin;
  }
  
  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=false, priority=EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
    {
      Block block = event.getClickedBlock();
      BlockState state = block.getState();
      if ((state instanceof Sign))
      {
        Sign sign = (Sign)state;
        String[] lines = sign.getLines();
        Integer quantity = JavaUtils.tryParseInt(lines[2]);
        if (quantity == null) {
          return;
        }
        Integer price = JavaUtils.tryParseInt(ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
        if (price == null) {
          return;
        }
        ItemStack stack;
        if (lines[1].equalsIgnoreCase("Crowbar")) {
          stack = new Crowbar().getItemIfPresent();
        } else if ((stack = BasePlugins.getPlugin().getItemDb().getItem(ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), (int) quantity)) == null) {
          return;
        }
        Player player = event.getPlayer();
        String[] fakeLines = (String[])Arrays.copyOf(sign.getLines(), 4);
        if (((lines[0].contains("Sell")) && (lines[0].contains(ChatColor.RED.toString()))) || (lines[0].contains(ChatColor.AQUA.toString())))
        {
          int sellQuantity = Math.min(quantity.intValue(), InventoryUtils.countAmount(player.getInventory(), stack.getType(), stack.getDurability()));
          if (sellQuantity <= 0)
          {
            fakeLines[0] = (ChatColor.RED + "Not carrying any");
            fakeLines[2] = (ChatColor.RED + "on you.");
            fakeLines[3] = "";
          }
          else
          {
            int newPrice = price.intValue() / quantity.intValue() * sellQuantity;
            fakeLines[0] = (ChatColor.GREEN + "Sold " + sellQuantity);
            fakeLines[3] = (ChatColor.GREEN + "for " + '$' + newPrice);
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
            InventoryUtils.removeItem(player.getInventory(), stack.getType(), (short)stack.getData().getData(), sellQuantity);
            player.updateInventory();
          }
        }
        else
        {
          if ((!lines[0].contains("Buy")) || (!lines[0].contains(ChatColor.GREEN.toString())) || (lines[0].contains(ChatColor.AQUA.toString()))) {
            return;
          }
          if (price.intValue() > this.plugin.getEconomyManager().getBalance(player.getUniqueId()))
          {
            fakeLines[0] = (ChatColor.RED + "Cannot afford");
          }
          else
          {
            fakeLines[0] = (ChatColor.GREEN + "Item bought");
            fakeLines[3] = (ChatColor.GREEN + "for " + '$' + price);
            this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price.intValue());
            World world = player.getWorld();
            Location location = player.getLocation();
            Map<Integer, ItemStack> excess = player.getInventory().addItem(new ItemStack[] { stack });
            for (Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
              world.dropItemNaturally(location, (ItemStack)excessItemStack.getValue());
            }
            player.setItemInHand(player.getItemInHand());
            player.updateInventory();
          }
        }
        event.setCancelled(true);
        BasePlugins.getPlugin().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
      }
    }
  }
  
  private static final Pattern ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
  private final Core plugin;
}
