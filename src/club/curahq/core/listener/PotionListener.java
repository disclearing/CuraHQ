package club.curahq.core.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class PotionListener implements Listener {
	@SuppressWarnings("incomplete-switch")
	@EventHandler
	public void onBrew(BrewEvent e) {
		switch (e.getContents().getIngredient().getType()) {
		case SPECKLED_MELON:
		case STATIONARY_LAVA:
			e.setCancelled(true);
			break;
		case RECORD_4:
		case RED_MUSHROOM:
			if (contains(e.getContents().getContents(), PotionType.POISON)) {
				e.setCancelled(true);
			} else if (contains(e.getContents().getContents(), new ItemStack(Material.POTION))) {
				e.setCancelled(true);
			}
			break;
		case STAINED_GLASS_PANE:
			if (contains(e.getContents().getContents(), new ItemStack(Material.POTION))) {
				e.setCancelled(true);
			} else if (contains(e.getContents().getContents(), PotionType.INSTANT_HEAL)) {
				e.setCancelled(true);
			} else if (contains(e.getContents().getContents(), PotionType.POISON)) {
				e.setCancelled(true);
			} else if (contains(e.getContents().getContents(), new ItemStack(Material.POTION, 1, (short) 16))) {
				e.setCancelled(true);
			}
			break;
		case STONE_AXE:
			if (contains(e.getContents().getContents(), new ItemStack(Material.POTION))) {
				e.setCancelled(true);
			}
			break;
		}
	}

	private boolean contains(ItemStack[] toSearch, ItemStack toFind) {
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = toSearch).length;
		for (int i = 0; i < j; i++) {
			ItemStack itemStack = arrayOfItemStack[i];
			if ((itemStack != null) && (toFind.isSimilar(itemStack))) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(ItemStack[] toSearch, PotionType potionType) {
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = toSearch).length;
		for (int i = 0; i < j; i++) {
			ItemStack itemStack = arrayOfItemStack[i];
			try {
				Potion p = Potion.fromItemStack(itemStack);
				if (p.getType() == potionType) {
					return true;
				}
			} catch (Exception localException) {
			}
		}
		return false;
	}
}