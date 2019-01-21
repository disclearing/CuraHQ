package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OreStatsCommand implements CommandExecutor, Listener {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		if ((cmd.getName().equalsIgnoreCase("ores")) && ((sender instanceof Player)) && (args.length > 1)) {
			sender.sendMessage("§cUsage: /ores <player>");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage("§cUsage: /ores <player>");
			return true;
		}
		Player target = Bukkit.getServer().getPlayer(args[0]);
		if ((args.length == 1) && (target == null)) {
			player.sendMessage("§cPlayer not found");
			return true;
		}
		this.onStatsGUI(player, target);
		/*
		 * /sender.sendMessage("§7§m--------------------------------------------------")
		 * ; sender.sendMessage("                     §eOres mined by: §6" +
		 * target.getDisplayName()); sender.sendMessage("§bDiamond(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
		 * sender.sendMessage("§aEmerald(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
		 * sender.sendMessage("§7Iron(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
		 * sender.sendMessage("§6Gold(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
		 * sender.sendMessage("§cRedstone(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
		 * sender.sendMessage("§8Coal(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
		 * sender.sendMessage("§9Lapis(s): §7" +
		 * target.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
		 * sender.sendMessage("§7§m--------------------------------------------------");
		 * /
		 */
		return false;
	}

	public void onStatsGUI(Player player, Player target) {
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Ores §8- §7" + target.getName());

		ItemStack Diamond_ore = new ItemStack(Material.DIAMOND_ORE);
		ItemStack Emerald_ore = new ItemStack(Material.EMERALD_ORE);
		ItemStack Gold_ore = new ItemStack(Material.GOLD_ORE);
		ItemStack Iron_ore = new ItemStack(Material.IRON_ORE);
		ItemStack Coal_ore = new ItemStack(Material.COAL_ORE);
		ItemStack Lapis_ore = new ItemStack(Material.LAPIS_ORE);
		ItemStack RedStone_ore = new ItemStack(Material.REDSTONE_ORE);

		ItemMeta DiamondMeta = Diamond_ore.getItemMeta();
		ItemMeta EmeraldMeta = Emerald_ore.getItemMeta();
		ItemMeta GoldMeta = Gold_ore.getItemMeta();
		ItemMeta IronMeta = Iron_ore.getItemMeta();
		ItemMeta CoalMeta = Coal_ore.getItemMeta();
		ItemMeta LapisMeta = Lapis_ore.getItemMeta();
		ItemMeta RedStoneMeta = RedStone_ore.getItemMeta();

		DiamondMeta.setDisplayName("§bDiamond(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
		EmeraldMeta.setDisplayName("§aEmerald(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
		IronMeta.setDisplayName("§7Iron(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
		GoldMeta.setDisplayName("§6Gold(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
		RedStoneMeta.setDisplayName("§cRedstone(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
		CoalMeta.setDisplayName("§8Coal(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
		LapisMeta.setDisplayName("§9Lapis(s): §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));

		Diamond_ore.setItemMeta(DiamondMeta);
		Emerald_ore.setItemMeta(EmeraldMeta);
		Iron_ore.setItemMeta(IronMeta);
		Gold_ore.setItemMeta(GoldMeta);
		RedStone_ore.setItemMeta(RedStoneMeta);
		Coal_ore.setItemMeta(CoalMeta);
		Lapis_ore.setItemMeta(LapisMeta);

		inv.setItem(0, Diamond_ore);
		inv.setItem(1, Emerald_ore);
		inv.setItem(2, Iron_ore);
		inv.setItem(3, Gold_ore);
		inv.setItem(4, RedStone_ore);
		inv.setItem(5, Coal_ore);
		inv.setItem(6, Lapis_ore);

		player.openInventory(inv);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().startsWith(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Ores §8- §7")) {
			event.setCancelled(true);
		}
	}
}