package club.curahq.core.faction;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.event.FactionClaimChangedEvent;
import club.curahq.core.faction.event.FactionCreateEvent;
import club.curahq.core.faction.event.FactionRemoveEvent;
import club.curahq.core.faction.event.FactionRenameEvent;
import club.curahq.core.faction.event.PlayerJoinedFactionEvent;
import club.curahq.core.faction.event.PlayerLeftFactionEvent;
import club.curahq.core.faction.event.cause.ClaimChangeCause;
import club.curahq.core.faction.struct.ChatChannel;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.EndPortalFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.faction.type.RoadFaction;
import club.curahq.core.faction.type.SpawnFaction;
import club.curahq.core.faction.type.WarzoneFaction;
import club.curahq.core.faction.type.WildernessFaction;
import club.curahq.core.util.Config;
import club.curahq.core.util.JavaUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.craftbukkit.v1_7_R4.util.LongHash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FlatFileFactionManager implements Listener, FactionManager {
	private final WarzoneFaction warzone;
	private final WildernessFaction wilderness;
	private final Table<String, Long, Claim> claimPositionMap = HashBasedTable.create();
	private final ConcurrentMap<UUID, UUID> factionPlayerUuidMap = new ConcurrentHashMap<UUID, UUID>();
	private final ConcurrentMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<UUID, Faction>();
	private final Map<String, UUID> factionNameMap = new CaseInsensitiveMap<UUID>();
	private Config config;
	private final Core plugin;

	public FlatFileFactionManager(Core plugin) {
		(this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
		this.warzone = new WarzoneFaction();
		this.wilderness = new WildernessFaction();
		reloadFactionData();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
		this.factionPlayerUuidMap.put(event.getUniqueID(), event.getFaction().getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
		this.factionPlayerUuidMap.remove(event.getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(FactionRenameEvent event) {
		this.factionNameMap.remove(event.getOriginalName());
		this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionClaim(FactionClaimChangedEvent event) {
		for (Claim claim : event.getAffectedClaims()) {
			cacheClaim(claim, event.getCause());
		}
	}

	@Deprecated
	public Map<String, UUID> getFactionNameMap() {
		return this.factionNameMap;
	}

	public ImmutableList<Faction> getFactions() {
		return ImmutableList.copyOf(this.factionUUIDMap.values());
	}

	public Claim getClaimAt(World world, int x, int z) {
		return (Claim) this.claimPositionMap.get(world.getName(), Long.valueOf(LongHash.toLong(x, z)));
	}

	public Claim getClaimAt(Location location) {
		return getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
	}

	public Faction getFactionAt(World world, int x, int z) {
		World.Environment environment = world.getEnvironment();

		Claim claim = getClaimAt(world, x, z);
		if (claim != null) {
			Faction faction = claim.getFaction();
			if (faction != null) {
				return faction;
			}
		}
		if (environment == World.Environment.THE_END) {
			return this.warzone;
		}
		int warzoneRadius = 140;
		if (environment == World.Environment.NETHER) {
			warzoneRadius = 200;
		}
		return (Math.abs(x) > warzoneRadius) || (Math.abs(z) > warzoneRadius) ? this.wilderness : this.warzone;
	}

	public Faction getFactionAt(Location location) {
		return getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
	}

	public Faction getFactionAt(Block block) {
		return getFactionAt(block.getLocation());
	}

	public Faction getFaction(String factionName) {
		UUID uuid = (UUID) this.factionNameMap.get(factionName);
		return uuid == null ? null : (Faction) this.factionUUIDMap.get(uuid);
	}

	public Faction getFaction(UUID factionUUID) {
		return (Faction) this.factionUUIDMap.get(factionUUID);
	}

	public PlayerFaction getPlayerFaction(UUID playerUUID) {
		UUID uuid = (UUID) this.factionPlayerUuidMap.get(playerUUID);
		Faction faction = uuid == null ? null : (Faction) this.factionUUIDMap.get(uuid);
		return (faction instanceof PlayerFaction) ? (PlayerFaction) faction : null;
	}

	public PlayerFaction getPlayerFaction(Player player) {
		return getPlayerFaction(player.getUniqueId());
	}

	@SuppressWarnings("deprecation")
	public PlayerFaction getContainingPlayerFaction(String search) {
		OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search))
				: Bukkit.getOfflinePlayer(search);
		return (target.hasPlayedBefore()) || (target.isOnline()) ? getPlayerFaction(target.getUniqueId()) : null;
	}

	@SuppressWarnings("deprecation")
	public Faction getContainingFaction(String search) {
		Faction faction = getFaction(search);
		if (faction != null) {
			return faction;
		}
		UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId();
		if (playerUUID != null) {
			return getPlayerFaction(playerUUID);
		}
		return null;
	}

	public boolean containsFaction(Faction faction) {
		return this.factionNameMap.containsKey(faction.getName());
	}

	public boolean createFaction(Faction faction) {
		return createFaction(faction, Bukkit.getConsoleSender());
	}

	public boolean createFaction(Faction faction, CommandSender sender) {
		if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
			return false;
		}
		this.factionNameMap.put(faction.getName(), faction.getUniqueID());
		if (((faction instanceof PlayerFaction)) && ((sender instanceof Player))) {
			Player player = (Player) sender;
			PlayerFaction playerFaction = (PlayerFaction) faction;
			if (!playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
				return false;
			}
		}
		FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
		Bukkit.getPluginManager().callEvent(createEvent);
		return !createEvent.isCancelled();
	}

	public boolean removeFaction(Faction faction, CommandSender sender) {
		if (!this.factionUUIDMap.containsKey(faction.getUniqueID())) {
			return false;
		}
		FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
		Bukkit.getPluginManager().callEvent(removeEvent);
		if (removeEvent.isCancelled()) {
			return false;
		}
		this.factionUUIDMap.remove(faction.getUniqueID());
		this.factionNameMap.remove(faction.getName());
		if ((faction instanceof ClaimableFaction)) {
			Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM,
					((ClaimableFaction) faction).getClaims()));
		}
		if ((faction instanceof PlayerFaction)) {
			PlayerFaction playerFaction = (PlayerFaction) faction;
			for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
				ally.getRelations().remove(faction.getUniqueID());
			}
			for (UUID uuid : playerFaction.getMembers().keySet()) {
				playerFaction.setMember(uuid, null, true);
			}
		}
		return true;
	}

	private void cacheClaim(Claim claim, ClaimChangeCause cause) {
		Preconditions.checkNotNull(claim, "Claim cannot be null");
		Preconditions.checkNotNull(cause, "Cause cannot be null");
		Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE, "Cannot cache claims of resize type");

		World world = claim.getWorld();
		if (world == null) {
			return;
		}
		int minX = Math.min(claim.getX1(), claim.getX2());
		int maxX = Math.max(claim.getX1(), claim.getX2());
		int minZ = Math.min(claim.getZ1(), claim.getZ2());
		int maxZ = Math.max(claim.getZ1(), claim.getZ2());
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				if (cause == ClaimChangeCause.CLAIM) {
					this.claimPositionMap.put(world.getName(), Long.valueOf(LongHash.toLong(x, z)), claim);
				} else if (cause == ClaimChangeCause.UNCLAIM) {
					this.claimPositionMap.remove(world.getName(), Long.valueOf(LongHash.toLong(x, z)));
				}
			}
		}
	}

	private void cacheFaction(Faction faction) {
		this.factionNameMap.put(faction.getName(), faction.getUniqueID());
		this.factionUUIDMap.put(faction.getUniqueID(), faction);
		if ((faction instanceof ClaimableFaction)) {
			for (Claim claim : ((ClaimableFaction) faction).getClaims()) {
				cacheClaim(claim, ClaimChangeCause.CLAIM);
			}
		}
		if ((faction instanceof PlayerFaction)) {
			for (FactionMember factionMember : ((PlayerFaction) faction).getMembers().values()) {
				this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
			}
		}
	}

	public void reloadFactionData() {
		this.factionNameMap.clear();
		this.config = new Config(this.plugin, "factions");

		Object object = this.config.get("factions");
		if ((object instanceof MemorySection)) {
			MemorySection section = (MemorySection) object;
			for (String factionName : section.getKeys(false)) {
				Object next = this.config.get(section.getCurrentPath() + '.' + factionName);
				if ((next instanceof Faction)) {
					cacheFaction((Faction) next);
				}
			}
		} else if ((object instanceof List)) {
			List<?> list = (List<?>) object;
			for (Object next : list) {
				if ((next instanceof Faction)) {
					cacheFaction((Faction) next);
				}
			}
		}
		Set<Faction> adding = new HashSet<Faction>();
		if (!this.factionNameMap.containsKey("NorthRoad")) {
			adding.add(new RoadFaction.NorthRoadFaction());
			adding.add(new RoadFaction.EastRoadFaction());
			adding.add(new RoadFaction.SouthRoadFaction());
			adding.add(new RoadFaction.WestRoadFaction());
		}
		if (!this.factionNameMap.containsKey("Spawn")) {
			adding.add(new SpawnFaction());
		}

		if (!this.factionNameMap.containsKey("EndPortal")) {
			adding.add(new EndPortalFaction());
		}
		for (Faction added : adding) {
			cacheFaction(added);
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.BLUE + "Faction " + added.getName() + " not found, created.");
		}
	}

	public void saveFactionData() {
		this.config.set("factions", new ArrayList<Faction>(this.factionUUIDMap.values()));
		this.config.save();
	}

	public List<ClaimableFaction> getClaimableFactions() {
		List<ClaimableFaction> factions = new ArrayList<ClaimableFaction>();
		for (Faction faction : getFactions()) {
			if ((faction instanceof ClaimableFaction)) {
				factions.add((ClaimableFaction) faction);
			}
		}
		return factions;
	}

	public Collection<PlayerFaction> getPlayerFactions() {
		List<PlayerFaction> factions = new ArrayList<PlayerFaction>();
		for (Faction faction : getFactions()) {
			if ((faction instanceof PlayerFaction)) {
				factions.add((PlayerFaction) faction);
			}
		}
		return factions;
	}
}
