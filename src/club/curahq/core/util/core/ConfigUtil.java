package club.curahq.core.util.core;

import com.google.common.collect.*;

import club.curahq.core.Core;
import me.apache.commons.lang3.time.DurationFormatUtils;

import java.util.*;
import java.util.concurrent.*;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.enchantments.*;
import org.bukkit.potion.*;
@SuppressWarnings("static-access")
public final class ConfigUtil {

	public static final String INVITE_PREFIX = "§6§lINVITE §8» ";
	public static final String FACTION_PREFIX = "§6§lFACTION §8» ";
	public static final String PREFIX = "§6§lHCF §8» ";
	
	public static final String PRIMAIRY_COLOR = Core.getPlugin().getConfig().getString("PRIMAIRY-COLOR").replace("&", "§");
	public static final String SECONDAIRY_COLOR = Core.getPlugin().getConfig().getString("SECONDAIRY-COLOR").replace("&", "§");
	public static final String THIRD_COLOR = Core.getPlugin().getConfig().getString("THIRD-COLOR").replace("&", "§");
	public static final String SERVER_NAME = Core.getPlugin().getConfig().getString("SERVER-NAME").replace("&", "§");
	
	public static final TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
	public static final int WARZONE_RADIUS = Core.getPlugin().getConfig().getInt("WARZONE-RADIUS");
	public static final String NAME = "Example";
	public static final String TEAMSPEAK_URL = Core.config.getString("teamspeak-frozen");
	public static final String BLOCKED_POTIONS = Core.config.getString("blocked-potions");
	public static final String DONATE_URL = "store.example.us";
	public static final String SUBREDDIT_URL = "Development";
	public static final int SPAWN_BUFFER = 300;
	public static final double MAP_NUMBER = 5.0D;
	public static final boolean KIT_MAP = Core.getPlugin().config.getBoolean("Kit-Map");
	public static final List<String> DISALLOWED_FACTION_NAMES = ImmutableList.of("shit", "niggar", "nigger", "shit",
			"kohieotw", "kohisotw", "hcteams", "hcteamseotw", "hcteamssotw");
	public static final Map<Enchantment, Integer> ENCHANTMENT_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<Enchantment, Integer> KOTH_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<Enchantment, Integer> TIER1_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<Enchantment, Integer> TIER3_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<Enchantment, Integer> TIER2_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<PotionType, Integer> POTION_LIMITS = new EnumMap<PotionType, Integer>(PotionType.class);
	public static final Map<World.Environment, Double> SPAWN_RADIUS_MAP = new EnumMap<Environment, Double>(
			World.Environment.class);
	public static double EXP_MULTIPLIER_GENERAL = 2.0D;
	public static double EXP_MULTIPLIER_FISHING = 2.0D;
	public static double EXP_MULTIPLIER_SMELTING = 2.0D;
	public static double EXP_MULTIPLIER_LOOTING_PER_LEVEL = 1.5D;
	public static double EXP_MULTIPLIER_LUCK_PER_LEVEL = 1.5D;
	public static double EXP_MULTIPLIER_FORTUNE_PER_LEVEL = 1.5D;
	private boolean handleCombatLogging = true;
	
	public static final String STORE = Core.getPlugin().config.getString("store");
	public static final String TOP_RANK = Core.getPlugin().config.getString("top-rank");

	public static final String ARCHER_TAG = Core.getPlugin().config.getString("Timers.archer");
	public static final String ENDERPEARL_TIMER = Core.getPlugin().config.getString("Timers.enderpearl");
	public static final String HOME_TIMER = Core.getPlugin().config.getString("Timers.home");
	public static final String LOGOUT_TIMER = Core.getPlugin().config.getString("Timers.logout");
	public static final String NOTCH_TIMER = Core.getPlugin().config.getString("Timers.notch");
	public static final String CLASS_WARMUP = Core.getPlugin().config.getString("Timers.warmup");
	public static final String PVP_TIMER = Core.getPlugin().config.getString("Timers.protection");
	public static final String SPAWN_TAG = Core.getPlugin().config.getString("Timers.spawntag");
	public static final String STUCK_TIMER = Core.getPlugin().config.getString("Timers.stuck");
	public static final String TELEPORT_TIMER = Core.getPlugin().config.getString("Timers.teleport");

	public static final boolean isBuildingInCombat = Core.getPlugin().config.getBoolean("Combat_building");

	public boolean isHandleCombatLogging() {
		return this.handleCombatLogging;
	}

	static {

		ENCHANTMENT_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL,
				Integer.valueOf(Core.config.getInt("Kitmap.Protection")));
		KOTH_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(2));
		TIER1_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(0));
		TIER2_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(1));
		TIER3_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.valueOf(2));

		ENCHANTMENT_LIMITS.put(Enchantment.DAMAGE_ALL, Integer.valueOf(Core.config.getInt("Kitmap.Sharpness")));
		ENCHANTMENT_LIMITS.put(Enchantment.ARROW_KNOCKBACK, Integer.valueOf(0));
		ENCHANTMENT_LIMITS.put(Enchantment.KNOCKBACK, Integer.valueOf(0));
		ENCHANTMENT_LIMITS.put(Enchantment.FIRE_ASPECT, Integer.valueOf(0));
		ENCHANTMENT_LIMITS.put(Enchantment.THORNS, Integer.valueOf(0));
		ENCHANTMENT_LIMITS.put(Enchantment.ARROW_FIRE, Integer.valueOf(1));
		ENCHANTMENT_LIMITS.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(3));

		SPAWN_RADIUS_MAP.put(World.Environment.NORMAL, Double.valueOf(0.0D));
		SPAWN_RADIUS_MAP.put(World.Environment.NETHER, Double.valueOf(0.0D));
		SPAWN_RADIUS_MAP.put(World.Environment.THE_END, Double.valueOf(0.0D));
		DEFAULT_DEATHBAN_DURATION = TimeUnit.HOURS.toMillis(3L);
	}

	public static final ChatColor TEAMMATE_COLOUR = ChatColor.GREEN;
	public static final ChatColor ALLY_COLOUR = ChatColor.AQUA;
	public static final ChatColor ENEMY_COLOUR = ChatColor.RED;
	public static final ChatColor SAFEZONE_COLOUR = ChatColor.GREEN;
	public static final ChatColor ROAD_COLOUR = ChatColor.GOLD;
	public static final ChatColor WARZONE_COLOUR = ChatColor.DARK_RED;
	public static final ChatColor WILDERNESS_COLOUR = ChatColor.DARK_GREEN;
	public static final ChatColor FOCUS_COLOUR = ChatColor.LIGHT_PURPLE;
	public static ChatColor WHITE = ChatColor.WHITE;
	public static ChatColor GREEN = ChatColor.GREEN;
	public static ChatColor RED = ChatColor.RED;
	public static ChatColor GOLD = ChatColor.GOLD;
	public static ChatColor YELLOW = ChatColor.YELLOW;
	public static ChatColor GRAY = ChatColor.GRAY;
	public static final String SCOREBOARD_TITLE = ChatColor.translateAlternateColorCodes('&',
			Core.config.getString("Scoreboard_title").replace("%straight_line%", "❘"));

	public static final int MAX_ALLIES_PER_FACTION = Core.config.getInt("MAX-ALLY");
	public static final int MAX_PLAYER_PER_FACTION = Core.config.getInt("MAX-FACTION");
	public static final int CONQUEST_REQUIRED_WIN_POINTS = 150;

	public static final long DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);
	public static final String DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils
			.formatDurationWords(DTR_MILLIS_BETWEEN_UPDATES, true, true);
	public static long DEFAULT_DEATHBAN_DURATION = TimeUnit.HOURS
			.toMillis(Core.getPlugin().config.getLong("DEATHBAN-MAXTIME"));
	public static boolean CRATE_BROADCASTS = false;

	private int combatlogDespawnDelayTicks = 600;

	public int getCombatlogDespawnDelayTicks() {
		return this.combatlogDespawnDelayTicks;
	}
}
