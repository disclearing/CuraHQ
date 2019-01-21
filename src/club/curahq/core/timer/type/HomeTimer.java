package club.curahq.core.timer.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.util.core.ConfigUtil;

public class HomeTimer extends PlayerTimer implements Listener {
	public HomeTimer() {
		super(ConfigUtil.HOME_TIMER, TimeUnit.SECONDS.toMillis(10L), false);
	}

	public String getScoreboardPrefix() {
		return ChatColor.BLUE.toString() + ChatColor.BOLD;
	}

	private void checkMovement(Player player, Location from, Location to) {
		if ((from.getBlockX() == to.getBlockX()) && (from.getBlockZ() == to.getBlockZ())) {
			return;
		}
		if (getRemaining(player) > 0L) {
			player.sendMessage(
					ChatColor.RED + "You moved a block, " + getDisplayName() + ChatColor.RED + " timer cancelled.");
			clearCooldown(player);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
			clearCooldown(uuid);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
			clearCooldown(uuid);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if ((entity instanceof Player)) {
			Player player = (Player) entity;
			if (getRemaining(player) > 0L) {
				player.sendMessage(
						ChatColor.RED + "You were damaged, " + getDisplayName() + ChatColor.RED + " timer ended.");
				clearCooldown(player);
			}
		}
	}

}
