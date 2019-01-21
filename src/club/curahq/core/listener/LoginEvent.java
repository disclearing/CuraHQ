package club.curahq.core.listener;

import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import club.curahq.core.Core;
import club.curahq.core.prefix.PrefixMenu;
import club.curahq.core.util.core.ConfigUtil;
import net.md_5.bungee.api.ChatColor;

public class LoginEvent implements Listener {
	@EventHandler
	public void Join(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 2F, 1F);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m-----------------------------"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.SECONDAIRY_COLOR + "Welcome to " + ConfigUtil.PRIMAIRY_COLOR + ConfigUtil.SERVER_NAME + ConfigUtil.THIRD_COLOR + " (Map " + Core.config.getString("CURRENT-MAP")) + ")");
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &8» " + ConfigUtil.THIRD_COLOR + "Website&7 " + Core.config.getString("website")));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &8» " + ConfigUtil.THIRD_COLOR + "Teamspeak&7 " + Core.config.getString("teamspeak-frozen")));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &8» " + ConfigUtil.THIRD_COLOR + "Store&7 " + Core.config.getString("store")));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m-----------------------------"));
		if(PrefixMenu.config.getString(p.getUniqueId() + ".prefix") == null) {
			PrefixMenu.config.set(p.getUniqueId() + ".prefix", "");
			try {
				PrefixMenu.savePrefixFile(PrefixMenu.config, PrefixMenu.configFile);
				PrefixMenu.reloadPrefixFile(PrefixMenu.config, PrefixMenu.configFile);
			} catch (InvalidConfigurationException e2) {
				e2.printStackTrace();
			}
		}
	}
}
