package club.curahq.core.commands.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseConstants;

public class InvSeeCommand implements Listener, CommandExecutor {
	private final InventoryType[] types;
	private final Map<InventoryType, Inventory> inventories;

	public InvSeeCommand(Core plugin) {
		this.types = new InventoryType[] { InventoryType.BREWING, InventoryType.CHEST, InventoryType.DISPENSER,
				InventoryType.ENCHANTING, InventoryType.FURNACE, InventoryType.HOPPER, InventoryType.PLAYER,
				InventoryType.WORKBENCH };
		this.inventories = new EnumMap<InventoryType, Inventory>(InventoryType.class);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public boolean isPlayerOnlyCommand() {
		return true;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
				return true;
			}
			Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
			if (target == null) {
				sender.sendMessage(
						String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
				return true;
			}
			sender.sendMessage(ChatColor.GRAY + "This players inventory contains: ");
			ItemStack[] arrayOfItemStack;
			int j = (arrayOfItemStack = target.getInventory().getContents()).length;
			for (int i = 0; i < j; i++) {
				ItemStack items = arrayOfItemStack[i];
				if (items != null) {
					sender.sendMessage(ChatColor.GOLD + items.getType().toString().replace("_", "").toLowerCase() + ": "
							+ items.getAmount());
				}
			}
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /Invsee <player>");
			return true;
		}
		Player player = (Player) sender;
		Inventory inventory = null;
		InventoryType[] types = this.types;
		int length = types.length;
		int i = 0;
		while (i < length) {
			InventoryType type = types[i];
			if (type.name().equalsIgnoreCase(args[0])) {
				Inventory inventoryRevert;
				inventory = (Inventory) this.inventories.putIfAbsent(type,
						inventoryRevert = Bukkit.createInventory(player, type));
				if (inventory != null) {
					break;
				}
				inventory = inventoryRevert;
				break;
			}
			i++;
		}
		if (inventory == null) {
			Player target2 = BukkitUtils.playerWithNameOrUUID(args[0]);
			if (sender.equals(target2)) {
				sender.sendMessage(ChatColor.RED + "You cannot check the inventory of yourself.");
				return true;
			}
			if ((target2 == null)) {
				sender.sendMessage(
						String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
				return true;
			}
			inventory = target2.getInventory();
		}
		player.openInventory(inventory);
		return true;
	}

	@SuppressWarnings("deprecation")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			return Collections.emptyList();
		}
		InventoryType[] values = InventoryType.values();
		List<String> results = new ArrayList<String>(values.length);
		Player senderPlayer = (Player) sender;
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
		for (int i = 0; i < j; i++) {
			Player target = arrayOfPlayer[i];
			if ((senderPlayer == null) || (senderPlayer.canSee(target))) {
				results.add(target.getName());
			}
		}
		return BukkitUtils.getCompletions(args, results);
	}
}
