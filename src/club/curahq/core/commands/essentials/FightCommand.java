package club.curahq.core.commands.essentials;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FightCommand implements CommandExecutor
{
    private static final long FIGHT_COOLDOWN_DELAY;
    public static final TObjectLongMap<UUID> FIGHT_COOLDOWN;
    
    static {
        FIGHT_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(30L);
        FIGHT_COOLDOWN = (TObjectLongMap)new TObjectLongHashMap();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No Console");
            return true;
        }
        final Player player = (Player)sender;
	    final int x = player.getLocation().getBlockX();
	    final int y = player.getLocation().getBlockY();
	    final int z = player.getLocation().getBlockZ();
	    
        if (command.getName().equalsIgnoreCase("fight")) {
            if (!player.hasPermission("command.fight")) {
                sender.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
                return true;
            }
            if (args.length == 0) {
                    final UUID uuid = player.getUniqueId();
                    final long timestamp = FightCommand.FIGHT_COOLDOWN.get(uuid);
                    final long millis = System.currentTimeMillis();
                    final long remaining = (timestamp == FightCommand.FIGHT_COOLDOWN.getNoEntryValue()) ? -1L : (timestamp - millis);
                    if (remaining > 0L) {
                        player.sendMessage(ChatColor.RED + "You cannot use this command for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
                        return true;
                    }                  
	                  Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
	                  Bukkit.broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + sender.getName() + ChatColor.GRAY + " has broadcasted their Location �f(" + x + ", " + y + ", " + z + ")");
	                  Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
                  }
                  player.sendMessage(ChatColor.GRAY + "You have announced that you are looking for a fight, you must wait 30 minutes before doing this again.");
                  FightCommand.FIGHT_COOLDOWN.put((UUID)player.getUniqueId(), System.currentTimeMillis() + FightCommand.FIGHT_COOLDOWN_DELAY);
                }
        return true;
    }
}