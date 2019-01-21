package club.curahq.core.scoreboard.provider;
 
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.classes.PvpClass;
import club.curahq.core.classes.archer.ArcherClass;
import club.curahq.core.classes.bard.BardClass;
import club.curahq.core.classes.type.MinerClass;
import club.curahq.core.commands.staff.FreezeCommand;
import club.curahq.core.commands.staff.StaffModeCommand;
import club.curahq.core.commands.staff.VanishCommand;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.game.EventTimer;
import club.curahq.core.game.eotw.EOTWHandler;
import club.curahq.core.game.faction.ConquestFaction;
import club.curahq.core.game.faction.EventFaction;
import club.curahq.core.game.tracker.ConquestTracker;
import club.curahq.core.listener.staff.StaffChatListener;
import club.curahq.core.listener.staff.VanishListener;
import club.curahq.core.scoreboard.SidebarEntry;
import club.curahq.core.scoreboard.SidebarProvider;
import club.curahq.core.sotw.SotwCommand;
import club.curahq.core.sotw.SotwTimer;
import club.curahq.core.timer.GlobalTimer;
import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.timer.Timer;
import club.curahq.core.timer.type.SpawnTagTimer;
import club.curahq.core.timer.type.TeleportTimer;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.ConfigUtil;
import club.curahq.core.util.core.Cooldowns;
import club.curahq.core.util.core.DateTimeFormats;
import me.apache.commons.lang3.time.DurationFormatUtils;

public class TimerSidebarProvider implements SidebarProvider {

	protected static String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
	protected static final String NEW_LINE = ChatColor.STRIKETHROUGH + "----------";

	private Core plugin;
	public static String choose;



	public TimerSidebarProvider(Core plugin) {
		this.plugin = plugin;
	}

	private static String handleBardFormat(long millis, boolean trailingZero) {
		return ((DecimalFormat) (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING
				: DateTimeFormats.REMAINING_SECONDS).get()).format(millis * 0.001D);
	}
	


	public SidebarEntry add(String s) {

		if (s.length() < 10) {
			return new SidebarEntry(s);
		}

		if (s.length() > 10 && s.length() < 20) {
			return new SidebarEntry(s.substring(0, 10), s.substring(10, s.length()), "");
		}

		if (s.length() > 20) {
			return new SidebarEntry(s.substring(0, 10), s.substring(10, 20), s.substring(20, s.length()));
		}

		return null;
	}

	@Override
	public String getTitle() {
		return ConfigUtil.SCOREBOARD_TITLE.replace("%straight_line%", "�?�");

	}

	public List<SidebarEntry> getLines(Player player) {
		
		List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
		EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
		PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
		EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
		List<SidebarEntry> conquestLines = null;
		Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
		EventFaction eventFaction = eventTimer.getEventFaction();
	
		if ((ConfigUtil.KIT_MAP) == true) {
			
			lines.add(new SidebarEntry(ConfigUtil.PRIMAIRY_COLOR + ChatColor.BOLD + "Stat", "ist", "ics" + ChatColor.DARK_GRAY + ":"));
			lines.add(new SidebarEntry("§8» ", ConfigUtil.THIRD_COLOR + "Kills" + ChatColor.GRAY + ": " + ChatColor.GRAY, player.getStatistic(Statistic.PLAYER_KILLS)));
			lines.add(new SidebarEntry("§8» ", ConfigUtil.THIRD_COLOR + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GRAY, player.getStatistic(Statistic.DEATHS)));
			if(Core.getPlugin().getFactionManager().getFactionAt(player.getLocation()).getName().equals("Spawn")) {
				lines.add(new SidebarEntry("§8» ", ConfigUtil.THIRD_COLOR + "Balance" + ChatColor.GRAY + ": " +ChatColor.GRAY, "$" + Core.getPlugin().getEconomyManager().getBalance(player.getUniqueId())));
			}
			
		}

		
		if ((pvpClass != null) && ((pvpClass instanceof BardClass))) {
			BardClass bardClass = (BardClass) pvpClass;
			lines.add(new SidebarEntry(ChatColor.AQUA + ChatColor.BOLD.toString() + "Bard ", ChatColor.AQUA + ChatColor.BOLD.toString() + "Energy", ChatColor.GRAY + ": " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(player), true)));
			long remaining2 = bardClass.getRemainingBuffDelay(player);
			if (remaining2 > 0L) {
				lines.add(new SidebarEntry(ChatColor.GREEN + ChatColor.BOLD.toString() + "Bard ", ChatColor.GREEN + ChatColor.BOLD.toString() + "Effect", ChatColor.GRAY + ": " + ChatColor.RED + Core.getRemaining(remaining2, true)));
			}
		
		}
		final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
		if (sotwRunnable != null) {
				if(SotwCommand.enabled.contains(player.getUniqueId())) {
					lines.add(new SidebarEntry(String.valueOf(ChatColor.GREEN.toString()) + ChatColor.BOLD, "SOTW", ChatColor.GRAY + ": " + String.valueOf(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH) + Core.getRemaining(sotwRunnable.getRemaining(), true)));

				} else {
					lines.add(new SidebarEntry(String.valueOf(ChatColor.GREEN.toString()) + ChatColor.BOLD, "SOTW", ChatColor.GRAY + ": " + String.valueOf(ChatColor.RED.toString()) + Core.getRemaining(sotwRunnable.getRemaining(), true)));

				}
			

		
		} else if ((pvpClass instanceof MinerClass)) {
			lines.add(new SidebarEntry(ChatColor.GREEN + ChatColor.BOLD.toString(), "Active Class", ChatColor.GRAY + ": " + ChatColor.RED + "Miner"));
		} else if ((pvpClass instanceof ArcherClass)) {
			lines.add(new SidebarEntry(ChatColor.GREEN + ChatColor.BOLD.toString(), "Active Class", ChatColor.GRAY + ": " + ChatColor.RED + "Archer"));
			if (Cooldowns.isOnCooldown("Archer_item_cooldown", player)) {
				lines.add(new SidebarEntry(ChatColor.GREEN + ChatColor.BOLD.toString(), ChatColor.GREEN + ChatColor.BOLD.toString() + "Buff Delay", ChatColor.GRAY + ": " + ChatColor.RED + Core.getRemaining(Cooldowns.getCooldownForPlayerLong("Archer_item_cooldown", player), true)));
			}
		}
		
		if ((StaffModeCommand.getInstance().isMod(player))) {
			lines.add(new SidebarEntry(ChatColor.DARK_AQUA.toString(), "Visibility: " + "", VanishListener.getInstance().isVanished(player) ? ChatColor.GREEN + "Vanished" : ChatColor.RED + "Visible"));
		}
		
		for (Timer timer : timers) {
			if (((timer instanceof PlayerTimer)) && (!(timer instanceof TeleportTimer))) {
				PlayerTimer playerTimer = (PlayerTimer) timer;
				long remaining3 = playerTimer.getRemaining(player);
				if (remaining3 > 0L) {
					String timerName1 = playerTimer.getName();
					if (timerName1.length() > 14) {
						timerName1 = timerName1.substring(0, timerName1.length());
					}
					if(timer instanceof SpawnTagTimer) {
						lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName1 + ChatColor.GRAY, ": " + ChatColor.RED + Core.getCombatRemaining(remaining3, true)));

					} else {
						lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName1 + ChatColor.GRAY, ": " + ChatColor.RED + Core.getRemaining(remaining3, true)));

					}
				}
				

				
			} else if ((timer instanceof GlobalTimer)) {
				GlobalTimer playerTimer2 = (GlobalTimer) timer;
				long remaining3 = playerTimer2.getRemaining();
				if (remaining3 > 0L) {
					String timerName = playerTimer2.getName();
					if (timerName.length() > 14) {
						timerName = timerName.substring(0, timerName.length());
					}
					if (!timerName.equalsIgnoreCase("Conquest")) {
						for(Timer timer2 : timers) {
							PlayerTimer playerTimer = (PlayerTimer) timer2;
							if(playerTimer.getRemaining(player) > 0L
									|| ConfigUtil.KIT_MAP == true) {
								lines.add(new SidebarEntry("§7§m------", "------", "--------"));
								lines.add(new SidebarEntry(playerTimer2.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + Core.getRemaining(remaining3, true)));
								break;
							} else {
								lines.add(new SidebarEntry(playerTimer2.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + Core.getRemaining(remaining3, true)));
								break;
							}

						}
					}
				}
			}
		}

		if (eotwRunnable != null) {
			long remaining4 = eotwRunnable.getTimeUntilStarting();
			if (remaining4 > 0L) {
				lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.GRAY + "", "" + ChatColor.GRAY + ": " + ChatColor.RED + Core.getRemaining(remaining4, true)));
			} else if ((remaining4 = eotwRunnable.getTimeUntilCappable()) > 0L) {
				lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "EOTW" + ChatColor.GRAY + "", "" + ChatColor.GRAY + ": " + ChatColor.RED + Core.getRemaining(remaining4, true)));
			}
		}
		
		

		
		if ((eventFaction instanceof ConquestFaction)) {
			lines.add(lines.size(), new SidebarEntry(ChatColor.DARK_GRAY, ChatColor.STRIKETHROUGH + STRAIGHT_LINE, ChatColor.STRIKETHROUGH + STRAIGHT_LINE));
			ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			CONQUEST_FORMATTER.get();
			conquestLines = new ArrayList<SidebarEntry>();
			int red = (int) Math.round(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0D);
			int green = (int) Math.round(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0D);
			int blue = (int) Math.round(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0D);
			int yellow = (int) Math.round(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0D);
			int conquest = (int) Math.round(conquestFaction.getConquest().getRemainingCaptureMillis() / 1000.0D);
			conquestLines.add(new SidebarEntry(ChatColor.GOLD.toString() + ChatColor.BOLD + "Conquest",
					ChatColor.GOLD.toString() + ChatColor.BOLD + " Event", ChatColor.GRAY + ":"));
			conquestLines.add(new SidebarEntry(ChatColor.BLUE + "Conquest" + ChatColor.GRAY + ": " + conquest));
			conquestLines.add(new SidebarEntry("  " + ChatColor.RED.toString() + yellow + "s", ChatColor.RESET + ": ", String.valueOf(ChatColor.YELLOW.toString()) + green + "s"));
			conquestLines.add(new SidebarEntry("  " + ChatColor.GREEN.toString() + blue + "s", ChatColor.RESET + ": " + ChatColor.RESET, String.valueOf(ChatColor.AQUA.toString()) + red + "s"));
			ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
			int count = 0;
			for (Iterator<?> localIterator = conquestTracker.getFactionPointsMap().entrySet().iterator(); localIterator
					.hasNext(); count = 3) {
				Map.Entry<PlayerFaction, Integer> entry = (Map.Entry) localIterator.next();
				String factionName = ((PlayerFaction) entry.getKey()).getDisplayName(player);
				if (factionName.length() > 14) {
					factionName = factionName.substring(0, 14);
				}
				lines.add(new SidebarEntry(ChatColor.GOLD + " * " + ChatColor.RED, factionName, ChatColor.GRAY + ": " + ChatColor.RED + entry.getValue()));
				if (++count == 3) {
					break;
				}
			}
		}

		
		if ((conquestLines != null) && (!conquestLines.isEmpty())) {
			if(player.hasPermission("command.mod")){
				conquestLines.add(new SidebarEntry("§7§m------", "------", "--------"));
			}
			conquestLines.addAll(lines);
			lines = conquestLines;
		}
		if (!lines.isEmpty()) {
			lines.add(0, new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + "-----------", "---------"));
			lines.add(lines.size(),
					new SidebarEntry(ChatColor.GRAY, NEW_LINE, "----------"));
		}

		return lines;
		
	}

	public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
		protected DecimalFormat initialValue() {
			return new DecimalFormat("00.0");
		}
	};
	

}
