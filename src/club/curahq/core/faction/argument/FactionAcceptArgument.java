package club.curahq.core.faction.argument;

import org.bukkit.ChatColor; 
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.FactionMember;
import club.curahq.core.faction.struct.ChatChannel;
import club.curahq.core.faction.struct.Relation;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.core.ConfigUtil;

@SuppressWarnings("deprecation")
public class FactionAcceptArgument
  extends CommandArgument
{
  private final Core plugin;
  
  public FactionAcceptArgument(Core plugin)
  {
    super("accept", "Accept a join request from an existing faction.", new String[] { "join", "a" });
    this.plugin = plugin;
  }
  
  public String getUsage(String label)
  {
    return '/' + label + ' ' + getName() + " <factionName>";
  }
  
public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    if (args.length < 2)
    {
      sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
      return true;
    }
    Player player = (Player)sender;
    if (this.plugin.getFactionManager().getPlayerFaction(player) != null)
    {
      sender.sendMessage(ChatColor.RED + "You are already in a faction.");
      return true;
    }
    Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
    if (faction == null)
    {
      sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
      return true;
    }
    if (!(faction instanceof PlayerFaction))
    {
      sender.sendMessage(ChatColor.RED + "You can only join player factions.");
      return true;
    }
    PlayerFaction targetFaction = (PlayerFaction)faction;
    plugin.getConfiguration();
	if (targetFaction.getMembers().size() >= ConfigUtil.MAX_PLAYER_PER_FACTION)
    {
      plugin.getConfiguration();
	sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + ConfigUtil.MAX_PLAYER_PER_FACTION + '.');
      return true;
    }
    if ((!targetFaction.isOpen()) && (!targetFaction.getInvitedPlayerNames().contains(player.getName())))
    {
      sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
      return true;
    }
    if (targetFaction.isLocked())
    {
      sender.sendMessage(ChatColor.RED + "You cannot join locked factions.");
      return true;
    }
    if (targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
      targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined the faction.");
    }
    return true;
  }
}
