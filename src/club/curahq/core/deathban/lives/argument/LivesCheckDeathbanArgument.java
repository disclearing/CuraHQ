package club.curahq.core.deathban.lives.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.base.Strings;

import club.curahq.core.Core;
import club.curahq.core.deathban.Deathban;
import club.curahq.core.user.FactionUser;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.core.DateTimeFormats;
import me.apache.commons.lang3.time.DurationFormatUtils;

public class LivesCheckDeathbanArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public LivesCheckDeathbanArgument(Core plugin)
  {
    super("checkdeathban", "Check the deathban cause of player");
    this.plugin = plugin;
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName>";
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
      return true;
    }
    Deathban deathban = this.plugin.getUserManager().getUser(target.getUniqueId()).getDeathban();
    if ((deathban == null) || (!deathban.isActive()))
    {
      sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
      return true;
    }
    sender.sendMessage(ChatColor.DARK_AQUA + "Deathban cause of " + target.getName() + '.');
    sender.sendMessage(ChatColor.GRAY + " Time: " + DateTimeFormats.HR_MIN.format(deathban.getCreationMillis()));
    sender.sendMessage(ChatColor.GRAY + " Duration: " + DurationFormatUtils.formatDurationWords(deathban.getInitialDuration(), true, true));
    
    Location location = deathban.getDeathPoint();
    if (location != null) {
      sender.sendMessage(ChatColor.GRAY + " Location: (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ") - " + location.getWorld().getName());
    }
    sender.sendMessage(ChatColor.GRAY + " Reason: [" + Strings.nullToEmpty(deathban.getReason()) + ChatColor.GREEN + "]");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length != 2) {
      return Collections.emptyList();
    }
    List<String> results = new ArrayList<String>();
    for (FactionUser factionUser : this.plugin.getUserManager().getUsers().values())
    {
      Deathban deathban = factionUser.getDeathban();
      if ((deathban != null) && (deathban.isActive()))
      {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
        String name = offlinePlayer.getName();
        if (name != null) {
          results.add(name);
        }
      }
    }
    return results;
  }
}
