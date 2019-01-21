package club.curahq.core.commands.staff;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import me.apache.commons.lang3.text.WordUtils;

public class LagCommand implements Listener, CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		double tps = Bukkit.spigot().getTPS()[0];
		double lag = Math.round((1.0D - tps / 20.0D) * 100.0D);
		ChatColor colour;
		if (tps >= 18.0D) {
			colour = ChatColor.GREEN;
		} else {
			if (tps >= 15.0D) {
				colour = ChatColor.YELLOW;
			} else {
				colour = ChatColor.RED;
			}
		}
		sender.sendMessage("§7§m-----------------------------------------------------");
		String nameOS= System.getProperty("os.name");
		sender.sendMessage(colour + "OS: " + nameOS);
		sender.sendMessage(colour + "Server TPS is currently at " + Math.round(tps * 10000.0D) / 10000.0D + '.');
		sender.sendMessage(colour + "Server lag is currently at " + Math.round(lag * 10000.0D) / 10000.0D + '%');
		if (sender.hasPermission(command.getPermission() + ".memory")) {
			Runtime runtime = Runtime.getRuntime();
			sender.sendMessage(colour + "Processor: " +  System.getenv("PROCESSOR_IDENTIFIER"));
			sender.sendMessage(colour + "Available Processors: " + runtime.availableProcessors());
			sender.sendMessage(colour + "Max Memory: " + runtime.maxMemory() / 1048576L + "MB");
			sender.sendMessage(colour + "Total Memory: " + runtime.totalMemory() / 1048576L + "MB");
			sender.sendMessage(colour + "Free Memory: " + runtime.freeMemory() / 1048576L + "MB");
			Collection<World> worlds = Bukkit.getWorlds();

			for (World world : worlds) {
				World.Environment environment = world.getEnvironment();
				String environmentName = WordUtils.capitalizeFully(environment.name().replace('_', ' '));
				int tileEntities = 0;
				Chunk[] loadedChunks2;
				Chunk[] loadedChunks = loadedChunks2 = world.getLoadedChunks();
				for (Chunk chunk : loadedChunks2) {
					tileEntities += chunk.getTileEntities().length;
				}
				sender.sendMessage(ChatColor.RED + world.getName() + '(' + environmentName + "): " + ChatColor.YELLOW
						+ loadedChunks.length + " chunks, " + world.getEntities().size() + " entities, " + tileEntities
						+ " tile entities.");
			}
			sender.sendMessage("§7§m-----------------------------------------------------");	
		}
		return true;
	}
}
