package club.curahq.core.commands.staff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Optional;

import club.curahq.core.crowbar.Crowbar;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;

public class CrowbarCommand implements CommandExecutor, TabCompleter {
	private final List<String> completions;

	public CrowbarCommand() {
		this.completions = Arrays.asList(new String[] { "spawn", "setspawners", "setendframes" });
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
			return true;
		}
		Player player = (Player) sender;
		if (args[0].equalsIgnoreCase("spawn")) {
			ItemStack stack = new Crowbar().getItemIfPresent();
			player.getInventory().addItem(new ItemStack[] { stack });
			sender.sendMessage(ChatColor.GRAY + "You have given yourself a " + ChatColor.AQUA + stack.getItemMeta().getDisplayName()
					+ ChatColor.GRAY + '.');
			return true;
		}
		Optional<Crowbar> crowbarOptional = Crowbar.fromStack(player.getItemInHand());
		if (!crowbarOptional.isPresent()) {
			sender.sendMessage(ChatColor.RED + "You are not holding a Crowbar.");
			return true;
		}
		if (args[0].equalsIgnoreCase("setspawners")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
				return true;
			}
			Integer amount = JavaUtils.tryParseInt(args[1]);
			if (amount == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
				return true;
			}
			if (amount.intValue() < 0) {
				sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
				return true;
			}
			if (amount.intValue() > 1) {
				sender.sendMessage(ChatColor.RED + "Crowbars have maximum Spawner uses of " + 1 + '.');
				return true;
			}
			Crowbar crowbar = (Crowbar) crowbarOptional.get();
			crowbar.setSpawnerUses(amount.intValue());
			player.setItemInHand(crowbar.getItemIfPresent());
			sender.sendMessage(ChatColor.GRAY + "Set Spawner uses of held Crowbar to " + ChatColor.AQUA + amount + ChatColor.GRAY + '.');
			return true;
		}
		if (!args[0].equalsIgnoreCase("setendframes")) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
			return true;
		}
		Integer amount = JavaUtils.tryParseInt(args[1]);
		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
			return true;
		}
		if (amount.intValue() < 0) {
			sender.sendMessage(ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
			return true;
		}
		if (amount.intValue() > 5) {
			sender.sendMessage(ChatColor.RED + "Crowbars have maximum End Frame uses of " + 1 + '.');
			return true;
		}
		Crowbar crowbar = (Crowbar) crowbarOptional.get();
		crowbar.setEndFrameUses(amount.intValue());
		player.setItemInHand(crowbar.getItemIfPresent());
		sender.sendMessage(ChatColor.GRAY + "Set End Frame uses of held Crowbar to " + ChatColor.AQUA + amount + ChatColor.GRAY + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? BukkitUtils.getCompletions(args, this.completions) : Collections.emptyList();
	}
}
