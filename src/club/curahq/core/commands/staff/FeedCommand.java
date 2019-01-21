package club.curahq.core.commands.staff;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableSet;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseConstants;

public class FeedCommand implements CommandExecutor {
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player onlyTarget = null;
		Collection<Player> targets;

		if ((args.length > 0) && (sender.hasPermission(command.getPermission() + ".others"))) {
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
				sender.sendMessage(ChatColor.RED + "Useage: /Feed <player>");
				return true;
			}
			targets = ImmutableSet.of(onlyTarget = (Player) sender);
		}
		if ((onlyTarget != null) && (onlyTarget.getFoodLevel() == 20)) {
			sender.sendMessage(ChatColor.RED + onlyTarget.getName() + " already has full hunger.");
			return true;
		}
		for (Player target : targets) {
			target.removePotionEffect(PotionEffectType.HUNGER);
			target.setFoodLevel(20);
		}
		sender.sendMessage(ChatColor.GRAY.toString() + "Your hunger has been replenished"
				+ (onlyTarget == null ? "all online players" : onlyTarget.getName()) + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? null : Collections.emptyList();
	}
}
