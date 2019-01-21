package club.curahq.core.commands.staff;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import net.md_5.bungee.api.ChatColor;

public class MiscCommands implements CommandExecutor {
	   public boolean onCommand (final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
	        if (!(sender instanceof Player)) {
	            sender.sendMessage(ChatColor.RED + "You cannot do this in console");
	            return true;
	        }
	        final Player p = (Player)sender;
	        p.getLocation();
	        p.getInventory();
	        String perm = ChatColor.translateAlternateColorCodes('&', "&cNo.");
	        perm = perm.replaceAll("&", "�");
	            if (cmd.getName().equalsIgnoreCase("copyinv")) {
	                if (!p.hasPermission("copyinv.yes")) {
	                    p.sendMessage(perm);
	                    return true;
	                }
	                if (args.length == 0) {
	                    p.sendMessage(ChatColor.GRAY + "/copyinv <Player>");
	                }
	                if (args.length == 1) {
	                    final Player all2 = Bukkit.getPlayer(args[0]);
	                    if (all2 == null) {
	                        p.sendMessage(ChatColor.RED + "That player does not exist!");
	                    }
	                    else {
	                        final ItemStack[] armor = all2.getInventory().getArmorContents();
	                        p.getInventory().clear();
	                        p.getInventory().setArmorContents((ItemStack[])null);
	                        p.getInventory().setArmorContents(armor);
	                        final ItemStack[] inv = all2.getInventory().getContents();
	                        final HashMap<Player, ItemStack[]> itemhash = new HashMap<Player, ItemStack[]>();
	                        itemhash.put(p, inv);
	                        final ItemStack[] items = itemhash.get(p);
	                        p.getInventory().setContents(items);
	                        p.sendMessage(ChatColor.GRAY + "Copying " + ChatColor.RED + args[0] + ChatColor.GRAY + " Inventory");
	                    }
	                }
	            }
	            if (cmd.getName().equalsIgnoreCase("fsay")) {
	                if (!p.hasPermission("fsay.yes")) {
	                    p.sendMessage(perm);
	                    return true;
	                }
	                if (args.length == 0) {
	                    sender.sendMessage(ChatColor.GRAY + "/fsay <player> <message>");
	                    return true;
	                }
	                if (args.length == 1) {
	                    sender.sendMessage(ChatColor.GRAY + "/fsay <player> <message>");
	                }
	                else if (args.length >= 2) {
	                    final Player user = Bukkit.getServer().getPlayer(args[0]);
	                    if (user == null) {
	                        final StringBuilder message = new StringBuilder(args[1]);
	                        for (int arg2 = 2; arg2 < args.length; ++arg2) {
	                            message.append(" ").append(args[arg2]);
	                        }
	                        return true;
	                    }
	                    final StringBuilder message = new StringBuilder(args[1]);
	                    for (int arg2 = 2; arg2 < args.length; ++arg2) {
	                        message.append(" ").append(args[arg2]);
	                    }
	                    user.chat(message.toString());
	                }
	            }
	            
	                    if (!cmd.getName().equalsIgnoreCase("slowstop")) {
	                        return false;
	                    }
	                    if (!p.hasPermission("core.slowstop")) {
	                        p.sendMessage(perm);
	                        return true;
	                    }
	                    new BukkitRunnable() {
		                    int i = 15;

							@Override
							public void run() {
								if(i > 0) {
									Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588" + "  " + ChatColor.RED + "Server Restarting in " + i + " seconds.");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588");
				                    Bukkit.broadcastMessage(ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
								}
			                    
			                    if(i == 0) {
				                    Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "restart");	                        
			                    }
			                    i--;
							}
	                    	
	                    }.runTaskTimer(Core.getPlugin(), 20L, 20L);


	                    return true;
	                }
        }
