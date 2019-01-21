package club.curahq.core.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

public abstract interface SidebarProvider
{
  public abstract String getTitle();
  
  public abstract List<SidebarEntry> getLines(Player paramPlayer);
}
