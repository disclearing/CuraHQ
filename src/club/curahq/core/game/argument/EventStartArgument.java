package club.curahq.core.game.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import club.curahq.core.Core;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.game.faction.EventFaction;
import club.curahq.core.util.command.CommandArgument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventStartArgument extends CommandArgument {
    private final Core plugin;

    public EventStartArgument(final Core plugin) {
        super("start", "Starts an game");
        this.plugin = plugin;
        this.aliases = new String[]{"begin"};
        this.permission = "command.game.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <eventName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        if(this.plugin.getTimerManager().eventTimer.tryContesting((EventFaction) faction, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Successfully contested " + faction.getName() + '.');
        }
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2) {
            return Collections.emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
