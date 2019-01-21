package club.curahq.core.game.faction;

import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.game.CaptureZone;
import club.curahq.core.game.EventType;
import club.curahq.core.util.cuboid.Cuboid;

public abstract class EventFaction
  extends ClaimableFaction
{
  public EventFaction(String name)
  {
    super(name);
    setDeathban(true);
  }
  
  public EventFaction(Map<String, Object> map)
  {
    super(map);
    setDeathban(true);
  }
  
  public String getDisplayName(Faction faction)
  {
    if (getEventType() == EventType.KOTH) {
      return ChatColor.GREEN + getName() + " KOTH";
    }
    return ChatColor.DARK_PURPLE + getEventType().getDisplayName();
  }
  
  public String getDisplayName(CommandSender sender)
  {
    if (getEventType() == EventType.KOTH) {
      return ChatColor.GREEN + getName() + " KOTH";
    }
    return ChatColor.DARK_PURPLE + getEventType().getDisplayName();
  }
  
  public void setClaim(Cuboid cuboid, CommandSender sender)
  {
    removeClaims(getClaims(), sender);
    Location min = cuboid.getMinimumPoint();
    min.setY(0);
    Location max = cuboid.getMaximumPoint();
    max.setY(256);
    addClaim(new Claim(this, min, max), sender);
  }
  
  public abstract EventType getEventType();
  
  public abstract List<CaptureZone> getCaptureZones();
}
