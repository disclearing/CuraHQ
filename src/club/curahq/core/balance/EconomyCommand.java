package club.curahq.core.balance;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.core.BaseConstants;

public class EconomyCommand
  implements CommandExecutor, TabCompleter
{
  private final Core plugin;
  
  public EconomyCommand(Core plugin)
  {
    this.plugin = plugin;
  }
  
@SuppressWarnings({ "deprecation", "unlikely-arg-type" })
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
    OfflinePlayer target;
    if ((args.length > 0) && (hasStaffPermission))
    {
      target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
    }
    else
    {
      if (!(sender instanceof Player))
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
        return true;
      }
      target = (OfflinePlayer)sender;
    }
    if ((!target.hasPlayedBefore()) && (!target.isOnline()))
    {
      sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
      return true;
    }
    UUID uuid = target.getUniqueId();
    int balance = this.plugin.getEconomyManager().getBalance(uuid);
    if ((args.length < 2) || (!hasStaffPermission))
    {
      sender.sendMessage(ChatColor.GRAY + (sender.equals(target) ? "Balance: " : new StringBuilder(String.valueOf(target.getName())).append("'s Balance: ").toString()) + ChatColor.GOLD + '$' + balance);
      return true;
    }
    if (GIVE.contains(args[1].toLowerCase()))
    {
      if (args.length < 3)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
        return true;
      }
      Integer amount = JavaUtils.tryParseInt(args[2]);
      if (amount == null)
      {
        sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
        return true;
      }
      int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount.intValue());
      sender.sendMessage(new String[] { ChatColor.GRAY + "Added " + ChatColor.GREEN + '$' + JavaUtils.format(amount) + ChatColor.GRAY + " to balance of " + target.getName() + '.', ChatColor.GRAY + "Balance of " + target.getName() + " is now " + ChatColor.GREEN + '$' + newBalance + ChatColor.GRAY + '.' });
      return true;
    }
    if (TAKE.contains(args[1].toLowerCase()))
    {
      if (args.length < 3)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
        return true;
      }
      Integer amount = JavaUtils.tryParseInt(args[2]);
      if (amount == null)
      {
        sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
        return true;
      }
      int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount.intValue());
      sender.sendMessage(new String[] { ChatColor.GRAY + "Taken " + '$' + JavaUtils.format(amount) + " from balance of " + target.getName() + '.', ChatColor.GRAY + "Balance of " + target.getName() + " is now " + '$' + newBalance + '.' });
      return true;
    }
    if (!args[1].equalsIgnoreCase("set"))
    {
      sender.sendMessage(ChatColor.GOLD + (sender.equals(target) ? "Your balance" : new StringBuilder("Balance of ").append(target.getName()).toString()) + " is " + ChatColor.WHITE + '$' + balance + ChatColor.GOLD + '.');
      return true;
    }
    if (args.length < 3)
    {
      sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
      return true;
    }
    Integer amount = JavaUtils.tryParseInt(args[2]);
    if (amount == null)
    {
      sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
      return true;
    }
    int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount.intValue());
    sender.sendMessage(ChatColor.GRAY + "Set balance of " + ChatColor.GOLD + target.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + '$' + JavaUtils.format(Integer.valueOf(newBalance)) + '.');
    return true;
  }
  
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args)
  {
    return args.length == 2 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
  }
  
  private static final ImmutableList<String> TAKE = ImmutableList.of("take", "negate", "minus", "subtract");
  private static final ImmutableList<String> GIVE;
  
  static
  {
    GIVE = ImmutableList.of("give", "add");
  }
  
  private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("add", "set", "take");
}
