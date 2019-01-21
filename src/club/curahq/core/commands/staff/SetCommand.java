package club.curahq.core.commands.staff;

import org.bukkit.ChatColor; 
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.config.WorldData;

public class SetCommand implements CommandExecutor, Listener {
	public SetCommand(Core plugin) {
	}

	public boolean onCommand(CommandSender s, Command c, String alias, String[] args) {
		if (!s.hasPermission("core.admin")) {
			s.sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
			return true;
		}
		Player p = (Player) s;
		if ((args.length != 1)) {
			p.sendMessage(ChatColor.RED + "/set <exit|end>");
			p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			p.sendMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + "Set Location " + ChatColor.GRAY + "(Page 1/1)");
			p.sendMessage(ChatColor.GRAY + " /Set end" + ChatColor.GOLD + " � " + ChatColor.RESET
					+ "Set the location for end-spawn.");
			p.sendMessage(ChatColor.GRAY + " /Set exit" + ChatColor.GOLD + " � " + ChatColor.RESET
					+ "Set the location for end-exit.");
			p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			return true;
		
		} else if (args[0].equalsIgnoreCase("exit")) {
			Location loc = p.getLocation();
			WorldData.getInstance().getConfig().set("world.exit.x", Double.valueOf(loc.getX()));
			WorldData.getInstance().getConfig().set("world.exit.y", Double.valueOf(loc.getY()));
			WorldData.getInstance().getConfig().set("world.exit.z", Double.valueOf(loc.getZ()));
			WorldData.getInstance().saveConfig();
			p.sendMessage(ChatColor.GREEN + "EndExit has been set!.");
		} else if (args[0].equalsIgnoreCase("end")) {
			Location loc = p.getLocation();
			WorldData.getInstance().getConfig().set("world.end.entrace.x", Double.valueOf(loc.getX()));
			WorldData.getInstance().getConfig().set("world.end.entrace.y", Double.valueOf(loc.getY()));
			WorldData.getInstance().getConfig().set("world.end.entrace.z", Double.valueOf(loc.getZ()));
			WorldData.getInstance().saveConfig();
			p.sendMessage(ChatColor.GREEN + "End spawn has been set!.");

		}
		return true;
	}

}
