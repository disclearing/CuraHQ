package club.curahq.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;



//Is buggy??

public class ElevatorListener
  implements Listener
{
	  @EventHandler
	  public void onInteract(PlayerInteractEvent e) {
	    Player p = e.getPlayer();
	    if ((e.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) && (
	      (e.getClickedBlock().getType() == Material.SIGN_POST) || (e.getClickedBlock().getType() == Material.WALL_SIGN))) {
	      org.bukkit.block.BlockState state = e.getClickedBlock().getState();
	      if ((state instanceof Sign)) {
	        Sign s = (Sign)state;
	        if ((s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&9[Elevator]"))) && (s.getLine(1).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&0Up")))) {
	          Location startLoc = s.getLocation();
	          double y = startLoc.getY() + 1.0D;
	          while (Bukkit.getWorld(s.getWorld().getName()).getBlockAt(new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), y, startLoc.getZ())).getType() == Material.AIR) {
	            y += 1.0D;
	            if (y > 255.0D) {
	              p.sendMessage(ChatColor.RED + "No valid location found.");
	              break;
	            }
	          }
	          if (y > 255.0D) {
	            return;
	          }
	          Location finalLoc = new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), y, startLoc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
	          if (Bukkit.getWorld(s.getWorld().getName()).getBlockAt(new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), finalLoc.getY() + 1.0D, startLoc.getZ())).getType() != Material.AIR) {
	            p.sendMessage(ChatColor.RED + "No valid location found.");
	            return;
	          }
	          p.teleport(finalLoc.add(0.5D, 1.0D, 0.5D));
	          p.sendMessage(ChatColor.GREEN + "You have been teleported!");
	        }
	        else if ((s.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&9[Elevator]"))) && (s.getLine(1).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&0Down")))) {
	          Location startLoc = s.getLocation();
	          double y = startLoc.getY() - 1.0D;
	          while (Bukkit.getWorld(s.getWorld().getName()).getBlockAt(new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), y, startLoc.getZ())).getType() != Material.AIR) {
	            y -= 1.0D;
	            if (y < 1.0D) {
	              p.sendMessage(ChatColor.RED + "No valid location found.");
	              break;
	            }
	          }
	          if (y < 1.0D) {
	            return;
	          }
	          Location finalLoc = new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), y, startLoc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
	          if (Bukkit.getWorld(s.getWorld().getName()).getBlockAt(new Location(Bukkit.getWorld(s.getWorld().getName()), startLoc.getX(), finalLoc.getY() - 1.0D, startLoc.getZ())).getType() != Material.AIR) {
	            p.sendMessage(ChatColor.RED + "No valid location found.");
	            return;
	          }
	          p.teleport(finalLoc.add(0.5D, -1.0D, 0.5D));
	          p.sendMessage(ChatColor.GREEN + "You have been teleported!");
	        }
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onSign(SignChangeEvent e)
	  {
	    if (e.getLine(0).equalsIgnoreCase("[Elevator]")) {
	      if (e.getLine(1).equalsIgnoreCase("Up")) {
	        e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&9[Elevator]"));
	        e.setLine(1, ChatColor.translateAlternateColorCodes('&', "&0Up"));
	        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have created an elevator!");
	        return;
	      }
	      if (e.getLine(1).equalsIgnoreCase("Down")) {
	        e.setLine(0, ChatColor.translateAlternateColorCodes('&', "&9[Elevator]"));
	        e.setLine(1, ChatColor.translateAlternateColorCodes('&', "&0Down"));
	        e.getPlayer().sendMessage(ChatColor.YELLOW + "You have created an elevator!");
	      }
	    }
	  }
	}