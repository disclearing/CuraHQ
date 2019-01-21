package club.curahq.core.faction.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import club.curahq.core.faction.type.PlayerFaction;

import java.util.UUID;

public class FactionFocusChangeEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final PlayerFaction senderFaction;
    private final Player player;
    private final UUID oldFocus;


    public FactionFocusChangeEvent(final PlayerFaction senderFaction, Player player, UUID oldFocus) {
        this.senderFaction = senderFaction;
        this.player = player;
        this.oldFocus = oldFocus;
    }

    public UUID getOldFocus() {
        return oldFocus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerFaction getSenderFaction() {
        return this.senderFaction;
    }


    public Player getPlayer() {
        return this.player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
