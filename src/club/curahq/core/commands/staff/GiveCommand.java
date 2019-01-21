package club.curahq.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.Core;
import club.curahq.core.util.core.BaseCommand;

public class GiveCommand extends BaseCommand
{
  public GiveCommand()
  {
    super("give", "Gives an item to a player.");
    setUsage("/(command) <playerName> <itemName> [quantity]");
  }
  
  @SuppressWarnings("deprecation")
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
      return true;
    }
    Player p = (Player)sender;
    if (args.length < 2) {
      p.sendMessage(ChatColor.RED + getUsage());
      return true;
    }
    if (Bukkit.getPlayer(args[0]) == null) {
      sender.sendMessage(ChatColor.RED + "Player not found.");
      return true;
    }
    Player t = Bukkit.getPlayer(args[0]);
    if (Core.getPlugin().getItemDb().getItem(args[1]) == null) {
      sender.sendMessage(ChatColor.RED + "Item named or with ID " + ChatColor.GOLD + args[1] + ChatColor.GRAY + " not found.");
      return true;
    }
    if (args.length == 2) {
      if (!t.getInventory().addItem(new ItemStack[] { Core.getPlugin().getItemDb().getItem(args[1], Core.getPlugin().getItemDb().getItem(args[1]).getMaxStackSize()) }).isEmpty()) {
        p.sendMessage(ChatColor.RED + "The inventory of the player is full.");
        return true;
      }
      for (Player on : Bukkit.getOnlinePlayers()) {
        if (on.hasPermission("core.give")) {
          if (on != p) {
            on.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " has given " + t.getName() + ChatColor.GOLD + " 64 " + Core.getPlugin().getItemDb().getName(Core.getPlugin().getItemDb().getItem(args[1])) + "]");
          } else {
            on.sendMessage(ChatColor.GRAY + "You gave " + ChatColor.GOLD + ChatColor.BOLD + t.getName() + ChatColor.GRAY + " " + " 64 " + Core.getPlugin().getItemDb().getName(Core.getPlugin().getItemDb().getItem(args[1])));
          }
        }
      }
    }
    if (args.length == 3) {
      if (!t.getInventory().addItem(new ItemStack[] { Core.getPlugin().getItemDb().getItem(args[1], Integer.parseInt(args[2])) }).isEmpty()) {
        p.sendMessage(ChatColor.RED + "The inventory of the player is full.");
        return true;
      }
      for (Player on : Bukkit.getOnlinePlayers()) {
        if (on.hasPermission("command.give")) {
          if (on != p) {
            on.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + p.getName() + ChatColor.GRAY + " has given " + ChatColor.GOLD + t.getName() + ChatColor.GRAY + " " + args[2] + " " + Core.getPlugin().getItemDb().getName(Core.getPlugin().getItemDb().getItem(args[1])) + "]");
          } else {
            on.sendMessage(ChatColor.GRAY + "You gave " + ChatColor.GOLD + ChatColor.BOLD + t.getName() + ChatColor.GRAY + " " + args[2] + " " + Core.getPlugin().getItemDb().getName(Core.getPlugin().getItemDb().getItem(args[1])));
          }
        }
      }
    }

    return true;
  }
}