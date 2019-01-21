package club.curahq.core.listener.staff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import club.curahq.core.util.config.PlayerData;
import club.curahq.core.util.core.NoteApi;

public class NoteListener implements Listener {

	private String perm = "command.note";
	private PlayerData note = PlayerData.getInstance();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (note.getConfig().contains("UUID." + e.getPlayer().getUniqueId() + ".Reason") && (!note.getConfig()
					.getString("UUID." + e.getPlayer().getUniqueId() + ".Reason").equals("null"))) {
				if (online.hasPermission(perm)) {
					NoteApi.checkNote(e.getPlayer(), online);
				}
			} else {
				note.getConfig().set("UUID." + e.getPlayer().getUniqueId() + ".Reason", "null");
				note.saveConfig();
			}

		}
	}

}
