package club.curahq.core.commands.essentials;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.timer.type.LogoutTimer;

public class LogoutCommand implements CommandExecutor, TabCompleter {
	private final Core plugin;

	public LogoutCommand(Core plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		LogoutTimer logoutTimer = this.plugin.getTimerManager().logoutTimer;
		if (!logoutTimer.setCooldown(player, player.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "Your " + logoutTimer.getDisplayName() + ChatColor.RED
					+ " timer is already active.");
			return true;
		}
		sender.sendMessage(
				ChatColor.RED + "Your " + logoutTimer.getDisplayName() + ChatColor.RED + " timer has started.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}
}
