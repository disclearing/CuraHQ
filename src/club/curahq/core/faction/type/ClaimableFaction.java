package club.curahq.core.faction.type;

import com.google.common.collect.ImmutableMap;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.event.FactionClaimChangeEvent;
import club.curahq.core.faction.event.FactionClaimChangedEvent;
import club.curahq.core.faction.event.cause.ClaimChangeCause;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.GenericUtils;
import club.curahq.core.util.cuboid.Cuboid;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClaimableFaction extends Faction {
	  protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS = ImmutableMap.of(World.Environment.NETHER, "Nether", 
			    World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End");

    protected final Set<Claim> claims;

    public ClaimableFaction(final String name) {
        super(name);
        this.claims = new HashSet<>();
    }

    public ClaimableFaction(final Map<String, Object> map) {
        super(map);
        (this.claims = new HashSet<>()).addAll(GenericUtils.createList(map.get("claims"), Claim.class));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList(claims));
        return map;
    }

    @Override
    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender));
        for(final Claim claim : this.claims) {
            final Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.WHITE.toString() +  ClaimableFaction.ENVIRONMENT_MAPPINGS.get( location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }

    public Set<Claim> getClaims() {
        return  this.claims;
    }

    public boolean addClaim(final Claim claim,  final CommandSender sender) {
        return this.addClaims(Collections.singleton(claim), sender);
    }

    public void setClaim(final Cuboid cuboid, final CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        final Location min = cuboid.getMinimumPoint();
        min.setY(0);
        final Location max = cuboid.getMaximumPoint();
        max.setY(256);
        addClaim(new Claim(this, min, max), sender);
    }

    public boolean addClaims(final Collection<Claim> adding,  CommandSender sender) {
        if(sender == null) {
            sender =  Bukkit.getConsoleSender();
        }
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent( new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }

    public boolean removeClaim(final Claim claim,  final CommandSender sender) {
        return this.removeClaims(Collections.singleton(claim), sender);
    }

    public boolean removeClaims(final Collection<Claim> removing,  CommandSender sender) {
        if(sender == null) {
            sender =  Bukkit.getConsoleSender();
        }
        int previousClaims = this.claims.size();
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent( event);
        if(event.isCancelled() || !this.claims.removeAll(removing)) {
            return false;
        }
        if(this instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction) this;
            final Location home = playerFaction.getHome();
            final Core plugin = Core.getPlugin();
            int refund = 0;
            for(final Claim claim : removing) {
                refund += plugin.getClaimHandler().calculatePrice(claim, previousClaims, true);
                if(previousClaims > 0) {
                    --previousClaims;
                }
                if(home != null && claim.contains(home)) {
                    playerFaction.setHome(null);
                    playerFaction.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
                    break;
                }
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
            playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
        }
        Bukkit.getPluginManager().callEvent( new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
}