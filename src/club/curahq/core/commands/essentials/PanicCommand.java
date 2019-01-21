package club.curahq.core.commands.essentials;


import org.bukkit.Bukkit; 
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PanicCommand
  implements CommandExecutor
{
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
  {
    if (commandLabel.equalsIgnoreCase("panic") && (sender.hasPermission("command.panic")))
    {
      sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have entered panic mode. Please join Teamspeak for further assistance.");
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ss " + sender.getName());
      Player[] arrayOfPlayer;
      int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
      for (int i = 0; i < j; i++)
      {
        Player players = arrayOfPlayer[i];
        if (players.hasPermission("command.mod")) {
          players.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&3&lPANIC&8) &b" + sender.getName() + " &7has just entered &6&lPanic Mode"));
        }
      }
    }
    return false;
  }
}
