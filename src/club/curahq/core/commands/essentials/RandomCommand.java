package club.curahq.core.commands.essentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;

public class RandomCommand implements CommandExecutor {
	public RandomCommand(Core plugin) {
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		List<Player> players = new ArrayList<Player>();
		Player[] arrayOfPlayer;
		int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
		for (int i = 0; i < j; i++) {
			Player players2 = arrayOfPlayer[i];
			players.add(players2);
		}
		Collections.shuffle(players);
		Random random = new Random();
		Integer randoms = Integer.valueOf(random.nextInt(Bukkit.getOnlinePlayers().length));
		Player p = (Player) players.get(randoms.intValue());
		if ((player.canSee(p)) && (player.hasPermission(command.getPermission() + ".teleport"))) {
			player.teleport(p);
			player.sendMessage(ChatColor.GRAY + "You have teleported to " + ChatColor.GOLD + p.getName());
		} else if (player.canSee(p)) {
			player.sendMessage(ChatColor.GRAY + "You have found " + ChatColor.GOLD + p.getName());
		} else {
			player.sendMessage(ChatColor.RED + "Player not found");
		}
		return true;
	}
}
