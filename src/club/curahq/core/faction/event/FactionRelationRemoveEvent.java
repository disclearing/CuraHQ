package club.curahq.core.faction.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import club.curahq.core.faction.struct.Relation;
import club.curahq.core.faction.type.PlayerFaction;

public class FactionRelationRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final PlayerFaction senderFaction;
    private final PlayerFaction targetFaction;
    private final Relation relation;
    private boolean cancelled;

    public FactionRelationRemoveEvent(final PlayerFaction senderFaction, final PlayerFaction targetFaction, final Relation relation) {
        this.senderFaction = senderFaction;
        this.targetFaction = targetFaction;
        this.relation = relation;
    }

    public static HandlerList getHandlerList() {
        return FactionRelationRemoveEvent.handlers;
    }

    public PlayerFaction getSenderFaction() {
        return this.senderFaction;
    }

    public PlayerFaction getTargetFaction() {
        return this.targetFaction;
    }

    public Relation getRelation() {
        return this.relation;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return FactionRelationRemoveEvent.handlers;
    }
}
