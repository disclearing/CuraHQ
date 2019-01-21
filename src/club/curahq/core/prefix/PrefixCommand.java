package club.curahq.core.prefix;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import club.curahq.core.util.core.ConfigUtil;

public class PrefixCommand implements CommandExecutor, Listener{

	public ArrayList<Player> choose = new ArrayList<Player>();
	private ArrayList<Player> choose2 = new ArrayList<Player>();
	private String prefixName = "";
	private String prefixDisplayName = "";
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(!(arg0 instanceof Player)) {
			return true;
		} else {
			if(arg3.length < 1) {
			PrefixMenu.openPrefix((Player) arg0); 
		} else {
			if(arg0.hasPermission("prefixes.admin")) {
				if(arg3[0].equalsIgnoreCase("create")) {
						if(arg3.length < 2) {
							return true;
						} else {
							PrefixMenu.createPrefix(arg3[1], arg3[2]);
							arg0.sendMessage(ConfigUtil.PREFIX + "§7You have successfully made the prefix " + arg3[2].replace("&", "§"));
							
						}
						
					}
				}
			}
		}
		return false;
	}
	
	
	}

