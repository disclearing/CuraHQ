package club.curahq.core.commands.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TLCommand implements CommandExecutor {
	
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No Console");
            return true;
        }
        final Player player = (Player)sender;
	    final int x = player.getLocation().getBlockX();
	    final int y = player.getLocation().getBlockY();
	    final int z = player.getLocation().getBlockZ();
		
	    if (command.getName().equalsIgnoreCase("tl")) {
	    		player.chat("/f message " + x + ", " + y + ", " + z);
                return true;
            }
		return true;
    }

}
