package club.curahq.core.faction.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import club.curahq.core.faction.type.Faction;

public class FactionRenameEvent extends FactionEvent implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final CommandSender sender;
    private final String originalName;
    private boolean cancelled;
    private String newName;

    public FactionRenameEvent(final Faction faction, final CommandSender sender, final String originalName, final String newName) {
        super(faction);
        this.sender = sender;
        this.originalName = originalName;
        this.newName = newName;
    }

    public static HandlerList getHandlerList() {
        return FactionRenameEvent.handlers;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public String getNewName() {
        return this.newName;
    }

    public void setNewName(final String newName) {
        if(!newName.equals(this.newName)) {
            this.newName = newName;
        }
    }

    public boolean isCancelled() {
        return this.cancelled || this.originalName.equals(this.newName);
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return FactionRenameEvent.handlers;
    }
}
