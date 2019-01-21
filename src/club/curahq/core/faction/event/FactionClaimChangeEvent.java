package club.curahq.core.faction.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.event.cause.ClaimChangeCause;
import club.curahq.core.faction.type.ClaimableFaction;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

public class FactionClaimChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private final ClaimChangeCause cause;
    private final Collection<Claim> affectedClaims;
    private final ClaimableFaction claimableFaction;
    private final CommandSender sender;
    private boolean cancelled;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public FactionClaimChangeEvent(final CommandSender sender, final ClaimChangeCause cause, final Collection<Claim> affectedClaims, final ClaimableFaction claimableFaction) {
        Preconditions.checkNotNull((Object) sender, (Object) "CommandSender cannot be null");
        Preconditions.checkNotNull((Object) cause, (Object) "Cause cannot be null");
        Preconditions.checkNotNull((Object) affectedClaims, (Object) "Affected claims cannot be null");
        Preconditions.checkNotNull((Object) affectedClaims.isEmpty(), (Object) "Affected claims cannot be empty");
        Preconditions.checkNotNull((Object) claimableFaction, (Object) "ClaimableFaction cannot be null");
        this.sender = sender;
        this.cause = cause;
        this.affectedClaims = (Collection<Claim>) ImmutableList.copyOf((Collection) affectedClaims);
        this.claimableFaction = claimableFaction;
    }

    public static HandlerList getHandlerList() {
        return FactionClaimChangeEvent.handlers;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public ClaimChangeCause getCause() {
        return this.cause;
    }

    public Collection<Claim> getAffectedClaims() {
        return this.affectedClaims;
    }

    public ClaimableFaction getClaimableFaction() {
        return this.claimableFaction;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return FactionClaimChangeEvent.handlers;
    }
}
