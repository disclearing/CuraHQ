package club.curahq.core.listener;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.collect.ImmutableSet;

import club.curahq.core.Core;
import club.curahq.core.faction.event.FactionChatEvent;
import club.curahq.core.faction.struct.ChatChannel;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.prefix.PrefixMenu;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatListener implements Listener
  {
  public ChatListener(Core plugin)
  {
    this.plugin = plugin;
  }
  
  @SuppressWarnings({ "deprecation" })
@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerChat(AsyncPlayerChatEvent event)
  {
    String message = event.getMessage();
    Player player = event.getPlayer();
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
    Set<Player> recipients = event.getRecipients();
    
    if ((chatChannel == ChatChannel.FACTION) || (chatChannel == ChatChannel.ALLIANCE))
    {
      if (!isGlobalChannel(message))
      {
        Collection<Player> online = playerFaction.getOnlinePlayers();
        if (chatChannel == ChatChannel.ALLIANCE)
        {
          Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
          for (PlayerFaction ally : allies) {
            online.addAll(ally.getOnlinePlayers());
          }
        }
        recipients.retainAll(online);
        event.setFormat(chatChannel.getRawFormat(player));
        Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
        return;
      }
      message = message.substring(1, message.length()).trim();
      event.setMessage(message);
    }
    event.setCancelled(true);
    Boolean.valueOf(true);
    if (player.hasPermission("faction.removetag")) {
      Boolean.valueOf(true);
    }
    String rank = ChatColor.translateAlternateColorCodes('&', "&7" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
    String displayName = player.getDisplayName();
    displayName = rank + displayName;
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    String tag = playerFaction == null ? ChatColor.DARK_RED + "-" : playerFaction.getDisplayName(console);
    console.sendMessage(ChatColor.DARK_GRAY + "[" + tag + ChatColor.DARK_GRAY + "] " + displayName + ChatColor.GOLD + " » " + ChatColor.GRAY + message);
    for (Player recipient : event.getRecipients())
    {

    	      tag = playerFaction == null ? ChatColor.RED + "*" : playerFaction.getDisplayName(recipient);
    	      recipient.sendMessage(PrefixMenu.getPrefix(player) + "" + ChatColor.DARK_GRAY + "[" + tag + ChatColor.DARK_GRAY + "] " + displayName + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + message);
    	
        
      }
  }
  
  private boolean isGlobalChannel(String input)
  {
    int length = input.length();
    if ((length <= 1) || (!input.startsWith("!"))) {
      return false;
    }
    int i = 1;
    while (i < length)
    {
      char character = input.charAt(i);
      if (character == ' ')
      {
        i++;
      }
      else
      {
        if (character != '/') {
          break;
        }
        return false;
      }
    }
    return true;
  }
  
  static
  {
    ImmutableSet.builder();
  }
  
  private final Core plugin;
}
