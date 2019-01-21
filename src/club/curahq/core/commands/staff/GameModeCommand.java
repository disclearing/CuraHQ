package club.curahq.core.commands.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseConstants;

public class GameModeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /gamemode <mode> <player>");
			return true;
		}
		GameMode mode = getGameModeByName(args[0]);
		if (mode == null) {
			sender.sendMessage(ChatColor.RED + "Gamemode '" + args[0] + "' not found.");
			return true;
		}
		Player target;
		if (args.length > 1) {
			if (sender.hasPermission(command.getPermission() + ".others")) {
				target = BukkitUtils.playerWithNameOrUUID(args[1]);
			} else {
				target = null;
			}
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Usage: /gamemode <mode> <player>");
				return true;
			}
			target = (Player) sender;
		}
		if ((target == null)) {
			sender.sendMessage(
					String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[1] }));
			return true;
		}
		if (target.getGameMode() == mode) {
			sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + '.');
			return true;
		}
		target.setGameMode(mode);
		Command.broadcastCommandMessage(sender, ChatColor.WHITE + "Set gamemode of " + ChatColor.GOLD + target.getName() + ChatColor.WHITE + " to " + ChatColor.GOLD + mode.name() + ChatColor.WHITE + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return Collections.emptyList();
		}
		GameMode[] gameModes = GameMode.values();
		List<String> results = new ArrayList<String>(gameModes.length);
		GameMode[] arrayOfGameMode1;
		int j = (arrayOfGameMode1 = gameModes).length;
		for (int i = 0; i < j; i++) {
			GameMode mode = arrayOfGameMode1[i];
			results.add(mode.name());
		}
		return BukkitUtils.getCompletions(args, results);
	}

	private GameMode getGameModeByName(String id) {
		id = id.toLowerCase(Locale.ENGLISH);
		if ((id.equalsIgnoreCase("gmc")) || (id.contains("creat")) || (id.equalsIgnoreCase("1"))
				|| (id.equalsIgnoreCase("c"))) {
			return GameMode.CREATIVE;
		}
		if ((id.equalsIgnoreCase("gms")) || (id.contains("survi")) || (id.equalsIgnoreCase("0"))
				|| (id.equalsIgnoreCase("s"))) {
			return GameMode.SURVIVAL;
		}
		if ((id.equalsIgnoreCase("gma")) || (id.contains("advent")) || (id.equalsIgnoreCase("2"))
				|| (id.equalsIgnoreCase("a"))) {
			return GameMode.ADVENTURE;
		}
		if ((id.equalsIgnoreCase("gmt")) || (id.contains("toggle")) || (id.contains("cycle"))
				|| (id.equalsIgnoreCase("t"))) {
			return null;
		}
		return null;
	}
}