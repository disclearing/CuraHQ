package club.curahq.core.kitmap;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SelectorListener implements Listener{
	
	
	Selector selec = new Selector();
	ItemStack selector = new ItemStack(Material.BOOK);
	String prefix = "§6§lKIT SELECTOR §7» ";
	ItemMeta s = selector.getItemMeta();

	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(!e.getPlayer().hasPlayedBefore()) {
			s.setDisplayName("§eKIT SELECTOR");
			s.setLore(Arrays.asList("§7§m---------------------", "", "§e§lRIGHT CLICK §7to select a §e§lKIT§7!", "", "§7§m---------------------"));
			selector.setItemMeta(s);
			
			e.getPlayer().getInventory().clear();
			e.getPlayer().getInventory().setItem(4, selector);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		s.setDisplayName("§eKIT SELECTOR");
		s.setLore(Arrays.asList("§7§m---------------------", "", "§e§lRIGHT CLICK §7to select a §e§lKIT§7!", "", "§7§m---------------------"));
		selector.setItemMeta(s);
		
		e.getPlayer().getInventory().clear();
		e.getPlayer().getInventory().setItem(4, selector);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getPlayer().getItemInHand() == null) {
			return;
		}
		
		if(e.getPlayer().getItemInHand().getItemMeta() == null) {
			return;
		}
		
		if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName()  == null) {
			return;
		}
		
		if(!e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§eKIT SELECTOR")) {
			return;
		}
		if((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (e.getItem() != null) && ((e.getItem().getItemMeta().getDisplayName().equals("§eKIT SELECTOR"))))) {
			selec.open(e.getPlayer());
			e.getPlayer().sendMessage(prefix + "§7You have opened the §e§lKIT SELECTOR§7!");
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if(e.getInventory() == null) {
			return;
		}
		if(e.getCurrentItem() == null) {
			return;
		}
		if(e.getCurrentItem().getType().equals(Material.AIR)) {
			return;
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName() == null) {
			return;
		}
		if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e§lDIAMOND CLASS")) {
			Kit.giveDiamondKit(player);
			player.sendMessage(prefix + "§7You have selected the §e§lDIAMOND CLASS");
			

			player.closeInventory();

		} else {
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e§lARCHER CLASS")) {
				Kit.giveArcherKit(player);
				player.sendMessage(prefix + "§7You have selected the §e§lARCHER CLASS");
				

				player.closeInventory();

			
		} else {
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e§lBARD CLASS")) {
				Kit.giveBardKit(player);
				player.sendMessage(prefix + "§7You have selected the §e§lBARD CLASS");
				

				player.closeInventory();

			} else {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e§lROGUE CLASS")) {
					Kit.giveRogueKit(player);
					player.sendMessage(prefix + "§7You have selected the §e§lROGUE CLASS");
					
					player.closeInventory();
					}
				}
			}
		}
		e.setCancelled(true);
	}
}
