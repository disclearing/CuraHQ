package club.curahq.core.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import club.curahq.core.Core;
import club.curahq.core.game.argument.EventCancelArgument;
import club.curahq.core.game.argument.EventCreateArgument;
import club.curahq.core.game.argument.EventDeleteArgument;
import club.curahq.core.game.argument.EventRenameArgument;
import club.curahq.core.game.argument.EventSetAreaArgument;
import club.curahq.core.game.argument.EventSetCapzoneArgument;
import club.curahq.core.game.argument.EventStartArgument;
import club.curahq.core.game.argument.EventUptimeArgument;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.command.ArgumentExecutor;
import club.curahq.core.util.command.CommandArgument;

public class EventExecutor
  extends ArgumentExecutor
{
  public EventExecutor(Core plugin)
  {
    super("event");
    addArgument(new EventCancelArgument(plugin));
    addArgument(new EventCreateArgument(plugin));
    addArgument(new EventDeleteArgument(plugin));
    addArgument(new EventRenameArgument(plugin));
    addArgument(new EventSetAreaArgument(plugin));
    addArgument(new EventSetCapzoneArgument(plugin));
    addArgument(new EventStartArgument(plugin));
    addArgument(new EventUptimeArgument(plugin));
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length < 1)
    {
      if (sender.hasPermission("event.admin"))
      {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Event Help");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " cancel <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Cancels a running event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " create <koth> <Conquest:KoTH>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Defines a new event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " delete <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Deletes an event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " setarea <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Sets the area of an event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " setcapzone <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Sets the capzone of an event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " start <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Start an event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " rename <koth> <newkoth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Renames an event.");
        sender.sendMessage(ChatColor.YELLOW + " /" + this.getLabel() + " uptime <koth>" + ChatColor.DARK_GRAY +" - " + ChatColor.GRAY + "Check the uptime of an event.");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
      }
      return true;
    }
    CommandArgument argument2 = getArgument(args[0]);
    String permission2 = argument2 == null ? null : argument2.getPermission();
    if ((argument2 == null) || ((permission2 != null) && (!sender.hasPermission(permission2))))
    {
      sender.sendMessage(ChatColor.RED + "Command not found");
      return true;
    }
    argument2.onCommand(sender, command, label, args);
    return true;
  }
}
