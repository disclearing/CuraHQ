package club.curahq.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.listener.GodListener;

public class GodCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("god")) {
			if (!sender.hasPermission("command.god")) {
				sender.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
				return true;
			}
			if (args.length < 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You cannot execute this command from CONSOLE");
					return true;
				}
				Player p = (Player) sender;
				if (GodListener.getInstance().isinGod(p)) {
					GodListener.getInstance().setGod(p, false);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou have &cdisabled &fyour &6GOD MODE"));
					return true;
				}
				GodListener.getInstance().setGod(p, true);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou have &aenabled &fyour &6GOD MODE"));
				return true;
			}
			if (!sender.hasPermission("command.god.others")) {
				sender.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
				return true;
			}
			Player t = Bukkit.getPlayer(args[0]);
			if (t == null) {
				sender.sendMessage("�cPlayer not found.");
				return true;
			}
			if (GodListener.getInstance().isinGod(t)) {
				GodListener.getInstance().setGod(t, false);
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou have &cdisabled &f" + t.getName() + "'s &6GOD MODE"));
				return true;
			}
			GodListener.getInstance().setGod(t, true);
			t.setAllowFlight(true);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou have &aenabled &f" + t.getName() + "'s &6GOD MODE"));
			return true;
		}
		return false;
	}

}
