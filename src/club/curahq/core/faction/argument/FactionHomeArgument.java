package club.curahq.core.faction.argument;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import club.curahq.core.Core;
import club.curahq.core.faction.FactionExecutor;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.game.faction.EventFaction;
import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.util.command.CommandArgument;

public class FactionHomeArgument
  extends CommandArgument
{
  private final FactionExecutor factionExecutor;
  private final Core plugin;
  
  public FactionHomeArgument(FactionExecutor factionExecutor, Core plugin)
  {
    super("home", "Teleport to the faction home.");
    this.factionExecutor = factionExecutor;
    this.plugin = plugin;
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName();
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    Player player = (Player)sender;
    if ((args.length >= 2) && (args[1].equalsIgnoreCase("set")))
    {
      this.factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
      return true;
    }
    UUID uuid = player.getUniqueId();
    PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
    long remaining = timer.getRemaining(player);
    if (remaining > 0L)
    {
      sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + Core.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
      return true;
    }
    if ((remaining = (timer = this.plugin.getTimerManager().combatTimer).getRemaining(player)) > 0L)
    {
      sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + Core.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
      return true;
    }
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
    if (playerFaction == null)
    {
      sender.sendMessage(ChatColor.RED + "You are not in a faction.");
      return true;
    }
    Location home = playerFaction.getHome();
    if (home == null)
    {
      sender.sendMessage(ChatColor.RED + "Your faction does not have a home set.");
      return true;
    }
    Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
    if ((factionAt instanceof EventFaction))
    {
      sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
      return true;
    }
    long millis = 0L;
    if (factionAt.isSafezone())
    {
      if (player.getWorld().getEnvironment() == World.Environment.THE_END)
      {
        sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction home whilst in The End.");
        return true;
      }
      if (this.plugin.getTimerManager().invincibilityTimer.getRemaining(player.getUniqueId()) > 0L)
      {
        player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it first.");
        return true;
      }
      millis = 0L;
    }
    else
    {
      switch (player.getWorld().getEnvironment())
      {
      case THE_END: 
        sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction home whilst in The End.");
        return true;
      case NORMAL: 
        millis = 10000L;
        break;
      default: 
        millis = 10000L;
      }
    }
    if ((!factionAt.equals(playerFaction)) && ((factionAt instanceof PlayerFaction)))
    {
      if (!((PlayerFaction)factionAt).isRaidable())
      {
        player.sendMessage(ChatColor.RED + "You are currently in enemy territory. Please use /f stuck.");
        return true;
      }
      millis *= 2L;
    }
    if (this.plugin.getTimerManager().invincibilityTimer.getRemaining(player.getUniqueId()) > 0L)
    {
      player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it first.");
      return true;
    }
    if (this.plugin.getTimerManager().home.getRemaining(player.getUniqueId()) > 0L)
    {
      player.sendMessage(ChatColor.RED + "You teleportation timer is already active.");
      return true;
    }
    if ((this.plugin.getTimerManager().teleportTimer.getNearbyEnemies(player, 60) != 0))
    {
      
      this.plugin.getTimerManager().teleportTimer.teleport(player, home, millis, ChatColor.YELLOW + "Teleporting to your faction home in " + ChatColor.LIGHT_PURPLE + Core.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Do not move or take damage.", TeleportCause.COMMAND);
      Core.getPlugin().getTimerManager().home.setCooldown(player,uuid);
      return true;
    }
    this.plugin.getTimerManager().teleportTimer.teleport(player, home, millis, ChatColor.YELLOW + "Teleporting to your faction home in " + ChatColor.LIGHT_PURPLE + Core.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Do not move or take damage.", TeleportCause.COMMAND);
    Core.getPlugin().getTimerManager().home.setCooldown(player,uuid);
    return true;
  }
}

