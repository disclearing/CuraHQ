package club.curahq.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;

public class SpawnCommand
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    Player player = (Player)sender;
    if (cmd.getName().equalsIgnoreCase("spawn"))
    {
    		if (args.length == 0) {
    		sendLocation(player);
    		return true;
      }

      Player target = Bukkit.getPlayer(args[0]);
      if (target == null)
      {
        player.sendMessage(ChatColor.RED + "Player isnt online");
        return true;
      }
      player.sendMessage(ChatColor.GREEN + target.getName() + " has been teleported to spawn!");
      sendLocation(target);
      return true;
    }
    if ((cmd.getName().equalsIgnoreCase("setspawn")) && 
      (cmd.getName().equalsIgnoreCase("setspawn")))
    {
    	Core.getPlugin().getConfig().set("spawn.world", player.getLocation().getWorld().getName());
    	Core.getPlugin().getConfig().set("spawn.x", Double.valueOf(player.getLocation().getX()));
    	Core.getPlugin().getConfig().set("spawn.y", Double.valueOf(player.getLocation().getY()));
    	Core.getPlugin().getConfig().set("spawn.z", Double.valueOf(player.getLocation().getZ()));
    	Core.getPlugin().getConfig().set("spawn.yaw", Float.valueOf(player.getLocation().getYaw()));
    	Core.getPlugin().getConfig().set("spawn.pitch", Float.valueOf(player.getLocation().getPitch()));
    	Core.getPlugin().saveConfig();
      player.sendMessage(ChatColor.GREEN + "Spawn set!");
      return true;
    }
    return false;
  }
  
  public static boolean sendLocation(Player player)
  {
    World w = Bukkit.getServer().getWorld(Core.getPlugin().getConfig().getString("spawn.world"));
    double x = Core.getPlugin().getConfig().getDouble("spawn.x");
    double y = Core.getPlugin().getConfig().getDouble("spawn.y");
    double z = Core.getPlugin().getConfig().getDouble("spawn.z");
    float yaw = (float)Core.getPlugin().getConfig().getDouble("spawn.yaw");
    float pitch = (float)Core.getPlugin().getConfig().getDouble("spawn.pitch");
    player.teleport(new Location(w, x, y, z, yaw, pitch));
    return false;
  }
}