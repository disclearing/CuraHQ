package club.curahq.core.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.curahq.core.commands.staff.StaffModeCommand;

public class StaffItems {

	@SuppressWarnings("deprecation")
	public static void modItems(Player p) {
		Inventory inv = p.getInventory();

		inv.clear();

		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemStack book = new ItemStack(Material.BOOK);
		ItemStack tp = new ItemStack(397, 1, (short) 3);
		ItemStack vanish = new ItemStack(351, 1, (short) 10);
		ItemStack carpet = new ItemStack(171, 1, (short) 15);

		ItemMeta compassMeta = compass.getItemMeta();
		ItemMeta bookMeta = book.getItemMeta();
		ItemMeta eggMeta = tp.getItemMeta();
		ItemMeta vanishMeta = vanish.getItemMeta();
		ItemMeta carpetMeta = carpet.getItemMeta();

		compassMeta.setDisplayName("§eZoom");
		bookMeta.setDisplayName("§eInventory Inspector");
		eggMeta.setDisplayName("§eFind Player");
		vanishMeta.setDisplayName("§eVanish: §aOn");
		carpetMeta.setDisplayName("§eView");

		compass.setItemMeta(compassMeta);
		book.setItemMeta(bookMeta);
		tp.setItemMeta(eggMeta);
		vanish.setItemMeta(vanishMeta);
		carpet.setItemMeta(carpetMeta);

		inv.setItem(0, compass);
		inv.setItem(1, book);
		inv.setItem(2, carpet);
		inv.setItem(7, tp);
		inv.setItem(8, vanish);
	}

	public static void saveInventory(Player p) {
		StaffModeCommand.armorContents.put(p.getName(), p.getInventory().getArmorContents());
		StaffModeCommand.inventoryContents.put(p.getName(), p.getInventory().getContents());
	}

	public static void loadInventory(Player p) {
		p.getInventory().clear();

		p.getInventory().setContents((ItemStack[]) StaffModeCommand.inventoryContents.get(p.getName()));
		p.getInventory().setArmorContents((ItemStack[]) StaffModeCommand.armorContents.get(p.getName()));

		StaffModeCommand.inventoryContents.remove(p.getName());
		StaffModeCommand.armorContents.remove(p.getName());
	}

}
