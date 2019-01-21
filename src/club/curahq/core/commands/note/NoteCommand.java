package club.curahq.core.commands.note;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.util.config.PlayerData;
import club.curahq.core.util.core.BaseConstants;
import club.curahq.core.util.core.NoteApi;

public class NoteCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(ChatColor.RED + "Please use the server to execute this command.");
			return true;
		}
		Player player = (Player) cs;
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "/note <add|remove|check> <player> <note>");
			return true;
		}
		if ((Bukkit.getPlayer(args[1]) == null) && (Bukkit.getOfflinePlayer(args[1]) == null)) {
			cs.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
			return true;
		}
		OfflinePlayer starget = Bukkit.getOfflinePlayer(args[1]);
		Player targetUser = (Player) starget;
		if (targetUser == null) {
			cs.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
			return true;
		}
		String note = StringUtils.join(args, ' ', 2, args.length);
		String time;
		if (args[0].equalsIgnoreCase("add")) {
			if (note == null) {
				player.sendMessage(ChatColor.RED + "You need to add a note to this player.");
				return true;
			}
			time = DateFormatUtils.format(System.currentTimeMillis(), "hh:mm");
			NoteApi.addNote(targetUser, player, note, time);
			player.sendMessage(ChatColor.GRAY + "You added a note to " + targetUser.getName() + ".");
			return true;
		}
		if (args[0].equalsIgnoreCase("remove")) {
			if (!player.hasPermission("command.note.remove")) {
				player.sendMessage(ChatColor.RED + "No permission to this argument.");
				return true;
			}
			if (PlayerData.getInstance().getConfig().contains("UUID." + targetUser.getUniqueId() + ".Reason")) {
				player.sendMessage(
						ChatColor.GOLD + targetUser.getName() + ChatColor.GRAY + " notes has been removed.");
				NoteApi.removeNote(targetUser);

			} else {
				player.sendMessage(ChatColor.RED + "The player does not contain any notes.");
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("check")) {
			NoteApi.checkNote(targetUser, player);
		}
		return false;
	}
}
