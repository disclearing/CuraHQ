package club.curahq.core.game.tracker;

import com.google.common.collect.ImmutableMap; 
import com.google.common.collect.Ordering;

import club.curahq.core.Core;
import club.curahq.core.faction.event.FactionRemoveEvent;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.game.CaptureZone;
import club.curahq.core.game.EventTimer;
import club.curahq.core.game.EventType;
import club.curahq.core.game.faction.ConquestFaction;
import club.curahq.core.game.faction.EventFaction;
import club.curahq.core.game.tracker.EventTracker;
import club.curahq.core.util.core.ConfigUtil;
import compat.com.google.common.collect.GuavaCompat;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;


public class ConquestTracker
implements EventTracker,
Listener {
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;
    private final Map<PlayerFaction, Integer> factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<PlayerFaction, Integer>());
    private final Core plugin;

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30);
        POINTS_COMPARATOR = (e1, e2) -> ((Integer)e2.getValue()).compareTo((Integer)e1.getValue());
    }

    public ConquestTracker(Core ins) {
        this.plugin = ins;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            Map<PlayerFaction, Integer> map = this.factionPointsMap;
            synchronized (map) {
                this.factionPointsMap.remove(faction);
            }
        }
    }

    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return ImmutableMap.copyOf(this.factionPointsMap);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getPoints(PlayerFaction faction) {
        Map<PlayerFaction, Integer> map = this.factionPointsMap;
        synchronized (map) {
            return (Integer)GuavaCompat.firstNonNull((Object)this.factionPointsMap.get(faction), (Object)0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressWarnings("rawtypes")
	public int setPoints(PlayerFaction faction, int amount) {
        if (amount < 0) {
            return amount;
        }
        Map<PlayerFaction, Integer> map = this.factionPointsMap;
        synchronized (map) {
            this.factionPointsMap.put(faction, amount);
            List<Map.Entry<PlayerFaction, Integer>> entries = Ordering.from(POINTS_COMPARATOR).sortedCopy(this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for (Map.Entry entry : entries) {
                this.factionPointsMap.put((PlayerFaction)entry.getKey(), (Integer)entry.getValue());
            }
        }
        return amount;
    }

    public int takePoints(PlayerFaction faction, int amount) {
        return this.setPoints(faction, this.getPoints(faction) - amount);
    }

    public int addPoints(PlayerFaction faction, int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }

    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
        List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) continue;
            long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0) {
                UUID uuid = cappingPlayer.getUniqueId();
                PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    int newPoints = this.addPoints(playerFaction, 1);
                    this.plugin.getConfiguration();
					if (newPoints >= ConfigUtil.CONQUEST_REQUIRED_WIN_POINTS) {
                        Map<PlayerFaction, Integer> map = this.factionPointsMap;
                        synchronized (map) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinner(cappingPlayer);
                        return;
                    }
                	if (captureZone.getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Conquest")) {
                		int Points = this.addPoints(playerFaction, 2);
                        captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                        this.plugin.getConfiguration();
						Bukkit.broadcastMessage((String)((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.LIGHT_PURPLE + playerFaction.getName() + (Object)ChatColor.GOLD + " gained " + 3 + " point for capturing " + captureZone.getDisplayName() + (Object)ChatColor.GOLD + ". " + (Object)ChatColor.AQUA + '(' + Points + '/' + ConfigUtil.CONQUEST_REQUIRED_WIN_POINTS + ')'));	
                	} else {
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    this.plugin.getConfiguration();
					Bukkit.broadcastMessage((String)((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.LIGHT_PURPLE + playerFaction.getName() + (Object)ChatColor.GOLD + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + (Object)ChatColor.GOLD + ". " + (Object)ChatColor.AQUA + '(' + newPoints + '/' + ConfigUtil.CONQUEST_REQUIRED_WIN_POINTS + ')'));
                	}
                }
                return;
            }
            int remainingSeconds = (int)Math.round((double)remainingMillis / 1000.0);
            if (remainingSeconds % 5 != 0) continue;
            UUID uuid2 = cappingPlayer.getUniqueId();
            PlayerFaction playerFaction2 = this.plugin.getFactionManager().getPlayerFaction(uuid2);
            playerFaction2.broadcast((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.GOLD + cappingPlayer.getName() + "'s attempting to control " + (Object)ChatColor.YELLOW + captureZone.getDisplayName() + (Object)ChatColor.GOLD + ". " + (Object)ChatColor.YELLOW + '(' + remainingSeconds + "s)");
            if (playerFaction2.getName().equals(cappingPlayer.getName())) {
            	return;
            }
            cappingPlayer.sendMessage((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.GOLD + "Attempting to control " + (Object)ChatColor.YELLOW + captureZone.getDisplayName() + (Object)ChatColor.GOLD + ". " + (Object)ChatColor.YELLOW + '(' + remainingSeconds + "s)");
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage((String)((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.GOLD + eventFaction.getName() + " can now be contested."));
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage((Object)ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0 && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage((String)((Object)ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + (Object)ChatColor.GOLD + player.getName() + " was knocked off " + captureZone.getDisplayName() + (Object)ChatColor.GOLD + '.'));
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopTiming() {
        Map<PlayerFaction, Integer> map = this.factionPointsMap;
        synchronized (map) {
            this.factionPointsMap.clear();
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        EventFaction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            Player player = event.getEntity();
            PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null) {
                int oldPoints = this.getPoints(playerFaction);
                if (oldPoints == 0) {
                    return;
                }
                if (this.getPoints(playerFaction) <= 20) {
                    this.setPoints(playerFaction, 0);
                } else {
                    this.takePoints(playerFaction, 20);
                }
                Bukkit.broadcastMessage((String)String.valueOf(this.getPoints(playerFaction)));
                event.setDeathMessage((Object)ChatColor.YELLOW + "[" + currentEventFac.getName() + "] " + (Object)ChatColor.LIGHT_PURPLE + playerFaction.getName() + (Object)ChatColor.GOLD + " lost " + (Object)ChatColor.BOLD + Math.min(20, oldPoints) + (Object)ChatColor.GOLD + " points because " + player.getName() + " died." + (Object)ChatColor.AQUA + " (" + this.getPoints(playerFaction) + '/' + 300 + ')' + (Object)ChatColor.YELLOW + '.');
            }
        }
    }
}