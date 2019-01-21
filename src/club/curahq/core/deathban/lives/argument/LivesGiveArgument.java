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
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.core.BaseConstants;

public class LivesGiveArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public LivesGiveArgument(Core plugin)
  {
    super("give", "Give lives to a player");
    this.plugin = plugin;
    this.aliases = new String[] { "transfer", "send", "pay", "add" };
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <playerName> <amount>";
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    Integer amount = JavaUtils.tryParseInt(args[2]);
    if (amount == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
      return true;
    }
    if (amount.intValue() <= 0)
    {
      sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
      return true;
    }
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[1] }));
      return true;
    }
    Player onlineTarget = target.getPlayer();
    if (((sender instanceof Player)) && (!sender.hasPermission(PERMISSION)))
    {
      Player player = (Player)sender;
      int ownedLives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
      if (amount.intValue() > ownedLives)
      {
        sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount + " lives, but you only have " + ownedLives + '.');
        return true;
      }
      this.plugin.getDeathbanManager().takeLives(player.getUniqueId(), amount.intValue());
    }
    this.plugin.getDeathbanManager().addLives(target.getUniqueId(), amount.intValue());
    sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ' ' + amount + ' ' + (amount.intValue() > 1 ? "lives" : "life") + '.');
    if (onlineTarget != null) {
      onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.GOLD + amount + ' ' + (amount.intValue() > 1 ? "lives" : "life") + '.');
    }
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 2 ? null : Collections.emptyList();
  }
  
  private static final String PERMISSION = "hcf.command.lives.argument.give.bypass";
}
