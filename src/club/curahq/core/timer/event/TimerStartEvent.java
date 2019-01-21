package club.curahq.core.timer.event;

import com.google.common.base.Optional;

import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.timer.Timer;

import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerStartEvent
  extends Event
{
  public TimerStartEvent(Timer timer, long duration)
  {
    this.player = Optional.absent();
    this.userUUID = Optional.absent();
    this.timer = timer;
    this.duration = duration;
  }
  
  public TimerStartEvent(@Nullable Player player, UUID uniqueId, PlayerTimer timer, long duration)
  {
    this.player = Optional.fromNullable(player);
    this.userUUID = Optional.fromNullable(uniqueId);
    this.timer = timer;
    this.duration = duration;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Optional<Player> getPlayer()
  {
    return this.player;
  }
  
  public Optional<UUID> getUserUUID()
  {
    return this.userUUID;
  }
  
  public Timer getTimer()
  {
    return this.timer;
  }
  
  public long getDuration()
  {
    return this.duration;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  private static final HandlerList handlers = new HandlerList();
  private final Optional<Player> player;
  private final Optional<UUID> userUUID;
  private final Timer timer;
  private final long duration;
}
