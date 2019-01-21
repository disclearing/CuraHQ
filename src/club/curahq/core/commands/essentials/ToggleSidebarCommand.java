package club.curahq.core.commands.essentials;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.scoreboard.PlayerBoard;

public class ToggleSidebarCommand implements CommandExecutor, TabExecutor {
	private final Core plugin;

	public ToggleSidebarCommand(Core plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		PlayerBoard playerBoard = this.plugin.getScoreboardHandler().getPlayerBoard(((Player) sender).getUniqueId());
		boolean newVisibile = !playerBoard.isSidebarVisible();
		playerBoard.setSidebarVisible(newVisibile);
		sender.sendMessage(ChatColor.GRAY + "You have " + (newVisibile ? ChatColor.GREEN + "enabled" : new StringBuilder().append(ChatColor.RED).append("disabled").toString()) + ChatColor.GRAY + " your §6§lScoreboard");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}
}
