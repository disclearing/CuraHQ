	package club.curahq.core.commands.staff;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import club.curahq.core.commands.StaffItems;
import club.curahq.core.listener.staff.VanishListener;

public class StaffModeCommand implements Listener, CommandExecutor {
	public static ArrayList<Player> modMode = new ArrayList<Player>();
	public static ArrayList<UUID> Staff = new ArrayList<UUID>();
	public static ArrayList<Player> teleportList = new ArrayList<Player>();
	public static HashMap<String, ItemStack[]> armorContents = new HashMap<>();
	public static HashMap<String, ItemStack[]> inventoryContents = new HashMap<>();
	public String color(String msg) {
		return ChatColor.translateAlternateColorCodes('§', msg);
	}

	static StaffModeCommand instance = new StaffModeCommand();

	public static StaffModeCommand getInstance() {
		return instance;
	}

	public static boolean isMod(Player p) {
		return Staff.contains(p.getUniqueId());
		
	}

	public static boolean enterMod(final Player p) {
		modMode.add(p);
		Staff.add(p.getUniqueId());
		StaffItems.saveInventory(p);
		VanishListener.getInstance().setVanish(p, true);
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setExp(0.0F);
		p.setAllowFlight(true);
		p.setGameMode(GameMode.CREATIVE);
		StaffItems.modItems(p);
		p.sendMessage("§fYou have §aenabled §fyour §6Staff Mode");
		return true;
	}

	public static boolean leaveMod(final Player p) {
		modMode.remove(p);
		Staff.remove(p.getUniqueId());
		p.getInventory().clear();
		StaffItems.loadInventory(p);
		p.setAllowFlight(false);
		VanishListener.getInstance().setVanish(p, false);
		p.sendMessage(ChatColor.translateAlternateColorCodes('§', "§fYou have §cdisabled §fyour §6Staff Mode"));
		p.setGameMode(GameMode.SURVIVAL);
		
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("staffmode")) {
			if (!sender.hasPermission("command.mod")) {
				sender.sendMessage(ChatColor.RED + "You lack the sufficient permissions to execute this command.");
				return true;
			}
			if (args.length < 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "User command only");
					return true;
				}
				if (modMode.contains(sender)) {
					leaveMod((Player) sender);
					return true;
				}
				enterMod((Player) sender);
				return true;
			}
			if (!sender.hasPermission("command.mod.others")) {
				sender.sendMessage(ChatColor.RED + "No.");
				return true;
			}
			Player t = Bukkit.getPlayer(args[0]);
			if (t == null) {
				sender.sendMessage("§cPlayer not found.");
				return true;
			}
			if (modMode.contains(t)) {
				leaveMod(t);
				sender.sendMessage("§fYou have §cdisabled §f" + t.getName() + "'s §6Staff Mode");
				return true;
			}
			enterMod(t);
			sender.sendMessage("§fYou have §aenabled §f" + t.getName() + "'s §6Staff Mode");
			return true;
		}
		return false;
	}
	  @SuppressWarnings({ "deprecation" })
	public static void onDisableMod() {
		    Player[] arrayOfPlayer;
		    int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
		    for (int i = 0; i < j; i++) {
		      Player p = arrayOfPlayer[i];
		      if (Staff.contains(p.getUniqueId())) {
		        leaveMod(p);
		        p.sendMessage(ChatColor.RED.toString() + "You have been taken out of Staff Mode because of a reload.");
		        teleportList.remove(p);
		      }
		    }
		  }

}
