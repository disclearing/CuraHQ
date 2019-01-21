package club.curahq.core.game.eotw;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.listener.BorderListener;
import club.curahq.core.util.config.WorldData;
import me.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Created by TREHOME on 01/26/2016.
 */
public class EOTWHandler {
    public static final int BORDER_DECREASE_MINIMUM = 1000;
    public static final int BORDER_DECREASE_AMOUNT = 200;
    public static final long BORDER_DECREASE_TIME_MILLIS;
    public static final int BORDER_DECREASE_TIME_SECONDS;
    public static final String BORDER_DECREASE_TIME_WORDS;
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS;
    public static final long EOTW_WARMUP_WAIT_MILLIS;
    public static final int EOTW_WARMUP_WAIT_SECONDS;
    private static final long EOTW_CAPPABLE_WAIT;
    WorldData data = WorldData.getInstance();

    static {
        BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5L);
        BORDER_DECREASE_TIME_SECONDS = (int) (BORDER_DECREASE_TIME_MILLIS / 1000L);
        BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
        BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2L, true, true);
        EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(10L);
        EOTW_WARMUP_WAIT_SECONDS = (int) (EOTW_WARMUP_WAIT_MILLIS / 1000L);
        EOTW_CAPPABLE_WAIT = TimeUnit.MINUTES.toMillis(20L);
    }
    private final Core plugin;
    private EotwRunnable runnable;

    public EOTWHandler(final Core plugin) {
        this.plugin = plugin;
    }

    public EotwRunnable getRunnable() {
        return this.runnable;
    }

    public boolean isEndOfTheWorld() {
        return this.isEndOfTheWorld(true);
    }

    public void setEndOfTheWorld(final boolean yes) {
        if(yes == this.isEndOfTheWorld(false)) {
            return;
        }
        if(yes) {
            (this.runnable = new EotwRunnable(this.data.getConfig().getInt("world-NORMAL-border"))).runTaskTimer(this.plugin, 1L, 100L);
        } else if(this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
    }

    public boolean isEndOfTheWorld(final boolean ignoreWarmup) {
        return this.runnable != null && (!ignoreWarmup || this.runnable.getElapsedMilliseconds() > 0L);
    }

    public static final class EotwRunnable extends BukkitRunnable {

        private static final PotionEffect WITHER;

        static {
            WITHER = new PotionEffect(PotionEffectType.WITHER, BORDER_DECREASE_AMOUNT, 0);
        }

        private boolean hasInformedStarted;
        private long startStamp;
        private int borderSize;

        public EotwRunnable(final int borderSize) {this.hasInformedStarted = false;
            this.borderSize = borderSize;
            this.startStamp = System.currentTimeMillis() + EOTW_WARMUP_WAIT_MILLIS;
        }

        public long getTimeUntilStarting() {
            final long difference = System.currentTimeMillis() - this.startStamp;
            return (difference > 0L) ? 0L : Math.abs(difference);
        }

        public long getTimeUntilCappable() {
            return EOTW_CAPPABLE_WAIT - this.getElapsedMilliseconds();
        }

        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - this.startStamp;
        }

        @SuppressWarnings("deprecation")
		@Override
        public void run() {
            final long elapsedMillis = this.getElapsedMilliseconds();
            final int elapsedSeconds = (int) Math.round(elapsedMillis / 1000.0);
            if(!this.hasInformedStarted && elapsedSeconds >= 0) {
                for(final Faction faction : Core.getPlugin().getFactionManager().getFactions()) {
                    if(faction instanceof ClaimableFaction) {
                        final ClaimableFaction claimableFaction = (ClaimableFaction) faction;
                        for(Claim claims : claimableFaction.getClaims()) {
                            claimableFaction.removeClaim(claims, Bukkit.getConsoleSender());
                        }
                        claimableFaction.getClaims().clear();
                    }
                }
                this.hasInformedStarted = true;
                Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.RED + " has began. Border will decrease by " + BORDER_DECREASE_AMOUNT + " blocks every " + BORDER_DECREASE_TIME_WORDS + " until at " + BORDER_DECREASE_MINIMUM + " blocks.");
                return;
            }
            if(elapsedMillis < 0L && elapsedMillis >= -EOTW_WARMUP_WAIT_MILLIS) {
                Bukkit.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.DARK_AQUA + " is starting in " + Core.getRemaining(Math.abs(elapsedMillis), true, false) + '.');
                return;
            }
            for(Player on : Bukkit.getOnlinePlayers()){
                if(!BorderListener.isWithinBorder(on.getLocation())){
                    on.sendMessage(ChatColor.RED + "EndOfTheWorld is active and your outside of the border. You will get wither.");
                    on.addPotionEffect(EotwRunnable.WITHER, true);
                }
            }
            WorldData data = WorldData.getInstance();
            int world = data.getConfig().getInt("world-NORMAL-border");
            if (world <= 1000) {
            final int newBorderSize = this.borderSize - BORDER_DECREASE_AMOUNT;
            if(newBorderSize <= BORDER_DECREASE_MINIMUM) return;
            if(elapsedSeconds % BORDER_DECREASE_TIME_SECONDS == 0) {
                final int borderSize = newBorderSize;
                this.borderSize = borderSize;
                data.getConfig().set("world-NORMAL-border", borderSize);
                data.saveConfig();
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Border has been decreased to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks.");
            } else if(elapsedSeconds % (TimeUnit.MINUTES.toSeconds(5)) == 0) {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Border decreasing to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks in " + ChatColor.YELLOW + BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
            }

        }
    }
    }
}
