package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor{
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
	  Player player = (Player)sender;
	  if ((command.getName().equalsIgnoreCase("reset")) && ((sender instanceof Player)) && 
	    (args.length > 1))
	  {
	    sender.sendMessage("�cUsage: /Reset <player>");
	    return true;
	  }
	  if (args.length == 0)
	  {
	    sender.sendMessage("�cUsage: /Reset <player>");
	    return true;
	  }
	  Player target = Bukkit.getServer().getPlayer(args[0]);
	  if ((args.length == 1) && 
	    (target == null))
	  {
	    player.sendMessage("�cPlayer not found");
	    return true;
	  }
	  target.setStatistic(Statistic.DEATHS, 0);
	  target.setStatistic(Statistic.PLAYER_KILLS, 0);
	  
	  sender.sendMessage(ChatColor.RED + "You have reset " + target.getPlayer().getName() + " 's stats!");
	  for (Player s : Bukkit.getOnlinePlayers()) {
			if (s.hasPermission("command.reset")) {
				s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + sender.getName() + " &fhas reset the &eStatistics &fof &e" + target.getName()));
				return false;
	}

       }
	return false;
   }
}
