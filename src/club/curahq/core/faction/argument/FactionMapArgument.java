package club.curahq.core.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.LandMap;
import club.curahq.core.user.FactionUser;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.visualise.VisualType;
import compat.com.google.common.collect.GuavaCompat;


public class FactionMapArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public FactionMapArgument(Core plugin)
  {
    super("map", "View all claims around your chunk.");
    this.plugin = plugin;
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " [factionName]";
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    Player player = (Player)sender;
    UUID uuid = player.getUniqueId();
    
    FactionUser factionUser = this.plugin.getUserManager().getUser(uuid);
    VisualType visualType1;
    if (args.length <= 1)
    {
      visualType1 = VisualType.CLAIM_MAP;
    }
    else if ((visualType1 = (VisualType)GuavaCompat.getIfPresent(VisualType.class, args[1]).orNull()) == null)
    {
      player.sendMessage(ChatColor.RED + "Visual type " + args[1] + " not found.");
      return true;
    }
    boolean newShowingMap = !factionUser.isShowClaimMap();
    if (newShowingMap)
    {
      if (!LandMap.updateMap(player, this.plugin, visualType1, true)) {
        return true;
      }
    }
    else
    {
      this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType1, null);
      sender.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
    }
    factionUser.setShowClaimMap(newShowingMap);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if ((args.length != 2) || (!(sender instanceof Player))) {
      return Collections.emptyList();
    }
    VisualType[] values = VisualType.values();
    List<String> results = new ArrayList<String>(values.length);
    VisualType[] arrayOfVisualType1;
    int j = (arrayOfVisualType1 = values).length;
    for (int i = 0; i < j; i++)
    {
      VisualType visualType = arrayOfVisualType1[i];
      results.add(visualType.name());
    }
    return results;
  }
}

