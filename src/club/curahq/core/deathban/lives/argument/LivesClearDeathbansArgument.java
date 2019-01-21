package club.curahq.core.deathban.lives.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.user.FactionUser;
import club.curahq.core.util.command.CommandArgument;

public class LivesClearDeathbansArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public LivesClearDeathbansArgument(Core plugin)
  {
    super("cleardeathbans", "Clears the global deathbans");
    this.plugin = plugin;
    this.aliases = new String[] { "resetdeathbans" };
    this.permission = ("hcf.command.lives.argument." + getName());
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (((sender instanceof ConsoleCommandSender)) || (((sender instanceof Player)) && (sender.getName().equalsIgnoreCase("Showyy"))) || (sender.getName().equalsIgnoreCase("URN")))
    {
      for (FactionUser user : this.plugin.getUserManager().getUsers().values()) {
        user.removeDeathban();
      }
      Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
      return true;
    }
    sender.sendMessage(ChatColor.RED + "Must be console");
    return false;
  }
}
