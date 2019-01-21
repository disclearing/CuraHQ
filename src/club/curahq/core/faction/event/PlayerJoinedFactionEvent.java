package club.curahq.core.faction.event;

import com.google.common.base.Optional;

import club.curahq.core.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerJoinedFactionEvent extends FactionEvent {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final UUID uniqueID;
    private Optional<Player> player;

    public PlayerJoinedFactionEvent(final Player player, final PlayerFaction playerFaction) {
        super(playerFaction);
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
    }

    public PlayerJoinedFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction) {
        super(playerFaction);
        this.uniqueID = playerUUID;
    }

    public static HandlerList getHandlerList() {
        return PlayerJoinedFactionEvent.handlers;
    }

        @Override
    public PlayerFaction getFaction() {
        return (PlayerFaction)this.faction;
    }

    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public HandlerList getHandlers() {
        return PlayerJoinedFactionEvent.handlers;
    }
}
