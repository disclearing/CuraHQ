package club.curahq.core.faction.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import club.curahq.core.faction.type.Faction;

public class PlayerClaimEnterEvent extends Event implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final Player player;
    private final Faction fromFaction;
    private final Faction toFaction;
    private final Location from;
    private final Location to;
    private final EnterCause enterCause;
    private boolean cancelled;

    public PlayerClaimEnterEvent(final Player player, final Location from, final Location to, final Faction fromFaction, final Faction toFaction, final EnterCause enterCause) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.fromFaction = fromFaction;
        this.toFaction = toFaction;
        this.enterCause = enterCause;
    }

    public static HandlerList getHandlerList() {
        return PlayerClaimEnterEvent.handlers;
    }

    public Faction getFromFaction() {
        return this.fromFaction;
    }

    public Faction getToFaction() {
        return this.toFaction;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }

    public EnterCause getEnterCause() {
        return this.enterCause;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return PlayerClaimEnterEvent.handlers;
    }

    public enum EnterCause {
        TELEPORT,
        MOVEMENT;
    }
}
