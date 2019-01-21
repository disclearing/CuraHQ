package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.deathban.Deathban;
import club.curahq.core.user.FactionUser;
import club.curahq.core.util.core.Cooldowns;

import java.util.UUID;

public class DonatorReviveCommand implements CommandExecutor {
	private final Core plugin;

	public DonatorReviveCommand(final Core plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [player]");
			return true;
		}
		final Player player = (Player) sender;
		final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if (Cooldowns.isOnCooldown("revive_cooldown", player)) {
			sender.sendMessage("§cYou cannot do this for another §l"
					+ Cooldowns.getCooldownForPlayerInt("revive_cooldown", player) / 60 + " §cminutes.");
			return true;
		}
		final UUID targetUUID = target.getUniqueId();
		final FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
		final Deathban deathban = factionTarget.getDeathban();
		if (deathban == null || !deathban.isActive()) {
			sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
			return true;
		}

		factionTarget.removeDeathban();
		sender.sendMessage(
				ChatColor.GRAY + "You have revived " + ChatColor.GOLD + target.getName() + ChatColor.GRAY + '.');
		Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + ChatColor.GRAY + " has use their donator revive on " + ChatColor.GOLD + target.getName()
				+ ChatColor.GRAY + "." + "You can purchase this at " + ChatColor.GOLD + Core.getPlugin().config.getString("store"));
		Cooldowns.addCooldown("revive_cooldown", player, 3600);

		return true;
	}

}
