package club.curahq.core.listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import club.curahq.core.Core;

public class GodListener implements Listener {

	public GodListener() {
		Bukkit.getPluginManager().registerEvents(this, Core.getPlugin());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		Entity entity = e.getEntity();
		if ((entity instanceof Player)) {
			Player p = (Player) entity;
			if (GodListener.getInstance().isinGod(p))
				e.setCancelled(true);
		}
	}

	public static GodListener instance = new GodListener();

	public static GodListener getInstance() {
		return instance;
	}

	private ArrayList<Player> God = new ArrayList<Player>();

	public boolean isinGod(Player p) {
		return God.contains(p);
	}

	public void setGod(Player p, boolean b) {
		if (b) {
			if (isinGod(p))
				return;
			God.add(p);
		} else {
			if (!isinGod(p))
				return;
			God.remove(p);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignCreate(SignChangeEvent event) {
		Player player = event.getPlayer();
		if ((player != null) && (player.hasPermission("base.sign.colour"))) {
			String[] lines = event.getLines();
			for (int i = 0; i < lines.length; i++) {
				if ((!player.hasPermission("base.sign.admin"))
						&& ((event.getLine(i).contains(ChatColor.translateAlternateColorCodes('&', "Sell")))
								|| (event.getLine(i).contains("Buy")) || (event.getLine(i).contains("Kit")))) {
					player.sendMessage(ChatColor.RED + "You have used a sign that you're not allowed.");
					event.setCancelled(true);
				}
				event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
			}
		}
	}

}
