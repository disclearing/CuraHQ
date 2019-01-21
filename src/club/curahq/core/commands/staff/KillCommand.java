package club.curahq.core.commands.staff;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseCommand;

public class KillCommand extends BaseCommand {
	public KillCommand() {
		super("kill", "Kills a player.");
		setAliases(new String[] { "slay" });
		setUsage("/(command) <player>");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player target;
		if ((args.length > 0)
				&& (sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others"))) {
			target = BukkitUtils.playerWithNameOrUUID(args[0]);
		} else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(getUsage(label));
				return true;
			}
			target = (Player) sender;
		}
		if (target.isDead()) {
			sender.sendMessage(ChatColor.RED + target.getName() + " is already dead.");
			return true;
		}
		EntityDamageEvent event = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.SUICIDE, 10000);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			sender.sendMessage(ChatColor.RED + "You cannot kill " + target.getName() + '.');
			return true;
		}
		target.setLastDamageCause(event);
		target.setHealth(0.0D);
		if (sender.equals(target)) {
			sender.sendMessage(ChatColor.RED + "You have been killed.");
			return true;
		}
		Command.broadcastCommandMessage(sender, ChatColor.GRAY + "Slain player " + ChatColor.GOLD + target.getName() + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return (args.length == 1)
				&& (sender.hasPermission(String.valueOf(String.valueOf(command.getPermission())) + ".others")) ? null
						: Collections.emptyList();
	}
}