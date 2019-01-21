package club.curahq.core.commands.staff;

import java.util.Collections; 
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import club.curahq.core.util.core.ConfigUtil;
import me.apache.commons.lang3.time.FastDateFormat;

public class ServerTimeCommand implements CommandExecutor, TabCompleter {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.GRAY + "The server time is " + ChatColor.GOLD
				+ FORMAT.format(System.currentTimeMillis()) + ChatColor.GRAY + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}

	private static final FastDateFormat FORMAT = FastDateFormat.getInstance("E MMM dd h:mm:ssa z yyyy",
			ConfigUtil.SERVER_TIME_ZONE);
}
