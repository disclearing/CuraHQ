package club.curahq.core.game.koth.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import club.curahq.core.util.command.CommandArgument;

public class KothShowArgument
  extends CommandArgument
{
  public KothShowArgument()
  {
    super("show", "View the information on a koth");
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args[1].isEmpty())
    {
      sender.sendMessage(ChatColor.RED + "FAIL: No koth");
      return true;
    }
    Bukkit.dispatchCommand(sender, "f who " + args[1]);
    return true;
  }
}
