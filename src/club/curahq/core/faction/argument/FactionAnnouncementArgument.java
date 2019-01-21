package club.curahq.core.faction.argument;

import com.google.common.collect.ImmutableList;

import club.curahq.core.Core;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.command.CommandArgument;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Created by TREHOME on 02/25/2016.
 */
public class FactionAnnouncementArgument extends CommandArgument {
	private final Core plugin;
	private static final ImmutableList<String> CLEAR_LIST;

	public FactionAnnouncementArgument(final Core plugin) {
		super("announcement", "Set your faction announcement.", new String[] { "announce" });
		this.plugin = plugin;
	}

	public String getUsage(final String label) {
		return '/' + label + ' ' + this.getName() + " <newAnnouncement>";
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}
		final Player player = (Player) sender;
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null) {
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			sender.sendMessage(ChatColor.RED + "You must be a officer to edit the faction announcement.");
			return true;
		}
		final String oldAnnouncement = playerFaction.getAnnouncement();
		String newAnnouncement;
		if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none")
				|| args[1].equalsIgnoreCase("remove")) {
			newAnnouncement = null;
		} else {
			newAnnouncement = StringUtils.join(args, ' ', 1, args.length);
		}
		if (oldAnnouncement == null && newAnnouncement == null) {
			sender.sendMessage(ChatColor.RED + "Your factions' announcement is already unset.");
			return true;
		}
		if (oldAnnouncement != null && newAnnouncement != null && oldAnnouncement.equals(newAnnouncement)) {
			sender.sendMessage(ChatColor.RED + "Your factions' announcement is already " + newAnnouncement + '.');
			return true;
		}
		playerFaction.setAnnouncement(newAnnouncement);
		if (newAnnouncement == null) {
			playerFaction.broadcast(
					ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " has cleared the factions' announcement.");
			return true;
		}
		playerFaction.broadcast(
				ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has updated the factions' announcement from "
						+ ChatColor.GREEN + ((oldAnnouncement != null) ? oldAnnouncement : "none") + ChatColor.YELLOW
						+ " to " + ChatColor.GREEN + newAnnouncement + ChatColor.YELLOW + '.');
		return true;
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (!(sender instanceof Player)) {
			return Collections.emptyList();
		}
		if (args.length == 2) {
			return FactionAnnouncementArgument.CLEAR_LIST;
		}
		return Collections.emptyList();
	}

	static {
		CLEAR_LIST = ImmutableList.of("clear");
	}
}
