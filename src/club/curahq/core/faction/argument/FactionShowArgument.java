package club.curahq.core.faction.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionShowArgument extends CommandArgument {
    private final Core plugin;

    public FactionShowArgument(final Core plugin) {
        super("show", "Get details about a faction.", new String[]{"i", "info", "who"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName|factionName]";
    }

    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;
        if(args.length < 2) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player) sender).getUniqueId());
            if(namedFaction == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a faction.");
                return true;
            }
        } else {

            namedFaction = this.plugin.getFactionManager().getFaction(args[1]);
            playerFaction = this.plugin.getFactionManager().getFaction(args[1]);

            if(Bukkit.getPlayer(args[1]) != null){
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getPlayer(args[1]));
            }else{
                if(Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()){
                    playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                }
            }
            if(namedFaction == null && playerFaction == null) {
                sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
        }
        if(namedFaction != null) {
            namedFaction.printDetails(sender);
        }
        if(playerFaction != null && (namedFaction == null || !namedFaction.equals(playerFaction))) {
            playerFaction.printDetails(sender);
        }
        return true;
    }

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if(args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player) sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for(final Player target : Bukkit.getOnlinePlayers()) {
            if(player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
