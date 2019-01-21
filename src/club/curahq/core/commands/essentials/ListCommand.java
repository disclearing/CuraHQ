package club.curahq.core.commands.essentials;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.listener.staff.VanishListener;
import club.curahq.core.util.BukkitUtils;


public class ListCommand implements CommandExecutor{
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("list")){
			if (!(sender instanceof Player)) {
				return true;
			}
			ArrayList<String> playernames = new ArrayList<String>();
			for(Player p : Bukkit.getOnlinePlayers()){
				if (p.hasPermission("command.mod") && (!VanishListener.isVanished(p))) {
					playernames.add(p.getName() + ChatColor.GOLD);
				}
			}
			sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			sender.sendMessage(ChatColor.GRAY + "There are currently " + ChatColor.GOLD + Bukkit.getOnlinePlayers().length + ChatColor.GRAY + " players online out of a maximum of " + ChatColor.GOLD + Bukkit.getMaxPlayers());
			sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		}
		return true;
	}


}
