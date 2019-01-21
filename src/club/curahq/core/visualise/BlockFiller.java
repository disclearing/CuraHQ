package club.curahq.core.visualise;

import com.google.common.collect.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;

abstract class BlockFiller
{
  abstract VisualBlockData generate(Player paramPlayer, Location paramLocation);
  
  ArrayList<VisualBlockData> bulkGenerate(Player player, Iterable<Location> locations)
  {
    ArrayList<VisualBlockData> data = new ArrayList<VisualBlockData>(Iterables.size(locations));
    for (Location location : locations) {
      data.add(generate(player, location));
    }
    return data;
  }
}
