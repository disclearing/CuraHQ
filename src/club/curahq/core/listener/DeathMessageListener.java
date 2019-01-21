package club.curahq.core.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.Core;
import me.apache.commons.lang3.text.WordUtils;

public class DeathMessageListener implements Listener {
    private final Core plugin;

    public DeathMessageListener(final Core plugin) {
        this.plugin = plugin;
    }

    public static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e)
    {
      if (e.getEntity().getKiller() != null)
      {
        String playerName = ChatColor.RED + e.getEntity().getName() + ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + this.plugin.getUserManager().getUser(e.getEntity().getUniqueId()).getKills() + ChatColor.DARK_RED + "]";
        
        String killerName = ChatColor.RED + e.getEntity().getKiller().getName() + ChatColor.DARK_RED + "[" + ChatColor.DARK_RED + this.plugin.getUserManager().getUser(e.getEntity().getKiller().getUniqueId()).getKills() + ChatColor.DARK_RED + "]";
        
        String itemName = "";
        ItemStack itemStack = e.getEntity().getKiller().getItemInHand();
        if (itemStack != null) {
          if (itemStack.getType() == Material.AIR) {
            itemName = "Hand";
          } else if (itemStack.getItemMeta().hasDisplayName()) {
            itemName = itemStack.getItemMeta().getDisplayName();
          } else {
            itemName = WordUtils.capitalizeFully(itemStack.getType().name().replaceAll("_", " "));
          }
        }
        e.setDeathMessage(playerName + ChatColor.YELLOW + " was killed by " + killerName + ChatColor.YELLOW + " using " + ChatColor.RED + itemName + ChatColor.YELLOW + ".");
      }
      else
      {
        String rawMessage = e.getDeathMessage().replaceAll(e.getEntity().getName(), "");
        e.setDeathMessage(ChatColor.RED + e.getEntity().getName() + ChatColor.DARK_RED + "[" + this.plugin.getUserManager().getUser(e.getEntity().getUniqueId()).getKills() + ChatColor.DARK_RED + "]" + ChatColor.YELLOW + rawMessage + ".");
      }
      e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
      e.setKeepLevel(false);
    }
    
}
