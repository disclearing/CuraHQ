package club.curahq.core.commands.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.commands.MessageEvent;
import net.md_5.bungee.api.ChatColor;

public class ReplyCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
      if (args.length == 0)
      {
        sender.sendMessage(ChatColor.RED + "Usage: /reply <message>");
      }
      else if (MessageCommand.reply.get(sender) == null)
      {
        sender.sendMessage(ChatColor.RED + "You have nobody to reply to.");
      }
      else if (((Player)MessageCommand.reply.get(sender)).getName() == sender.getName())
      {
        sender.sendMessage(ChatColor.RED + "You cannot message yourself.");
      }
      else if (args[0].equalsIgnoreCase("who"))
      {
        sender.sendMessage(MessageEvent.format("&7You are currently in a conversation with &b" + ((Player)MessageCommand.reply.get(sender)).getName()));
      }
      else if ((MessageCommand.toggle.contains(((Player)MessageCommand.reply.get(sender)).getName())) && (!sender.hasPermission("command.message.bypass")))
      {
        sender.sendMessage(MessageEvent.format("&f" + ((Player)MessageCommand.reply.get(sender)).getName() + " has their messages disabled."));
      }
      else
      {
        String msgto = MessageEvent.format("&8(&7To " + "&f" + MessageCommand.reply.get(sender).getName() + "&8)&7 " + MessageEvent.toString(args, 0));
        sender.sendMessage(msgto);
        String msgget = MessageEvent.format("&8(&7From " + ChatColor.WHITE + sender.getName() + "&8)&7 " + MessageEvent.toString(args, 0));
        ((Player)MessageCommand.reply.get(sender)).sendMessage(MessageEvent.format(msgget));
        
        return true;
      }
    return false;
  }
}

