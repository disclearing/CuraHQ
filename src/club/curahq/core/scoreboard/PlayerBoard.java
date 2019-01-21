package club.curahq.core.scoreboard;

import java.util.Collection; 
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import club.curahq.core.Core;
import club.curahq.core.classes.archer.ArcherClass;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.core.ConfigUtil;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;

@SuppressWarnings("deprecation")
public class PlayerBoard {
	private boolean sidebarVisible = false;
	private SidebarProvider defaultProvider;
	private SidebarProvider temporaryProvider;
	private BukkitRunnable runnable;
	private final AtomicBoolean removed = new AtomicBoolean(false);
	private final Team members;
	public static boolean INVISIBILITYFIX = true;
	private final Team neutrals;
	private final Team allies;
	public static boolean NAMES_ENABLED = true;
	private final Team archers;
	private final Team focused;
	private boolean removedd;
	private final BufferedObjective bufferedObjective;
	private final Scoreboard scoreboard;
	private final Player player;
	private final Core plugin;

	public PlayerBoard(Core plugin, Player player) {
		this.plugin = plugin;
		this.player = player;

		this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		this.bufferedObjective = new BufferedObjective(this.scoreboard);

		this.members = this.scoreboard.registerNewTeam("members");
		this.members.setPrefix(ConfigUtil.TEAMMATE_COLOUR.toString());
		this.members.setCanSeeFriendlyInvisibles(true);

		this.neutrals = this.scoreboard.registerNewTeam("neutrals");
		this.neutrals.setPrefix(ChatColor.YELLOW.toString());

		(this.archers = this.scoreboard.registerNewTeam("archers")).setPrefix(ChatColor.DARK_RED.toString());

		this.allies = this.scoreboard.registerNewTeam("enemies");
		this.allies.setPrefix(ConfigUtil.ALLY_COLOUR.toString());

		this.focused = this.scoreboard.registerNewTeam("focused");
		this.focused.setPrefix(ChatColor.LIGHT_PURPLE.toString());

		player.setScoreboard(this.scoreboard);
	}

	public void remove() {
		if ((!this.removed.getAndSet(true)) && (this.scoreboard != null)) {
			for (Team team : this.scoreboard.getTeams()) {
				team.unregister();
			}
			for (Objective objective : this.scoreboard.getObjectives()) {
				objective.unregister();
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public boolean isSidebarVisible() {
		return this.sidebarVisible;
	}

	public void setSidebarVisible(boolean visible) {
		this.sidebarVisible = visible;
		this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
	}

	public void setDefaultSidebar(final SidebarProvider provider, long updateInterval) {
		if (provider != this.defaultProvider) {
			this.defaultProvider = provider;
			if (this.runnable != null) {
				this.runnable.cancel();
			}
			if (provider == null) {
				this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
				return;
			}
			(this.runnable = new BukkitRunnable() {
				public void run() {
					if (removed.get()) {
						cancel();
						return;
					}
					if (provider == defaultProvider) {
						updateObjective();
					}
				}
			}).runTaskTimerAsynchronously(this.plugin, updateInterval, updateInterval);
		}
	}

	public void setTemporarySidebar(final SidebarProvider provider, long expiration) {
		if (this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}
		this.temporaryProvider = provider;
		updateObjective();
		new BukkitRunnable() {
			public void run() {
				if (removed.get()) {
					cancel();
					return;
				}
				if (temporaryProvider == provider) {
					temporaryProvider = null;
					updateObjective();
				}
			}
		}.runTaskLaterAsynchronously(this.plugin, expiration);
	}

	private void updateObjective() {
		if (this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}
		SidebarProvider provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
		if (provider == null) {
			this.bufferedObjective.setVisible(false);
		} else {
			this.bufferedObjective.setTitle(provider.getTitle().replace("%straight_line%", "�?�"));
			this.bufferedObjective.setAllLines(provider.getLines(this.player));
			this.bufferedObjective.flip();
		}
	}

	public void setFocused(Collection<Player> players) {
		if ((!NAMES_ENABLED) || (isRemoved())) {
			return;
		}
		synchronized (this.scoreboard) {
			for (Player player : players) {
				if (!checkInvis(player)) {
					this.focused.addPlayer(player);
				}
			}
		}
	}

	public boolean isRemoved() {
		return this.removedd;
	}

	public boolean checkInvis(Player player) {
		return (INVISIBILITYFIX) && (player.hasPotionEffect(PotionEffectType.INVISIBILITY));
	}

	public void removeAll(Player player) {
		synchronized (this.scoreboard) {
			this.neutrals.removePlayer(player);
			this.allies.removePlayer(player);
			this.archers.removePlayer(player);
			this.focused.removePlayer(player);
		}
		((CraftPlayer) this.player).getHandle().playerConnection
				.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) player).getHandle()));
	}
	  public void wipe(String entry)
	  {
	    synchronized (this.scoreboard)
	    {
	      this.neutrals.removeEntry(entry);
	      this.members.removeEntry(entry);
	      this.archers.removeEntry(entry);
	      this.allies.removeEntry(entry);
	      this.focused.removeEntry(entry);
	    }
	  }
	public void addUpdate(Player target) {
		addUpdates(Collections.singleton(target));
	}

	public void addUpdates(final Iterable<? extends Player> updates) {
		if (this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerFaction playerFaction = null;
				boolean hasRun = false;
				for (Player update : updates) {
					if (player.equals(update)) {

						if (members.hasPlayer(update))
							members.removePlayer(update);
						if (neutrals.hasPlayer(update))
							neutrals.removePlayer(update);
						if (allies.hasPlayer(update))
							allies.removePlayer(update);
						if (archers.hasPlayer(update))
							archers.removePlayer(update);
						if(focused.hasPlayer(update));
						   focused.removePlayer(update);

						if (ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
							archers.addPlayer(update);
						}

						members.addPlayer(update);
					} else {
						if (!hasRun) {
							playerFaction = plugin.getFactionManager().getPlayerFaction(player);
							hasRun = true;
						}

						if (ArcherClass.TAGGED.containsKey(update.getUniqueId())) {
							archers.addPlayer(update);
						} else {
							PlayerFaction targetFaction;
							targetFaction = plugin.getFactionManager().getPlayerFaction(update);
							if ((playerFaction == null) || (targetFaction == null)) {
								neutrals.addPlayer(update);
							} else {
								if (playerFaction.equals(targetFaction)) {
									members.addPlayer(update);
								} else if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
									allies.addPlayer(update);
								}else if (playerFaction.getFocus() == player.getUniqueId()) {
							            setFocused(Collections.singleton(player));
							            }
								
								else {
									neutrals.addPlayer(update);
								}

							}
						}
					}
				}
			}
		}.runTaskAsynchronously(this.plugin);
	}
}
