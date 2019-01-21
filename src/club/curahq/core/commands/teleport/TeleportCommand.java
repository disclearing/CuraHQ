package club.curahq.core.commands.teleport;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseConstants;

public class TeleportCommand implements CommandExecutor {
	static final int MAX_COORD = 30000000;
	static final int MIN_COORD = -30000000;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((args.length < 1) || (args.length > 4)) {
			sender.sendMessage(ChatColor.RED + "Usage: /Teleport <player> | <x , y , z>");
			return true;
		}
		Player targetA;
		if ((args.length == 1) || (args.length == 3)) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Usage: /Teleport <player> | <x , y , z>");
				return true;
			}
			targetA = (Player) sender;
		} else {
			targetA = BukkitUtils.playerWithNameOrUUID(args[0]);
		}
		if ((targetA == null)) {
			sender.sendMessage(
					String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
			return true;
		}
		if (args.length < 3) {
			Player targetB = BukkitUtils.playerWithNameOrUUID(args[(args.length - 1)]);
			if ((targetB == null)) {
				sender.sendMessage(ChatColor.RED + "Player  '" + ChatColor.GRAY + args[(args.length - 1)]
						+ ChatColor.RED + "' not found.");
				return true;
			}
			if (targetA.equals(targetB)) {
				sender.sendMessage(ChatColor.RED + "The teleportee and teleported are the same player.");
				return true;
			}
			if (targetA.teleport(targetB, PlayerTeleportEvent.TeleportCause.COMMAND)) {
				sender.sendMessage(
						ChatColor.GRAY + "Teleported " + targetA.getName() + " to " + targetB.getName() + '.');
			} else {
				sender.sendMessage(ChatColor.RED + "Failed to teleport you to " + targetB.getName() + '.');
			}
		} else if (targetA.getWorld() != null) {
			Location loc = new Location(targetA.getLocation().getWorld(), Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			targetA.teleport(loc);
			if (targetA.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND)) {
				sender.sendMessage(
						"You have been teleported to: " + ChatColor.GREEN + args[0] + ", " + args[1] + ", " + args[2]);
			} else {
				sender.sendMessage(ChatColor.RED + "Failed to teleport you.");
			}
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return (args.length == 1) || (args.length == 2) ? null : Collections.emptyList();
	}
}
