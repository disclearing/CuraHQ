package club.curahq.core.commands.staff;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableSet;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseConstants;
import net.md_5.bungee.api.ChatColor;

public class ClearCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player onlyTarget = null;
		Collection<Player> targets;

		if ((args.length > 0) && (sender.hasPermission("command.clearinventory"))) {
			if ((args[0].equalsIgnoreCase("all")) && (sender.hasPermission(command.getPermission() + ".all"))) {
				targets = ImmutableSet.copyOf(Bukkit.getOnlinePlayers());
			} else {
				if (((onlyTarget = BukkitUtils.playerWithNameOrUUID(args[0])) == null)) {
					sender.sendMessage(
							String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
					return true;
				}
				targets = ImmutableSet.of(onlyTarget);
			}
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Useage: /ci <player>");
				return true;
			}
			targets = ImmutableSet.of(onlyTarget = (Player) sender);
		}
		for (Player target : targets) {
			target.removePotionEffect(PotionEffectType.HUNGER);
			target.setFoodLevel(20);
			target.getInventory().clear();
			target.getInventory().setHelmet(new ItemStack(Material.AIR));
			target.getInventory().setChestplate(new ItemStack(Material.AIR));
			target.getInventory().setLeggings(new ItemStack(Material.AIR));
			target.getInventory().setBoots(new ItemStack(Material.AIR));
		}
		sender.sendMessage(ChatColor.GRAY.toString() + "Your inventory has been cleared.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? null : Collections.emptyList();
	}
}
