package club.curahq.core.commands.staff;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import club.curahq.core.Core;
import club.curahq.core.user.FactionUser;
import club.curahq.core.user.UserManager;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.chat.Text;
import club.curahq.core.util.core.BaseCommand;
import club.curahq.core.util.core.BaseConstants;
import club.curahq.core.util.core.StaffPriority;
import me.apache.commons.lang3.text.WordUtils;
import me.apache.commons.lang3.time.DurationFormatUtils;

public class WhoisCommand extends BaseCommand {
	
    private final Core plugin;

    public WhoisCommand(final Core plugin) {
        super("whois", "Check information about a player.");
        this.plugin = plugin;
        this.setUsage("/(command) [playerName]");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 1) {
            sender.sendMessage(this.getUsage());
            return true;
        }
        final Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
        if(target == null || !BaseCommand.canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }
        final Location location = target.getLocation();
        final World world = location.getWorld();
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + " [" + ChatColor.GRAY + target.getDisplayName() + ChatColor.GRAY + ']');
        sender.sendMessage(ChatColor.GOLD + "  Hunger: " + ChatColor.GRAY + target.getFoodLevel() + '/' + 20 + " (" + target.getSaturation() + " saturation)");
        sender.sendMessage(ChatColor.GOLD + "  Exp/Level: " + ChatColor.GRAY + target.getExp() + '/' + target.getLevel());
        sender.sendMessage(ChatColor.GOLD + "  Location: " + ChatColor.GRAY + world.getName() + ' ' + ChatColor.GRAY + '[' + WordUtils.capitalizeFully(world.getEnvironment().name().replace('_', ' ')) + "] " + ChatColor.GRAY + '(' + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ')');
        sender.sendMessage(ChatColor.GOLD + "  Operator: " + ChatColor.GRAY + target.isOp());
        sender.sendMessage(ChatColor.GOLD + "  GameMode: " + ChatColor.GRAY + WordUtils.capitalizeFully(target.getGameMode().name().replace('_', ' ')));
        sender.sendMessage(ChatColor.GOLD + "  Idle Time: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(BukkitUtils.getIdleTime(target), true, true));
        sender.sendMessage(ChatColor.GOLD + "  IP4 Address: " + ChatColor.GRAY + (sender.hasPermission(command.getPermission() + ".ip") ? target.getAddress().getHostString(): ChatColor.STRIKETHROUGH + "1.1.1.1" ));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
