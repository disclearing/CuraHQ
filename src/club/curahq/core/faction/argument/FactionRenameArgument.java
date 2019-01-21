package club.curahq.core.faction.argument;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.core.ConfigUtil;

import java.util.concurrent.TimeUnit;

public class FactionRenameArgument extends CommandArgument {
    private static final long FACTION_RENAME_DELAY_MILLIS;
    private static final String FACTION_RENAME_DELAY_WORDS;

    static {
        FACTION_RENAME_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(15L);
        FACTION_RENAME_DELAY_WORDS = DurationFormatUtils.formatDurationWords(FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS, true, true);
    }

    private final Core plugin;

    public FactionRenameArgument(final Core plugin) {
        super("rename", "Change the name of your faction.");
        this.plugin = plugin;
        this.aliases = new String[]{"changename", "setname"};
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <newFactionName>";
    }

    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create faction.");
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
        if(playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to edit the name.");
            return true;
        }
        final String newName = args[1];
        if(ConfigUtil.DISALLOWED_FACTION_NAMES.contains(newName.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + newName + "' is a blocked faction name.");
            return true;
        }
        if(newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if(newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if(!JavaUtils.isAlphanumeric(newName)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if(this.plugin.getFactionManager().getFaction(newName) != null) {
            sender.sendMessage(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
            return true;
        }
        final long difference = playerFaction.lastRenameMillis - System.currentTimeMillis() + FactionRenameArgument.FACTION_RENAME_DELAY_MILLIS;
        if(!player.isOp() && difference > 0L) {
            player.sendMessage(ChatColor.RED + "There is a faction rename delay of " + FactionRenameArgument.FACTION_RENAME_DELAY_WORDS + ". Therefore you need to wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + " to rename your faction.");
            return true;
        }
        playerFaction.setName(args[1], sender);
        return true;
    }
}
