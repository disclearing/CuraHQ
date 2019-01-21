package club.curahq.core.faction.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.curahq.core.Core;
import club.curahq.core.deathban.Deathban;
import club.curahq.core.faction.FactionMember;
import club.curahq.core.faction.event.FactionDtrChangeEvent;
import club.curahq.core.faction.event.PlayerJoinedFactionEvent;
import club.curahq.core.faction.event.PlayerLeaveFactionEvent;
import club.curahq.core.faction.event.PlayerLeftFactionEvent;
import club.curahq.core.faction.event.cause.FactionLeaveCause;
import club.curahq.core.faction.struct.Raidable;
import club.curahq.core.faction.struct.RegenStatus;
import club.curahq.core.faction.struct.Relation;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.timer.type.TeleportTimer;
import club.curahq.core.user.FactionUser;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.GenericUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.PersistableLocation;
import club.curahq.core.util.core.ConfigUtil;
import me.apache.commons.lang3.StringUtils;
import me.apache.commons.lang3.time.DurationFormatUtils;

public class PlayerFaction extends ClaimableFaction implements Raidable {
    private static final UUID[] EMPTY_UUID_ARRAY;

    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }

    @SuppressWarnings("rawtypes")
	protected final Map requestedRelations = new HashMap();
    @SuppressWarnings("rawtypes")
	protected final Map relations = new HashMap();
    @SuppressWarnings("rawtypes")
	protected final Map members = new HashMap();
    protected final Set<String> invitedPlayerNames;
    protected PersistableLocation home;
    protected String announcement;
    protected UUID focus;
    protected boolean open;
    protected int balance;
    protected double deathsUntilRaidable;
    protected long regenCooldownTimestamp;
    private long lastDtrUpdateTimestamp;


    @SuppressWarnings({ "unchecked", "rawtypes" })
	public PlayerFaction(final String name) {
        super(name);
        this.invitedPlayerNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0D;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public PlayerFaction(Map map) {
        super(map);
        this.invitedPlayerNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0D;
        Iterator object = GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet().iterator();

        while(object.hasNext()) {
            Map.Entry entry = (Map.Entry) object.next();
            members.put(UUID.fromString((String) entry.getKey()), entry.getValue());
        }

        this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));
        Object object1 = map.get("home");
        if(object1 != null) {
            this.home = (PersistableLocation) object1;
        }

        object1 = map.get("announcement");
        if(object1 != null) {
            this.announcement = (String) object1;
        }

        Iterator entry2 = GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet().iterator();

        Map.Entry entry1;
        while(entry2.hasNext()) {
            entry1 = (Map.Entry) entry2.next();
            this.relations.put(UUID.fromString((String) entry1.getKey()), Relation.valueOf((String) entry1.getValue()));
        }

        entry2 = GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet().iterator();

        while(entry2.hasNext()) {
            entry1 = (Map.Entry) entry2.next();
            this.requestedRelations.put(UUID.fromString((String) entry1.getKey()), Relation.valueOf((String) entry1.getValue()));
        }

        this.open = ((Boolean) map.get("open")).booleanValue();
        this.balance = ((Integer) map.get("balance")).intValue();
        this.deathsUntilRaidable = ((Double) map.get("deathsUntilRaidable")).doubleValue();
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map serialize() {
        Map map = super.serialize();
        HashMap relationSaveMap = new HashMap(this.relations.size());
        Iterator requestedRelationsSaveMap = this.relations.entrySet().iterator();

        while(requestedRelationsSaveMap.hasNext()) {
            Map.Entry entrySet = (Map.Entry) requestedRelationsSaveMap.next();
            relationSaveMap.put(((UUID) entrySet.getKey()).toString(), ((Relation) entrySet.getValue()).name());
        }

        map.put("relations", relationSaveMap);
        HashMap requestedRelationsSaveMap1 = new HashMap(this.requestedRelations.size());
        Iterator entrySet1 = this.requestedRelations.entrySet().iterator();

        while(entrySet1.hasNext()) {
            Map.Entry saveMap = (Map.Entry) entrySet1.next();
            requestedRelationsSaveMap1.put(( saveMap.getKey()).toString(), ((Relation) saveMap.getValue()).name());
        }

        map.put("requestedRelations", requestedRelationsSaveMap1);
        Set entrySet2 = this.members.entrySet();
        LinkedHashMap saveMap1 = new LinkedHashMap(this.members.size());
        Iterator var6 = entrySet2.iterator();

        while(var6.hasNext()) {
            Map.Entry entry = (Map.Entry) var6.next();
            saveMap1.put(( entry.getKey()).toString(), entry.getValue());
        }

        map.put("members", saveMap1);
        map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));
        if(this.home != null) {
            map.put("home", this.home);
        }

        if(this.announcement != null) {
            map.put("announcement", this.announcement);
        }

        map.put("open", Boolean.valueOf(this.open));
        map.put("balance", Integer.valueOf(this.balance));
        map.put("deathsUntilRaidable", Double.valueOf(this.deathsUntilRaidable));
        map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
        return map;
    }

    public boolean setMember(final UUID playerUUID, final FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public UUID getFocus()
    {
      return this.focus;
    }
    
    public void setFocus(UUID uuid)
    {
      this.focus = uuid;
    }
   
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }

    public boolean setMember(final Player player, final FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }

    public boolean setMember(final Player player, final FactionMember factionMember, final boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }

    @SuppressWarnings("unchecked")
	private boolean setMember( final Player player, final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        if(factionMember == null) {
            if(!force) {
                final PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent( event);
                if(event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            final PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent( event2);
            return true;
        }
        final PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent( eventPre);
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }

    @SuppressWarnings("unchecked")
	public Collection<UUID> getAllied() {
        return Maps.filterValues(this.relations, new Predicate<Relation>(){

            public boolean apply(Relation relation) {
                if (relation != Relation.ALLY) return false;
                return true;
            }
        }).keySet();
    }

    public List<PlayerFaction> getAlliedFactions() {
        final Collection<UUID> allied = this.getAllied();
        final Iterator<UUID> iterator = allied.iterator();
        final List<PlayerFaction> results = new ArrayList<PlayerFaction>(allied.size());
        while(iterator.hasNext()) {
            final Faction faction = Core.getPlugin().getFactionManager().getFaction(iterator.next());
            if(faction instanceof PlayerFaction) {
                results.add((PlayerFaction) faction);
            } else {
                iterator.remove();
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
	public Map<UUID, Relation> getRequestedRelations() {
        return this.requestedRelations;
    }

    @SuppressWarnings("unchecked")
	public Map<UUID, Relation> getRelations() {
        return this.relations;
    }

    @SuppressWarnings("unchecked")
	public Map<UUID, FactionMember> getMembers() {
        return ImmutableMap.copyOf(members);
    }

    public Set<Player> getOnlinePlayers() {
        return this.getOnlinePlayers(null);
    }

    public Set<Player> getOnlinePlayers(final CommandSender sender) {
        final Set<Map.Entry<UUID, FactionMember>> entrySet = this.getOnlineMembers(sender).entrySet();
        final Set<Player> results = new HashSet<Player>(entrySet.size());
        for (final Map.Entry<UUID, FactionMember> entry : entrySet) {
            results.add(Bukkit.getPlayer((UUID)entry.getKey()));
        }
        return results;
    }

    @SuppressWarnings("rawtypes")
	public Map getOnlineMembers() {
        return this.getOnlineMembers(null);
    }

    @SuppressWarnings("unchecked")
	public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender) {
        Player senderPlayer = sender instanceof Player ? (Player)sender : null;
        HashMap<UUID, FactionMember> results = new HashMap<UUID, FactionMember>();
        Iterator<Map.Entry<UUID, FactionMember>> iterator = this.members.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FactionMember> entry = iterator.next();
            Player target = Bukkit.getPlayer((UUID)entry.getKey());
            if (target == null) continue;
            if (senderPlayer != null && !senderPlayer.canSee(target)) continue;
            results.put(entry.getKey(), entry.getValue());
        }
        return results;
    }


    @SuppressWarnings("unchecked")
	public FactionMember getLeader() {
        Map.Entry<UUID, FactionMember> entry;
        Map<UUID, FactionMember> members = this.members;
        Iterator<Map.Entry<UUID, FactionMember>> iterator = members.entrySet().iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while ((entry = iterator.next()).getValue().getRole() != Role.LEADER);
        return entry.getValue();
    }

    @Deprecated
    public FactionMember getMember(String memberName) {
        UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        FactionMember factionMember = (FactionMember) this.members.get(uuid);
        return factionMember;
    }

    public FactionMember getMember(Player player) {
        return this.getMember(player.getUniqueId());
    }

    public FactionMember getMember(UUID memberUUID) {
        return (FactionMember) this.members.get(memberUUID);
    }

    public Set<String> getInvitedPlayerNames() {
        return this.invitedPlayerNames;
    }

    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }

    @SuppressWarnings("rawtypes")
	public void setHome( Location home) {
        if(home == null && this.home != null) {
            TeleportTimer timer = Core.getPlugin().getTimerManager().teleportTimer;
            Iterator var3 = this.getOnlinePlayers().iterator();

            while(var3.hasNext()) {
                Player player = (Player) var3.next();
                Location destination = (Location) timer.getDestination(player);
                if(Objects.equal(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getDisplayName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }

        this.home = home == null ? null : new PersistableLocation(home);
    }

    public String getAnnouncement() {
        return this.announcement;
    }

    public void setAnnouncement(@Nullable final String announcement) {
        this.announcement = announcement;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(final boolean open) {
        this.open = open;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(final int balance) {
        this.balance = balance;
    }

    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }

    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }

    @Override
    public double getMaximumDeathsUntilRaidable() {
        if(this.members.size() == 1) {
            return 1.1;
        }
        return Math.min(5.5, this.members.size() * 0.9);
    }


    public double getDeathsUntilRaidable(final boolean updateLastCheck) {
        if(updateLastCheck) {
            this.updateDeathsUntilRaidable();
        }
        return this.deathsUntilRaidable;
    }

    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();
        if(this.deathsUntilRaidable < 0.0) {
            return ChatColor.RED;
        }
        if(this.deathsUntilRaidable < 1.0) {
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }

    private void updateDeathsUntilRaidable() {
        if(this.getRegenStatus() == RegenStatus.REGENERATING) {
            final long now = System.currentTimeMillis();
            final long millisPassed = now - this.lastDtrUpdateTimestamp;
            if(millisPassed >= ConfigUtil.DTR_MILLIS_BETWEEN_UPDATES) {
                final long remainder = millisPassed % ConfigUtil.DTR_MILLIS_BETWEEN_UPDATES;
                final int multiplier = (int) ((millisPassed + remainder) / ConfigUtil.DTR_MILLIS_BETWEEN_UPDATES);
                final double increase = multiplier * 0.1;
                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }

    @Override
    public double setDeathsUntilRaidable(final double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }

    private double setDeathsUntilRaidable(double deathsUntilRaidable, final boolean limit) {

        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if(limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if(deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            final FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if(deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    Core.getPlugin().getLogger().info("Faction " + this.getName() + " is now raidable.");
                }
                this.lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }

    protected long getRegenCooldownTimestamp() {
        return this.regenCooldownTimestamp;
    }

    @Override
    public long getRemainingRegenerationTime() {
        return (this.regenCooldownTimestamp == 0L) ? 0L : (this.regenCooldownTimestamp - System.currentTimeMillis());
    }

    @Override
    public void setRemainingRegenerationTime(final long millis) {
        final long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;
        this.lastDtrUpdateTimestamp = systemMillis + ConfigUtil.DTR_MILLIS_BETWEEN_UPDATES * 2L;
    }

    @Override
    public RegenStatus getRegenStatus() {
        if(this.getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        }
        if(this.getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        }
        return RegenStatus.FULL;
    }


    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public void printDetails(CommandSender sender) {
        String	leaderName = null;
        HashSet<String> coleader = new HashSet();
        HashSet allyNames = new HashSet(1);
        Iterator combinedKills = this.relations.entrySet().iterator();
        int combinedKills1 = 0;
        PlayerFaction playerFaction;
        while(combinedKills.hasNext()) {
            Map.Entry memberNames = (Map.Entry) combinedKills.next();
            Faction captainNames = Core.getPlugin().getFactionManager().getFaction((UUID) memberNames.getKey());
            if(captainNames instanceof PlayerFaction) {
                playerFaction = (PlayerFaction) captainNames;
                allyNames.add(playerFaction.getDisplayName(sender) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
            }
        }

        HashSet memberNames = new HashSet();
        HashSet<String> captainNames = new HashSet();
        Iterator playerFaction1 = this.members.entrySet().iterator();
        while(playerFaction1.hasNext()) {
            final Map.Entry entry = (Map.Entry) playerFaction1.next();
            final FactionMember factionMember = (FactionMember) entry.getValue();
            final Player target = factionMember.toOnlinePlayer();
            final FactionUser user = Core.getPlugin().getUserManager().getUser((UUID) entry.getKey());
            int kills = user.getKills();
            combinedKills1 += kills;
            final Deathban deathban = user.getDeathban();
            ChatColor colour;
            if (deathban != null && deathban.isActive()) {
                colour = ChatColor.RED;
            }
            else if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
                colour = ChatColor.GRAY;
            }
            else {
                colour = ChatColor.GREEN;
            }
            final String memberName =  colour + factionMember.getName() +  ChatColor.GRAY + '[' +  ChatColor.GREEN + kills +  ChatColor.GRAY + ']';
            switch (factionMember.getRole()) {
                case LEADER: {
                    leaderName = memberName;
                    continue;
                }
                case COLEADER: {
                    coleader.add(memberName);
                    continue;
                }
                case CAPTAIN: {
                    captainNames.add(memberName);
                    continue;
                }
                case MEMBER: {
                    memberNames.add(memberName);
                    continue;
                }
            }
        }

        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(" " + this.getDisplayName(sender) + ChatColor.GRAY+" (" + this.getOnlineMembers().size() + "/" + this.getMembers().size()+") "+ ChatColor.GREEN + "- " + ChatColor.YELLOW + "Home " + ChatColor.GOLD + " » " + ChatColor.GRAY + (this.home == null ? "Not set" : ChatColor.GRAY.toString() + this.home.getLocation().getBlockX() + " | " + this.home.getLocation().getBlockY() + " | " + ChatColor.GRAY.toString() + this.home.getLocation().getBlockZ()));


        if(!allyNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + " Allies" + ChatColor.GOLD + " » " + StringUtils.join(allyNames, ChatColor.GRAY + ", "));
        }

        if(leaderName != null) {
            sender.sendMessage(ChatColor.YELLOW + " Leader" + ChatColor.GOLD + " » " + ChatColor.RED + leaderName);
        }
        
        if (!coleader.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + " Co-Leaders" + ChatColor.GOLD + " » " + ChatColor.RED + StringUtils.join(coleader, new StringBuilder().append(ChatColor.GRAY).append(", ").toString()));
          }

        if(!captainNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + " Captains" + ChatColor.GOLD + " » " + ChatColor.RED + StringUtils.join(captainNames, ChatColor.GRAY + ", "));
        }

        if(!memberNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + " Members" + ChatColor.GOLD + " » " + ChatColor.RED + StringUtils.join(memberNames, ChatColor.GRAY + ", "));
        }
        
        sender.sendMessage(ChatColor.YELLOW + " Balance" + ChatColor.GOLD + " » "  + ChatColor.GREEN + '$' + this.balance);
        
        sender.sendMessage(ChatColor.YELLOW + " Total Kills" + ChatColor.GOLD + " » " + ChatColor.GREEN + combinedKills1);
        String stringDtr = ConfigUtil.KIT_MAP ? "§cDisabled" : this.getRegenStatus().getSymbol() + this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false));
        sender.sendMessage(ChatColor.YELLOW + " DTR" + ChatColor.GOLD + " » "  + stringDtr);

        long dtrRegenRemaining = this.getRemainingRegenerationTime();
        if(dtrRegenRemaining > 0L) {
            sender.sendMessage(ChatColor.YELLOW+ " Time until Regen" + ChatColor.GOLD + " » " + ChatColor.GREEN + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
        }
        if (sender instanceof Player) {
            final Faction playerFaction2 = Core.getPlugin().getFactionManager().getPlayerFaction((Player)sender);
            if (playerFaction2 != null && playerFaction2.equals(this) && this.announcement != null) {
                sender.sendMessage(ChatColor.YELLOW + " Announcement" + ChatColor.GOLD + " » " + ChatColor.GREEN + this.announcement);
            }
        }

        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    

    public void broadcast(final String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }

    public void broadcast(final String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }

    public void broadcast(final String message, @Nullable final UUID... ignore) {
        this.broadcast(new String[]{message}, ignore);
    }

	public void broadcast(final String[] messages, final UUID... ignore) {
        Preconditions.checkNotNull((Object) messages, (Object) "Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, (Object) "Message array cannot be empty");
        final Collection<Player> players = this.getOnlinePlayers();
        final Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        for(final Player player : players) {
            if(!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }

}
