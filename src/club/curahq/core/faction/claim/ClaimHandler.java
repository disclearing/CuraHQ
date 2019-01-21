package club.curahq.core.faction.claim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import club.curahq.core.Core;
import club.curahq.core.faction.FactionManager;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.faction.type.RoadFaction;
import club.curahq.core.faction.type.WildernessFaction;
import club.curahq.core.util.ItemBuilder;
import club.curahq.core.util.core.ConfigUtil;
import club.curahq.core.util.cuboid.Cuboid;
import club.curahq.core.visualise.VisualType;

public class ClaimHandler {
    public static final int MIN_CLAIM_HEIGHT = 0;
    public static final int MAX_CLAIM_HEIGHT = 256;
    public static final ItemStack CLAIM_WAND;
    public static final int MIN_CLAIM_RADIUS = 5;
    public static final int MAX_CHUNKS_PER_LIMIT = 16;
    private static final int NEXT_PRICE_MULTIPLIER_AREA = 250;
    private static final int NEXT_PRICE_MULTIPLIER_CLAIM = 500;
    private static final double CLAIM_SELL_MULTIPLIER = 0.8;
    private static final double CLAIM_PRICE_PER_BLOCK = 0.25;
    static {
        CLAIM_WAND = new ItemBuilder(Material.DIAMOND_HOE).displayName(ChatColor.GREEN.toString() + "Claim Wand").lore(new String[]{ChatColor.BLUE + "Left or Right Click " + ChatColor.GREEN + "a Block" + ChatColor.BLUE + " to:", ChatColor.GRAY + "Set the first and second position of ", ChatColor.GRAY + "your Claim selection.", "", ChatColor.BLUE + "Right Click " + ChatColor.GREEN + "the Air" + ChatColor.BLUE + " to:", ChatColor.GRAY + "Clear your current Claim selection.", "", ChatColor.BLUE + "Shift " + ChatColor.BLUE + "Left Click " + ChatColor.GREEN + "the Air or a Block" + ChatColor.BLUE + " to:", ChatColor.GRAY + "Purchase your current Claim selection."}).build();
    }

	public final Map<UUID, ClaimSelection> claimSelectionMap;
    private final Core plugin;

    public ClaimHandler(final Core plugin) {
        this.plugin = plugin;
        this.claimSelectionMap = new HashMap<>();
    }

    public int calculatePrice(final Cuboid claim, int currentClaims, final boolean selling) {
        if(currentClaims == -1 || !claim.hasBothPositionsSet()) {
            return 0;
        }
        int multiplier = 1;
        int remaining = claim.getArea();
        double price = 0.0;
        while(remaining > 0) {
            if(--remaining % NEXT_PRICE_MULTIPLIER_AREA == 0) {
                ++multiplier;
            }
            price += CLAIM_PRICE_PER_BLOCK * multiplier;
        }
        if(currentClaims != 0) {
            currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
            price += currentClaims * NEXT_PRICE_MULTIPLIER_CLAIM;
        }
        if(selling) {
            price *= CLAIM_SELL_MULTIPLIER;
        }
        return (int) price;
    }

    public boolean clearClaimSelection(final Player player) {
        final ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.remove(player.getUniqueId());
        if(claimSelection != null) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, null);
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
	public boolean canSubclaimHere(final Player player, final Location location) {
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if(playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to subclaim land.");
            return false;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if(!this.plugin.getFactionManager().getFactionAt(location).equals(playerFaction)) {
            player.sendMessage(ChatColor.RED + "This location is not part of your factions' territory.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
	public boolean canClaimHere(final Player player, final Location location) {
        final World world = location.getWorld();
        if(world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        if(!(this.plugin.getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the " + ConfigUtil.WILDERNESS_COLOUR + "Wilderness" + ChatColor.RED + ". " + "Make sure you are past " + 800 + " blocks from spawn..");
            return false;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if(playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if(playerFaction.getClaims().size() >= 8) {
            player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        final int locX = location.getBlockX();
        final int locZ = location.getBlockZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for(int x = locX - MIN_CLAIM_RADIUS; x < locX + MIN_CLAIM_RADIUS; ++x) {
            for(int z = locZ - MIN_CLAIM_RADIUS; z < locZ + MIN_CLAIM_RADIUS; ++z) {
                final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if(!(factionAtNew instanceof RoadFaction)) {
                }
                if(!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "This position contains enemy claims within a " + MIN_CLAIM_RADIUS + " block buffer radius.");
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
	public boolean tryPurchasing(final Player player, final Claim claim) {
        Preconditions.checkNotNull((Object) claim,  "Claim is null");
        final World world = claim.getWorld();
        if(world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if(playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage(ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if(playerFaction.getClaims().size() >= 8) {
            player.sendMessage(ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        final int factionBalance = playerFaction.getBalance();
        final int claimPrice = this.calculatePrice(claim, playerFaction.getClaims().size(), false);
        if (claimPrice > factionBalance) {
            player.sendMessage(ChatColor.RED + "Your faction bank only has " + '$' + factionBalance + ", the price of this claim is " + '$' + claimPrice + '.');
            return false;
        }
        if(claim.getChunks().size() > MAX_CHUNKS_PER_LIMIT) {
            player.sendMessage(ChatColor.RED + "Claims cannot exceed " + MAX_CHUNKS_PER_LIMIT + " chunks.");
            return false;
        }
        if(claim.getWidth() < MIN_CLAIM_RADIUS || claim.getLength() < MIN_CLAIM_RADIUS) {
            player.sendMessage(ChatColor.RED + "Claims must be at least " + MIN_CLAIM_RADIUS + 'x' + MIN_CLAIM_RADIUS + " blocks.");
            return false;
        }
        final int minimumX = claim.getMinimumX();
        final int maximumX = claim.getMaximumX();
        final int minimumZ = claim.getMinimumZ();
        final int maximumZ = claim.getMaximumZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for(int x = minimumX; x < maximumX; ++x) {
            for(int z = minimumZ; z < maximumZ; ++z) {
                final Faction factionAt = factionManager.getFactionAt(world, x, z);
                if(factionAt != null && !(factionAt instanceof WildernessFaction)) {
                    player.sendMessage(ChatColor.RED + "This claim contains a location not within the " + ConfigUtil.WILDERNESS_COLOUR + factionAt.getDisplayName(player) + ChatColor.RED + '.');

                    return false;
                }
            }
        }
        for(int x = minimumX - 1; x < maximumX + 1; ++x) {
            for(int z = minimumZ - 1; z < maximumZ + 1; ++z) {
                final Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if(!(factionAtNew instanceof RoadFaction)) {
                }
                if(!playerFaction.equals(factionAtNew) && factionAtNew instanceof ClaimableFaction) {
                    player.sendMessage(ChatColor.RED + "This claim contains enemy claims within a " + 1 + " block buffer radius.");
                    return false;
                }
            }
        }
        final Collection<Claim> otherClaims = playerFaction.getClaims();
        boolean conjoined = otherClaims.isEmpty();
            if(!conjoined) {
                player.sendMessage(ChatColor.RED+"Use /f unclaim to resize your faction claims.");
                return false;
            }
        claim.setY1(MIN_CLAIM_HEIGHT);
        claim.setY2(MAX_CLAIM_HEIGHT);
        if(!playerFaction.addClaim(claim,  player)) {
            return false;
        }
        final Location center = claim.getCenter();
        player.sendMessage(ChatColor.YELLOW + "Claim has been purchased for " + ChatColor.GREEN + '$' + claimPrice + " " + playerFaction.getName() + ChatColor.YELLOW + '.');
        playerFaction.setBalance(factionBalance - claimPrice);
        playerFaction.broadcast(ChatColor.GREEN+ player.getName() + ChatColor.YELLOW + " claimed land for your faction at " + ChatColor.GRAY + '(' + center.getBlockX() + ", " + center.getBlockZ() + ')' + ChatColor.GREEN + '.', player.getUniqueId());
        return true;
    }
}
