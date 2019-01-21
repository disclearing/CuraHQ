package club.curahq.core.faction;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import me.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface FactionManager {
    public static final long MAX_DTR_REGEN_MILLIS = TimeUnit.HOURS.toMillis(3L);
    public static final String MAX_DTR_REGEN_WORDS = DurationFormatUtils.formatDurationWords(FactionManager.MAX_DTR_REGEN_MILLIS, true, true);
    Map<String, ?> getFactionNameMap();


    Collection<Faction> getFactions();

    Collection<ClaimableFaction> getClaimableFactions();

    Collection<PlayerFaction> getPlayerFactions();

    Claim getClaimAt(Location p0);

    Claim getClaimAt(World p0, int p1, int p2);

    Faction getFactionAt(Location p0);

    Faction getFactionAt(Block p0);

    Faction getFactionAt(World p0, int p1, int p2);

    Faction getFaction(String p0);

    Faction getFaction(UUID p0);

    @Deprecated
    PlayerFaction getContainingPlayerFaction(String p0);

    @Deprecated
    PlayerFaction getPlayerFaction(Player p0);

    PlayerFaction getPlayerFaction(UUID p0);

    Faction getContainingFaction(String p0);


    boolean createFaction(Faction p0, CommandSender p1);

    boolean removeFaction(Faction p0, CommandSender p1);

    void reloadFactionData();

    void saveFactionData();
}
