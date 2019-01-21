package club.curahq.core.deathban.lives.argument;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.command.CommandArgument;

public class LivesCheckArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public LivesCheckArgument(Core plugin)
  {
    super("check", "Check how much lives a player has");
    this.plugin = plugin;
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " [playerName]";
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    OfflinePlayer target;
    if (args.length > 1)
    {
      target = Bukkit.getOfflinePlayer(args[1]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
      }
      target = (OfflinePlayer)sender;
    }
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
      return true;
    }
    int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
    sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + (targetLives == 1 ? "life" : "lives") + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
}
