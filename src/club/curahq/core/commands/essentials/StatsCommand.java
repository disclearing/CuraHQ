package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;

public class StatsCommand implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
	  Player player = (Player)sender;
	  if ((cmd.getName().equalsIgnoreCase("Stats")) && ((sender instanceof Player)) && 
	    (args.length > 1))
	  {
	    sender.sendMessage("§cUsage: /Stats <player>");
	    return true;
	  }
	  if (args.length == 0)
	  {
	    sender.sendMessage("§cUsage: /Stats <player>");
	    return true;
	  }
	  Player target = Bukkit.getServer().getPlayer(args[0]);
	  if ((args.length == 1) && 
	    (target == null))
	  {
	    player.sendMessage("§cPlayer not found");
	    return true;
	  }
	  int kills = target.getStatistic(Statistic.PLAYER_KILLS);
	  int deaths = target.getStatistic(Statistic.DEATHS);
	  double kdr = Math.abs(kills / deaths);
	  sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	  sender.sendMessage(" " + ChatColor.GOLD + target.getName() + "'s Stats ");
	  sender.sendMessage(" " + ChatColor.YELLOW + "Kills" + ChatColor.GOLD + " § " + ChatColor.GRAY + kills);
	  sender.sendMessage(" " + ChatColor.YELLOW + "Deaths" + ChatColor.GOLD + " § " + ChatColor.GRAY + deaths);
	  sender.sendMessage(" " + ChatColor.YELLOW + "KD" + ChatColor.GOLD + " § " + ChatColor.GRAY + kdr);
	  sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	  return false;
	}

}
