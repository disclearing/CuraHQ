package club.curahq.core.balance;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.core.BaseConstants;

public class PayCommand
  implements CommandExecutor, TabCompleter
{
  private final Core plugin;
  
  public PayCommand(Core plugin)
  {
    this.plugin = plugin;
  }
  
  @SuppressWarnings({ "deprecation", "unlikely-arg-type" })
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
      return true;
    }
    Integer amount = JavaUtils.tryParseInt(args[1]);
    if (amount == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
      return true;
    }
    if (amount.intValue() <= 0)
    {
      sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
      return true;
    }
    Player senderPlayer = (Player)sender;
    int senderBalance = senderPlayer != null ? this.plugin.getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024;
    if (senderBalance < amount.intValue())
    {
      sender.sendMessage(ChatColor.RED + "You do not have that much money, you have: " + ChatColor.GREEN + senderBalance);
      return true;
    }
    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
    if (sender.equals(target))
    {
      sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
      return true;
    }
    Player targetPlayer = target.getPlayer();
    if ((!target.hasPlayedBefore()) && (targetPlayer == null))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
      return true;
    }
    if (targetPlayer == null) {
      return false;
    }
    if (senderPlayer != null) {
      this.plugin.getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount.intValue());
    }
    this.plugin.getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount.intValue());
    targetPlayer.sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.GRAY + " has sent you " + ChatColor.GREEN + '$' + amount + ChatColor.GRAY + '.');
    sender.sendMessage(ChatColor.GRAY + "You have sent " + ChatColor.GREEN + '$' + amount + ChatColor.GRAY + " to " + ChatColor.AQUA + target.getName() + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return args.length == 1 ? null : Collections.emptyList();
  }
}
