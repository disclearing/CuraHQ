package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((cmd.getName().equalsIgnoreCase("ping"))) {
			if (args.length < 1) {
				if ((sender instanceof Player)) {
					CraftPlayer handler = (CraftPlayer) sender;
					sender.sendMessage(ChatColor.GRAY + "You have a ping of " + ChatColor.GOLD
							+ handler.getHandle().playerConnection.player.ping + ChatColor.GRAY + " ms.");
				} else {
					sender.sendMessage(ChatColor.RED + "Correct usage: /ping <player>");
				}
			} else {
				boolean found = false;
				for (Player p : Bukkit.getOnlinePlayers()) {
					String name = p.getName();
					CraftPlayer handler = (CraftPlayer) p;
					if (name.equalsIgnoreCase(args[0])) {
						sender.sendMessage(ChatColor.GOLD + name + ChatColor.GRAY + " has a ping of " + ChatColor.GOLD
								+ handler.getHandle().playerConnection.player.ping + ChatColor.GRAY + " ms.");
						found = true;
						break;
					}
				}
				if (!found) {
					sender.sendMessage(ChatColor.RED + "Player not found.");
				}
			}
		}
		return true;
	}
}
