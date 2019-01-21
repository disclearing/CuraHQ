package club.curahq.core.deathban.lives;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LivesListener implements Listener{
	
	  @EventHandler
	  public void onVanish(PlayerInteractEvent e)
	  {
		Player p = e.getPlayer();
	    if (((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (e.getAction().equals(Action.RIGHT_CLICK_AIR))) && 
	    	      (e.getPlayer().getItemInHand() != null) && 
	    	      (e.getPlayer().getItemInHand().hasItemMeta()) && 
	    	      (e.getPlayer().getItemInHand().getItemMeta().hasDisplayName())) {
	    if ((p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "1 Life " + ChatColor.GRAY + "(Right Click)")  && (p.getItemInHand().getType() == Material.PAPER)))
	    {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 1");
			p.getItemInHand().setType(Material.AIR);
		      if (e.getItem().getAmount() <= 1) {
		      e.getPlayer().getInventory().remove(e.getItem());
		      }
		      else
		      {
		          e.getPlayer().getInventory().getItemInHand().setAmount(e.getItem().getAmount() - 1);
		      }
	    	
	    }
	    else if ((p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "3 Lives " + ChatColor.GRAY + "(Right Click)")  && (p.getItemInHand().getType() == Material.PAPER)))
	    {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 3");
			p.getItemInHand().setType(Material.AIR);
		      if (e.getItem().getAmount() <= 1) {
		      e.getPlayer().getInventory().remove(e.getItem());
		      }
		      else
		      {
		          e.getPlayer().getInventory().getItemInHand().setAmount(e.getItem().getAmount() - 1);
		      }
	    }
	    else if ((p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "5 Lives " + ChatColor.GRAY + "(Right Click)")  && (p.getItemInHand().getType() == Material.PAPER)))
	    {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 5");
			p.getItemInHand().setType(Material.AIR);
		      if (e.getItem().getAmount() <= 1) {
		      e.getPlayer().getInventory().remove(e.getItem());
		      }
		      else
		      {
		          e.getPlayer().getInventory().getItemInHand().setAmount(e.getItem().getAmount() - 1);
		      }
	    }
	    else if ((p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "8 Lives " + ChatColor.GRAY + "(Right Click)")  && (p.getItemInHand().getType() == Material.PAPER)))
	    {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lives give " + p.getName() + " 8");
			p.getItemInHand().setType(Material.AIR);
		      if (e.getItem().getAmount() <= 1) {
		      e.getPlayer().getInventory().remove(e.getItem());
		      }
		      else
		      {
		          e.getPlayer().getInventory().getItemInHand().setAmount(e.getItem().getAmount() - 1);
		      }
	    	}
	    }
	  }

}
