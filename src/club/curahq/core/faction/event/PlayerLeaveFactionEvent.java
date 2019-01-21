package club.curahq.core.faction.event;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import club.curahq.core.faction.event.cause.FactionLeaveCause;
import club.curahq.core.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerLeaveFactionEvent extends FactionEvent implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final UUID uniqueID;
    private final FactionLeaveCause cause;
    private boolean cancelled;
    private Optional<Player> player;

    public PlayerLeaveFactionEvent(final Player player, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull((Object) player, (Object) "Player cannot be null");
        Preconditions.checkNotNull((Object) playerFaction, (Object) "Player faction cannot be null");
        Preconditions.checkNotNull((Object) "Leave cause cannot be null");
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
        this.cause = cause;
    }

    public PlayerLeaveFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction, final FactionLeaveCause cause) {
        super(playerFaction);
        Preconditions.checkNotNull((Object) playerUUID, (Object) "Player UUID cannot be null");
        Preconditions.checkNotNull((Object) playerFaction, (Object) "Player faction cannot be null");
        Preconditions.checkNotNull((Object) "Leave cause cannot be null");
        this.uniqueID = playerUUID;
        this.cause = cause;
    }

    public static HandlerList getHandlerList() {
        return PlayerLeaveFactionEvent.handlers;
    }

    public Optional<Player> getPlayer() {
        if(this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public FactionLeaveCause getLeaveCause() {
        return this.cause;
    }

    public HandlerList getHandlers() {
        return PlayerLeaveFactionEvent.handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
