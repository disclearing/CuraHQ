package club.curahq.core.sotw;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import club.curahq.core.Core;
import club.curahq.core.util.core.ConfigUtil;

public class SotwListener implements Listener {

    private final Core plugin;

    public SotwListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final Player oponent = (Player)e.getDamager();
            if (this.plugin.getSotwTimer().getSotwRunnable() != null && SotwCommand.enabled.contains(oponent.getUniqueId()) && !SotwCommand.enabled.contains(player.getUniqueId())) {
                oponent.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou are not permitted to hit this player, they do not have their §a§lSOTW §cpaused.");
                e.setCancelled(true);
            }
            else if (this.plugin.getSotwTimer().getSotwRunnable() != null && !SotwCommand.enabled.contains(oponent.getUniqueId()) && SotwCommand.enabled.contains(player.getUniqueId())) {
                oponent.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou are not permitted to hit §7" + player.getName() + ".");
                e.setCancelled(true);
            }
            else if (this.plugin.getSotwTimer().getSotwRunnable() != null  && !SotwCommand.enabled.contains(oponent.getUniqueId()) && !SotwCommand.enabled.contains(player.getUniqueId())) {
            	oponent.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou cannot hit players whilst sotw is active if you would like to execute §7/sotw enable.");
            	e.setCancelled(true);
            }
           
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.plugin.getSotwTimer().getSotwRunnable() != null) {
            final Player player = (Player)event.getEntity();
            if (SotwCommand.enabled.contains(player.getUniqueId())) {
                event.setCancelled(false);
                return;
            }
            if (event.getCause() != EntityDamageEvent.DamageCause.SUICIDE && this.plugin.getSotwTimer().getSotwRunnable() != null) {
                event.setCancelled(true);
            }
        }
    }
}


