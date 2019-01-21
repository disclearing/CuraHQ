package club.curahq.core.timer.event;

import com.google.common.base.Optional;

import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.timer.Timer;

import java.util.UUID;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerPauseEvent
  extends Event
  implements Cancellable
{
  public TimerPauseEvent(Timer timer, boolean paused)
  {
    this.userUUID = Optional.absent();
    this.timer = timer;
    this.paused = paused;
  }
  
  public TimerPauseEvent(UUID userUUID, PlayerTimer timer, boolean paused)
  {
    this.userUUID = Optional.fromNullable(userUUID);
    this.timer = timer;
    this.paused = paused;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Optional<UUID> getUserUUID()
  {
    return this.userUUID;
  }
  
  public Timer getTimer()
  {
    return this.timer;
  }
  
  public boolean isPaused()
  {
    return this.paused;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled)
  {
    this.cancelled = cancelled;
  }
  
  private static final HandlerList handlers = new HandlerList();
  private final boolean paused;
  private final Optional<UUID> userUUID;
  private final Timer timer;
  private boolean cancelled;
}
