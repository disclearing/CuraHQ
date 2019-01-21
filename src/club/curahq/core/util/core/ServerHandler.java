package club.curahq.core.util.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import club.curahq.core.Core;

public class ServerHandler
{
  private final List<String> announcements = new ArrayList<String>();
  private final List<String> serverRules = new ArrayList<String>();
  private final Core plugin;
  private int clearlagdelay;
  public boolean useProtocolLib;
  private int announcementDelay;
  private long chatSlowedMillis;
  private long chatDisabledMillis;
  private int chatSlowedDelay;
  private String broadcastFormat;
  private FileConfiguration config;
  private boolean decreasedLagMode;
  private int worldBorder;
  private boolean end;
  private Location endExit;
  private boolean donorOnly;
  private int netherBorder;
  private int endBorder;
  
  public ServerHandler(Core plugin)
  {
    this.plugin = plugin;
    this.config = plugin.getConfig();
    reloadServerData();
  }
  
  public void setServerBorder(World.Environment environment, Integer integer)
  {
    if (environment.equals(World.Environment.NORMAL)) {
      this.worldBorder = integer.intValue();
    } else if (environment.equals(World.Environment.NETHER)) {
      this.netherBorder = integer.intValue();
    } else if (environment.equals(World.Environment.THE_END)) {
      this.endBorder = integer.intValue();
    }
  }
  
  public void setDonorOnly(boolean value)
  {
    this.donorOnly = value;
  }
  
  public boolean isDonorOnly()
  {
    return this.donorOnly;
  }
  
  public void setEndExit(Location location)
  {
    this.endExit = location;
  }
  
  public Location getEndExit()
  {
    return this.endExit;
  }
  
  public void setEnd(boolean end)
  {
    this.end = end;
  }
  
  public boolean isEnd()
  {
    return this.end;
  }
  
  public Integer getWorldBorder()
  {
    return Integer.valueOf(this.worldBorder);
  }
  
  public Integer getNetherBorder()
  {
    return Integer.valueOf(this.netherBorder);
  }
  
  public Integer getEndBorder()
  {
    return Integer.valueOf(this.endBorder);
  }
  
  public int getAnnouncementDelay()
  {
    return this.announcementDelay;
  }
  
  public void setClearlagdelay(Integer integer)
  {
    this.clearlagdelay = integer.intValue();
  }
  
  public int getClaggDelay()
  {
    return this.clearlagdelay;
  }
  
  public void setAnnouncementDelay(int delay)
  {
    this.announcementDelay = delay;
  }
  
  public List<String> getAnnouncements()
  {
    return this.announcements;
  }
  
  public boolean isChatSlowed()
  {
    return getRemainingChatSlowedMillis() > 0L;
  }
  
  public long getChatSlowedMillis()
  {
    return this.chatSlowedMillis;
  }
  
  public void setChatSlowedMillis(long ticks)
  {
    this.chatSlowedMillis = (System.currentTimeMillis() + ticks);
  }
  
  public long getRemainingChatSlowedMillis()
  {
    return this.chatSlowedMillis - System.currentTimeMillis();
  }
  
  public boolean isChatDisabled()
  {
    return getRemainingChatDisabledMillis() > 0L;
  }
  
  public long getChatDisabledMillis()
  {
    return this.chatDisabledMillis;
  }
  
  public void setChatDisabledMillis(long ticks)
  {
    long millis = System.currentTimeMillis();
    this.chatDisabledMillis = (millis + ticks);
  }
  
  public long getRemainingChatDisabledMillis()
  {
    return this.chatDisabledMillis - System.currentTimeMillis();
  }
  
  public int getChatSlowedDelay()
  {
    return this.chatSlowedDelay;
  }
  
  public void setChatSlowedDelay(int delay)
  {
    this.chatSlowedDelay = delay;
  }
  
  public String getBroadcastFormat()
  {
    return this.broadcastFormat;
  }
  
  public void setBroadcastFormat(String broadcastFormat)
  {
    this.broadcastFormat = broadcastFormat;
  }
  
  public boolean isDecreasedLagMode()
  {
    return this.decreasedLagMode;
  }
  
  public void setDecreasedLagMode(boolean decreasedLagMode)
  {
    this.decreasedLagMode = decreasedLagMode;
  }
  
  public void reloadServerData()
  {
    this.plugin.reloadConfig();
    this.config = this.plugin.getConfig();
    String exitWorld = this.config.getString("end.exitLocation.world", "world");
    double x = this.config.getDouble("end.exitLocation.x", 0.0D);
    double y = this.config.getDouble("end.exitLocation.y", 66.0D);
    double z = this.config.getDouble("end.exitLocation.z", -200.0D);
    this.endExit = new Location(Bukkit.getWorld(exitWorld), x, y, z);
    this.donorOnly = this.config.getBoolean("donor-only-enter");
    this.end = this.config.getBoolean("end-open");
    this.worldBorder = this.config.getInt("border.worldBorder", 3000);
    this.netherBorder = this.config.getInt("border.netherBorder", 1000);
    this.endBorder = this.config.getInt("border.endBorder", 1500);
    this.serverRules.clear();
    this.clearlagdelay = this.config.getInt("clearlag.delay", 200);
    this.announcementDelay = this.config.getInt("announcements.delay", 15);
    this.announcements.clear();
    for (String each : this.config.getStringList("announcements.list")) {
      this.announcements.add(ChatColor.translateAlternateColorCodes('&', each));
    }
    this.chatDisabledMillis = this.config.getLong("chat.disabled.millis", 0L);
    this.chatSlowedMillis = this.config.getLong("chat.slowed.millis", 0L);
    this.chatSlowedDelay = this.config.getInt("chat.slowed.delay", 15);
    this.useProtocolLib = this.config.getBoolean("use-protocol-lib", true);
    this.decreasedLagMode = this.config.getBoolean("decreased-lag-mode");
    this.broadcastFormat = ChatColor.translateAlternateColorCodes('&', this.config.getString("broadcast.format", ChatColor.AQUA + " &7%1$s"));
  }
  
  public void saveServerData()
  {
    this.config.set("clearlag.delay", Integer.valueOf(this.clearlagdelay));
    this.config.set("server-rules", this.serverRules);
    this.config.set("use-protocol-lib", Boolean.valueOf(this.useProtocolLib));
    this.config.set("chat.disabled.millis", Long.valueOf(this.chatDisabledMillis));
    this.config.set("chat.slowed.millis", Long.valueOf(this.chatSlowedMillis));
    this.config.set("chat.slowed-delay", Integer.valueOf(this.chatSlowedDelay));
    this.config.set("announcements.delay", Integer.valueOf(this.announcementDelay));
    this.config.set("announcements.list", this.announcements);
    this.config.set("decreased-lag-mode", Boolean.valueOf(this.decreasedLagMode));
    this.config.set("end.exitLocation.world", this.endExit.getWorld().getName());
    this.config.set("end.exitLocation.x", Double.valueOf(this.endExit.getX()));
    this.config.set("end.exitLocation.y", Double.valueOf(this.endExit.getY()));
    this.config.set("end.exitLocation.z", Double.valueOf(this.endExit.getX()));
    this.config.set("donor-only-enter", Boolean.valueOf(this.donorOnly));
    this.config.set("end-open", Boolean.valueOf(this.end));
    this.config.set("border.worldBorder", Integer.valueOf(this.worldBorder));
    this.config.set("border.netherBorder", Integer.valueOf(this.netherBorder));
    this.config.set("border.endBorder", Integer.valueOf(this.endBorder));
    this.plugin.saveConfig();
  }
}
