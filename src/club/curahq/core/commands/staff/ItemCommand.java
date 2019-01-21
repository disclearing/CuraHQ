package club.curahq.core.commands.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.util.core.BasePlugins;

public class ItemCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
			return true;
		}
		String amount = "";
		Player p = (Player) sender;
		if (args.length == 0) {
			p.sendMessage(ChatColor.RED + "Usage: /I <item>");
			return true;
		}
		if (BasePlugins.getPlugin().getItemDb().getItem(args[0]) == null) {
			sender.sendMessage(ChatColor.RED + "Item or ID not found.");
			return true;
		}
		if (args.length == 1) {
			if (!p.getInventory().addItem(new ItemStack[] { BasePlugins.getPlugin().getItemDb().getItem(args[0],
					BasePlugins.getPlugin().getItemDb().getItem(args[0]).getMaxStackSize()) }).isEmpty()) {
				p.sendMessage(ChatColor.RED + "Your inventory is full.");
				return true;
			}
		}
		if (args.length == 2) {
			if (!p.getInventory().addItem(
					new ItemStack[] { BasePlugins.getPlugin().getItemDb().getItem(args[0], Integer.parseInt(args[1])) })
					.isEmpty()) {
				p.sendMessage(ChatColor.RED + "Your inventory is full.");
				return true;
			}
			amount = args[1];
		}
		Command.broadcastCommandMessage(sender, ChatColor.GRAY + p.getName() + " gave himself " + amount + ", "
				+ BasePlugins.getPlugin().getItemDb().getName(BasePlugins.getPlugin().getItemDb().getItem(args[0])),
				true);
		return true;
	}
}
