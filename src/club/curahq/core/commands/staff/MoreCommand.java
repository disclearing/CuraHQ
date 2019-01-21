package club.curahq.core.commands.staff;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.util.core.BaseCommand;

public class MoreCommand extends BaseCommand {
	public MoreCommand() {
		super("more", "Sets your item to its maximum amount.");
		setUsage("/(command)");
	}

	public boolean isPlayerOnlyCommand() {
		return true;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
			return true;
		}
		Player player = (Player) sender;
		ItemStack stack = player.getItemInHand();
		if ((stack == null) || (stack.getType() == Material.AIR)) {
			sender.sendMessage(ChatColor.RED + "You are not holding any item.");
			return true;
		}
		Integer amount = null;
		if (args.length > 0) {
			Integer amount1 = Integer.valueOf(Integer.parseInt(args[0]));
			if (amount1 == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number.");
				return true;
			}
			if (amount1.intValue() <= 0) {
				sender.sendMessage(ChatColor.RED + "Item amounts must be positive.");
				return true;
			}
		} else {
			int curAmount = stack.getAmount();
			if (curAmount >= (amount = Integer.valueOf(stack.getMaxStackSize())).intValue()) {
				sender.sendMessage(ChatColor.RED + "You already have the maximum amount: " + amount + '.');
				return true;
			}
		}
		stack.setAmount(amount.intValue());
		return true;
	}
}