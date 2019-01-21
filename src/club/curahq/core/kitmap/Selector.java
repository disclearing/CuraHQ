package club.curahq.core.kitmap;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.curahq.core.Core;


public class Selector {
	
	Core plugin = Core.getPlugin();
	
	public void open(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, "§e§lKIT SELECTOR");
		//===================== Item Stacks =====================
		ItemStack diamond = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack archer = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack bard = new ItemStack(Material.GOLD_CHESTPLATE);
		ItemStack rogue = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		
		//===================== Item Metas =====================
		ItemMeta d = diamond.getItemMeta();
		ItemMeta a = archer.getItemMeta();
		ItemMeta b = bard.getItemMeta();
		ItemMeta r = rogue.getItemMeta();
		
		//===================== Lores and Names =====================
		d.setDisplayName("§e§lDIAMOND CLASS");
		d.setLore(Arrays.asList("§7§m---------------------", "", "§e§lDIAMOND §7Class!", "", "§e§lRIGHT CLICK §7to select the §e§lDIAMOND §7Class!", "", "§7§m---------------------"));
		
		a.setDisplayName("§e§lARCHER CLASS");
		a.setLore(Arrays.asList("§7§m---------------------", "", "§e§lARCHER §7Class!", "", "§e§lRIGHT CLICK §7to select the §e§lARCHER §7Class!", "", "§7§m---------------------"));

		b.setDisplayName("§e§lBARD CLASS");
		b.setLore(Arrays.asList("§7§m---------------------", "", "§e§lBARD §7Class!", "", "§e§lRIGHT CLICK §7to select the §e§lBARD §7Class!", "", "§7§m---------------------"));
		
		r.setDisplayName("§e§lROGUE CLASS");
		r.setLore(Arrays.asList("§7§m---------------------", "", "§e§lROGUE §7Class!", "", "§e§lRIGHT CLICK §7to select the §e§lROGUE §7Class!", "", "§7§m---------------------"));
		
		
		//===================== Setting item metas =====================
		diamond.setItemMeta(d);
		archer.setItemMeta(a);
		bard.setItemMeta(b);
		rogue.setItemMeta(r);
		
		// ===================== Setting items =====================
		inv.setItem(10, diamond);
		inv.setItem(12, archer);
		inv.setItem(14, bard);
		inv.setItem(16, rogue);
		
		player.openInventory(inv);
	}

}
