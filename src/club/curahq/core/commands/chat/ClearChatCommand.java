package club.curahq.core.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.util.core.BaseCommand;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class ClearChatCommand
  extends BaseCommand
{

private static final String[] CLEAR_MESSAGE = new String[101];
  
  public ClearChatCommand()
  {
    super("clearchat", "Clears the server chat for players.");
    setAliases(new String[] { "cc" });
    setUsage("/(command) <reason>");
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length == 0)
    {
      sender.sendMessage(getUsage());
      return true;
    }
    String reason = StringUtils.join(args, ' ');
    Player[] arrayOfPlayer;
    int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
    for (int i = 0; i < j; i++)
    {
      Player player = arrayOfPlayer[i];
      if (!player.hasPermission("command.mod")) {
        player.sendMessage(CLEAR_MESSAGE);
      }
    }
    Command.broadcastCommandMessage(sender, ChatColor.WHITE + "In-Game Chat has been cleared by " + ChatColor.YELLOW + sender.getName() + ChatColor.WHITE + " for: " + reason, true);
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.GRAY + " has cleared In-Game Chat for§7: §7" + reason);
    return true;
  }
}
