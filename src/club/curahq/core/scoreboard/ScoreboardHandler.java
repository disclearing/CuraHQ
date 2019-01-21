package club.curahq.core.scoreboard;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import club.curahq.core.Core;
import club.curahq.core.faction.event.FactionFocusChangeEvent;
import club.curahq.core.faction.event.FactionRelationCreateEvent;
import club.curahq.core.faction.event.FactionRelationRemoveEvent;
import club.curahq.core.faction.event.PlayerJoinedFactionEvent;
import club.curahq.core.faction.event.PlayerLeftFactionEvent;
import club.curahq.core.scoreboard.provider.TimerSidebarProvider;
 
public class ScoreboardHandler implements Listener {
 
    private static final long UPDATE_TICK_INTERVAL = 2L;
 
    private final Map<UUID, PlayerBoard> playerBoards = new HashMap<>();
    private final TimerSidebarProvider timerSidebarProvider;
    private final Core plugin;
 
    @SuppressWarnings("deprecation")
	public ScoreboardHandler(Core plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);
 
        // Give all online players a scoreboard.
        List<Player> onlinePlayers = new ArrayList<Player>();
        Player[] arrayOfPlayer;
        int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
        for (int i = 0; i < j; i++)
        {
          Player players = arrayOfPlayer[i];
          PlayerBoard playerBoard;
          setPlayerBoard(players.getUniqueId(), playerBoard = new PlayerBoard(plugin, players));
          onlinePlayers.add(players);
          playerBoard.addUpdates(onlinePlayers);
        }
      }
 
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player); // Update this player for every other online player.
        }
        List<Player> onlinePlayers = new ArrayList<Player>();
        Player[] arrayOfPlayer;
        int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
        for (int i = 0; i < j; i++)
        {
          Player p = arrayOfPlayer[i];
          onlinePlayers.add(p);
        }
        PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        board2.addUpdates(onlinePlayers);
        setPlayerBoard(player.getUniqueId(), board2);
    }
    
    @EventHandler
    public void onFocus(FactionFocusChangeEvent e){
        final HashSet<Player> updates = new HashSet<>(e.getSenderFaction().getOnlinePlayers());
        if(e.getPlayer() != null) {
            updates.add(e.getPlayer());
        }
        if(e.getOldFocus() != null && Bukkit.getPlayer(e.getOldFocus()) != null) {
            updates.add(Bukkit.getPlayer(e.getOldFocus()));

        }
        for(final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }

    }

 
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }
 
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();
 
            @SuppressWarnings("unchecked")
			Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for (Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }
 
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            Player player = optional.get();
 
            Collection<Player> players = event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for (Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }
 
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(FactionRelationCreateEvent event) {
        Iterable<Player> updates = Iterables.concat(event.getSenderFaction().getOnlinePlayers(), event.getTargetFaction().getOnlinePlayers());
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }
 
    @SuppressWarnings("unchecked")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(FactionRelationRemoveEvent event) {
        Iterable<Player> updates = Iterables.concat(event.getSenderFaction().getOnlinePlayers(), event.getTargetFaction().getOnlinePlayers());
        for (PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }
 
    public PlayerBoard getPlayerBoard(UUID uuid) {
        return this.playerBoards.get(uuid);
    }
 
    public PlayerBoard applyBoard(Player player) {
        PlayerBoard board = new PlayerBoard(plugin, player);
        PlayerBoard previous = this.playerBoards.put(player.getUniqueId(), board);
        if (previous != null && previous != board) {
            previous.remove();
        }
 
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, UPDATE_TICK_INTERVAL);
        return board;
    }
 
    public void clearBoards() {
        Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
    
    public void setPlayerBoard(UUID uuid, PlayerBoard board)
    {
      this.playerBoards.put(uuid, board);
      board.setSidebarVisible(true);
      board.setDefaultSidebar(this.timerSidebarProvider, 2L);
    }
}