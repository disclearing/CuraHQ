package club.curahq.core.listener.fixes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import club.curahq.core.util.config.PotionLimiterData;
import net.md_5.bungee.api.ChatColor;

public class PotionLimitData
  implements Listener
{
  private static List<Short> disabledPotions;
  public boolean isPotionDisabled(ItemStack item)
  {
    return item.getType() == Material.POTION ? PotionLimitData.disabledPotions.contains(Short.valueOf(item.getDurability())) : false;
  }
  
  public static void init() {
	  reload();
  }
  
  public static void reload()
  {
    disabledPotions = PotionLimiterData.getInstance().getConfig().getShortList("disable-potions");
  }
  
  @EventHandler
  public void onSplash(PotionSplashEvent event)
  {
    if (isPotionDisabled(event.getPotion().getItem()))
    {
      event.setCancelled(true);
      @SuppressWarnings("deprecation")
	ProjectileSource shooter = event.getEntity().getShooter();
      if ((shooter instanceof Player)) {
    	((Player) shooter).sendMessage(ChatColor.RED + "You cannot use this potions. ");
      }
    }
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent event)
  {
    if (isPotionDisabled(event.getItem()))
    {
      event.setCancelled(true);
  	  event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this potions. ");
    }
  }
  
  @EventHandler
  public void onBrew(BrewEvent e) {
       BrewerInventory inventory = e.getContents();
       BrewingStand stand = inventory.getHolder(); 
       stand.setBrewingTime(200);
	   if (isPotionDisabled(e.getContents().getItem(0))) {
	    e.setCancelled(true);
	   }

	  
  }
}