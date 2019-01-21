package club.curahq.core.faction.argument;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.struct.ChatChannel;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.command.CommandArgument;

import java.util.Iterator;

public class FactionMessageArgument extends CommandArgument {
    private final Core plugin;

    public FactionMessageArgument(final Core plugin) {
        super("message", "Sends a message to your faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"msg"};
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <message>";
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player) sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", me.apache.commons.lang3.StringUtils.join((Object[]) args, (char) ' ', (int) 1, (int) args.length));
        Iterator<Player> iterator = playerFaction.getOnlinePlayers().iterator();
        while(iterator.hasNext()) {
            Player target = iterator.next();
            target.sendMessage(format);
        }
        return true;
    }
}
