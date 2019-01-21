package club.curahq.core.commands.teleport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseCommand;

public class WorldCommand
  extends BaseCommand
{
  public WorldCommand()
  {
    super("world", "Change current world.");
    setAliases(new String[] { "changeworld", "switchworld" });
    setUsage("/(command) <worldName>");
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
      return true;
    }
    if (args.length < 1)
    {
      sender.sendMessage(ChatColor.RED + getUsage());
      return true;
    }
    World world = Bukkit.getWorld(args[0]);
    if (world == null)
    {
      sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found.");
      return true;
    }
    Player player = (Player)sender;
    if (player.getWorld().equals(world))
    {
      sender.sendMessage(ChatColor.RED + "You are already in that world.");
      return true;
    }
    Location origin = player.getLocation();
    Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ(), origin.getYaw(), origin.getPitch());
    player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
    sender.sendMessage(ChatColor.GRAY + "Switched world to '" + world.getName() + ChatColor.AQUA + " [" + WordUtils.capitalizeFully(world.getEnvironment().name().replace('_', ' ')) + ']' + ChatColor.AQUA + "'.");
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    if (args.length != 1) {
      return Collections.emptyList();
    }
    List<?> worlds = Bukkit.getWorlds();
    ArrayList<String> results = new ArrayList<String>(worlds.size());
    for (World world : Bukkit.getWorlds()) {
      results.add(world.getName());
    }
    return BukkitUtils.getCompletions(args, results);
  }
}