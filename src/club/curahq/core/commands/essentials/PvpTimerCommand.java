package club.curahq.core.commands.essentials;

import com.google.common.collect.ImmutableList;

import club.curahq.core.Core;
import club.curahq.core.timer.type.PvPTimerProtection;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.DurationFormatter;
import club.curahq.core.util.config.PlayerData;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PvpTimerCommand implements CommandExecutor, TabCompleter {
	private final Core plugin;

	public PvpTimerCommand(Core plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		PvPTimerProtection pvpTimer = this.plugin.getTimerManager().invincibilityTimer;
		if (args.length < 1) {
			printUsage(sender, label, pvpTimer);
			return true;
		}
		if ((args[0].equalsIgnoreCase("enable")) || (args[0].equalsIgnoreCase("remove"))
				|| (args[0].equalsIgnoreCase("off"))) {
			if (pvpTimer.getRemaining(player) <= 0L) {
				sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED
						+ " is currently not active.");
				return true;
			}
			sender.sendMessage(
					ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is now off.");
			pvpTimer.clearCooldown(player);
			PlayerData.getInstance().getConfig().set("UUID." + player.getUniqueId() + ".Pvp-Timer", Integer.valueOf(0));
			PlayerData.getInstance().saveConfig();
			return true;
		}
		if ((args[0].equalsIgnoreCase("remaining")) || (args[0].equalsIgnoreCase("time"))
				|| (args[0].equalsIgnoreCase("left")) || (args[0].equalsIgnoreCase("check"))) {
			long remaining = pvpTimer.getRemaining(player);
			if (remaining <= 0L) {
				sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED
						+ " timer is currently not active.");
				return true;
			}
			sender.sendMessage(ChatColor.GRAY + "Your " + ChatColor.GOLD + pvpTimer.getDisplayName() + "Timer" + ChatColor.GRAY + " is active for another " + ChatColor.GOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.GRAY + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');

			return true;
		}
		printUsage(sender, label, pvpTimer);
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
	}

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");

	private void printUsage(CommandSender sender, String label, PvPTimerProtection pvpTimer) {
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage("§6PvP Help");
		sender.sendMessage(ChatColor.YELLOW + " /pvp enable" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Remove your PvP Protection.");
		sender.sendMessage(ChatColor.YELLOW + " /pvp time" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Check the remaining for your PvP Protection.");
		sender.sendMessage(ChatColor.YELLOW + " /lives" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Info about lives and deathbans.");
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}
}
