package club.curahq.core.game;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import club.curahq.core.Core;
import club.curahq.core.game.tracker.ConquestTracker;
import club.curahq.core.game.tracker.EventTracker;
import club.curahq.core.game.tracker.KothTracker;

public enum EventType
{
  CONQUEST("Conquest", new ConquestTracker(Core.getPlugin())),  KOTH("KOTH", new KothTracker(Core.getPlugin()));
  
  private static final ImmutableMap<String, EventType> byDisplayName;
  private final EventTracker eventTracker;
  private final String displayName;
  
  private EventType(String displayName, EventTracker eventTracker)
  {
    this.displayName = displayName;
    this.eventTracker = eventTracker;
  }
  
  @Deprecated
  public static EventType getByDisplayName(String name)
  {
    return (EventType)byDisplayName.get(name.toLowerCase());
  }
  
  public EventTracker getEventTracker()
  {
    return this.eventTracker;
  }
  
  public String getDisplayName()
  {
    return this.displayName;
  }
  
  static
  {
    ImmutableMap.Builder<String, EventType> builder = new ImmutableBiMap.Builder();
    EventType[] arrayOfEventType;
    int j = (arrayOfEventType = values()).length;
    for (int i = 0; i < j; i++)
    {
      EventType eventType = arrayOfEventType[i];
      builder.put(eventType.displayName.toLowerCase(), eventType);
    }
    byDisplayName = builder.build();
  }
}
