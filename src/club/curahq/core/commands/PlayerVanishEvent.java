package club.curahq.core.commands;

import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerVanishEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final boolean vanished;
	private final Collection<Player> viewers;
	private boolean cancelled;

	public PlayerVanishEvent(Player player, Collection<Player> viewers, boolean vanished) {
		super(player);
		this.viewers = viewers;
		this.vanished = vanished;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Collection<Player> getViewers() {
		return this.viewers;
	}

	public boolean isVanished() {
		return this.vanished;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}