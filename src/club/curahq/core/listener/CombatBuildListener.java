package club.curahq.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import club.curahq.core.Core;
import club.curahq.core.util.core.ConfigUtil;

public class CombatBuildListener implements Listener{

	@EventHandler
	public void onBlock(BlockPlaceEvent e) {
		if(ConfigUtil.isBuildingInCombat == false && Core.getInstance().getTimerManager().getCombatTimer().getRemaining(e.getPlayer()) > 0L) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cYou can not build in §c§lSpawn Tag §c[" + 
			Core.getRemaining(Core.getInstance().getTimerManager().getCombatTimer().getRemaining(e.getPlayer()), true) + "]");
		}
	}
	
	@EventHandler
	public void onBlock(BlockBreakEvent e) {
		if(ConfigUtil.isBuildingInCombat == false && Core.getInstance().getTimerManager().getCombatTimer().getRemaining(e.getPlayer()) > 0L) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cYou can not break in §c§lSpawn Tag §c[" + 
			Core.getRemaining(Core.getInstance().getTimerManager().getCombatTimer().getRemaining(e.getPlayer()), true) + "]");
		}
	}
}
