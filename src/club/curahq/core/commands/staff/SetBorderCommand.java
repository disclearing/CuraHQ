package club.curahq.core.commands.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.google.common.base.Optional;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.config.WorldData;
import compat.com.google.common.collect.GuavaCompat;

public class SetBorderCommand implements CommandExecutor, TabCompleter {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <worldType> <amount>");
			return true;
		}
		Optional<World.Environment> optional = GuavaCompat.getIfPresent(World.Environment.class, args[0]);
		if (!optional.isPresent()) {
			sender.sendMessage(ChatColor.RED + "Environment '" + args[0] + "' not found.");
			return true;
		}
		Integer amount = JavaUtils.tryParseInt(args[1]);
		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
			return true;
		}
		if (amount.intValue() < 50) {
			sender.sendMessage(ChatColor.RED + "Minimum border size is " + 50 + 100 + '.');
			return true;
		}
		if (amount.intValue() > 250000) {
			sender.sendMessage(ChatColor.RED + "Maximum border size is " + 250000 + '.');
			return true;
		}
		World.Environment environment = (World.Environment) optional.get();
		WorldData.getInstance().getConfig().set("world-" + environment + "-border", amount);
		WorldData.getInstance().saveConfig();
		Command.broadcastCommandMessage(sender,
				ChatColor.GRAY + "Set border size of environment " + environment.name() + " to " + amount + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return Collections.emptyList();
		}
		World.Environment[] values = World.Environment.values();
		List<String> results = new ArrayList<String>(values.length);
		World.Environment[] arrayOfEnvironment1;
		int j = (arrayOfEnvironment1 = values).length;
		for (int i = 0; i < j; i++) {
			World.Environment environment = arrayOfEnvironment1[i];
			results.add(environment.name());
		}
		return BukkitUtils.getCompletions(args, results);
	}
}
