package club.curahq.core.util.core;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.curahq.core.util.config.PlayerData;

public class NoteApi {

	private static PlayerData note = PlayerData.getInstance();

	public static void addNote(Player target, Player sender, String reason, String date) {
		note.getConfig().set("UUID." + target.getUniqueId() + ".Name", String.valueOf(target.getName()));
		note.getConfig().set("UUID." + target.getUniqueId() + ".Reason", String.valueOf(reason));
		note.getConfig().set("UUID." + target.getUniqueId() + ".Created-By", String.valueOf(sender.getName()));
		note.getConfig().set("UUID." + target.getUniqueId() + ".Time-Created", date);
	}

	public static void removeNote(Player target) {
		note.getConfig().set("UUID." + target.getUniqueId() + ".Name", String.valueOf(target.getName()));
		note.getConfig().set("UUID." + target.getUniqueId() + ".Reason", String.valueOf("null"));
	}

	public static void checkNote(Player target, Player sender) {
		sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " Notes:");
		sender.sendMessage(ChatColor.GOLD + "Note: " + ChatColor.YELLOW + PlayerData.getInstance().getConfig().getString("UUID." + target.getUniqueId() + ".Reason"));
		sender.sendMessage(ChatColor.GOLD + "Created-By: " + ChatColor.YELLOW + PlayerData.getInstance().getConfig().getString("UUID." + target.getUniqueId() + ".Created-By"));
		sender.sendMessage(ChatColor.GOLD + "Time-Created: " + ChatColor.YELLOW + PlayerData.getInstance().getConfig().getString("UUID." + target.getUniqueId() + ".Time-Created"));
	}

}
