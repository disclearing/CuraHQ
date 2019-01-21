package club.curahq.core.game.tracker;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.game.CaptureZone;
import club.curahq.core.game.EventTimer;
import club.curahq.core.game.EventType;
import club.curahq.core.game.faction.EventFaction;
import club.curahq.core.game.faction.KothFaction;
import club.curahq.core.util.core.DateTimeFormats;

public class KothTracker
  implements EventTracker
{
  private final Core plugin;
  
  public KothTracker(Core plugin)
  {
    this.plugin = plugin;
  }
  
  public EventType getEventType()
  {
    return EventType.KOTH;
  }
  
  public void tick(EventTimer eventTimer, EventFaction eventFaction)
  {
    CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
    long remainingMillis = captureZone.getRemainingCaptureMillis();
    if (remainingMillis <= 0L)
    {
      this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
      eventTimer.clearCooldown();
      return;
    }
    if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
      return;
    }
    int remainingSeconds = (int)(remainingMillis / 1000L);
    if ((remainingSeconds > 0) && (remainingSeconds % 30 == 0)) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
    }
  }
  
  public void onContest(EventFaction eventFaction, EventTimer eventTimer)
  {
	Bukkit.broadcastMessage("§7§m---------------------------------");
	Bukkit.broadcastMessage("§7\u2588§e\u2588\u2588\u2588\u2588\u2588\u2588\u2588§7\u2588");
	Bukkit.broadcastMessage("§e\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
	Bukkit.broadcastMessage("§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588");
	Bukkit.broadcastMessage("§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588§6\u2588§e\u2588");
	Bukkit.broadcastMessage("§e\u2588§6\u2588\u2588\u2588\u2588\u2588\u2588\u2588§e\u2588       §a§l" + eventFaction.getName());
	Bukkit.broadcastMessage("§e\u2588§6\u2588§b\u2588§6\u2588§b\u2588§6\u2588§b\u2588§6\u2588§e\u2588 §7has been started. §a"  + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()));
	Bukkit.broadcastMessage("§e\u2588§6\u2588\u2588\u2588\u2588\u2588\u2588\u2588§e\u2588");
	Bukkit.broadcastMessage("§e\u2588\u2588\u2588§7\u2588\u2588\u2588§e\u2588\u2588\u2588");
	Bukkit.broadcastMessage("§e\u2588\u2588\u2588\u2588§7\u2588§e\u2588\u2588\u2588\u2588");
	Bukkit.broadcastMessage("§7\u2588§e\u2588\u2588\u2588§7\u2588§e\u2588\u2588\u2588§7\u2588");
	Bukkit.broadcastMessage("§7§m---------------------------------");
    //Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');

  }
  
  public boolean onControlTake(Player player, CaptureZone captureZone)
  {
    player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
    return true;
  }
  
  public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction)
  {
    player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
    long remainingMillis = captureZone.getRemainingCaptureMillis();
    if ((remainingMillis > 0L) && (captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE)) {
      Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
    }
    return true;
  }
  
  private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
  public static final long DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
  
  public void stopTiming() {}
}
