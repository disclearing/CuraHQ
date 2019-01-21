package club.curahq.core.commands.essentials;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseCommand;
import club.curahq.core.util.core.BaseConstants;

public class PlayTimeCommand
  extends BaseCommand
{
  private final Core plugin;
  
  public PlayTimeCommand(Core plugin)
  {
    super("playtime", "Check the playtime of another player.");
    setAliases(new String[] { "pt" });
    setUsage("/(command) [playerName]");
    this.plugin = plugin;
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    OfflinePlayer target;
    if (args.length >= 1)
    {
      target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(getUsage(label));
        return true;
      }
      target = (OfflinePlayer)sender;
    }
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
      return true;
    }
    sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------------");
    sender.sendMessage(ChatColor.GOLD + target.getName() + " §7has a playtime of " + ChatColor.GREEN + DurationFormatUtils.formatDurationWords(this.plugin.getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true) + ChatColor.GRAY + " this map.");
    sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------------");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 1 ? null : Collections.emptyList();
  }
}