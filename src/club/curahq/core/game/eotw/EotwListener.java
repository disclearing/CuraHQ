package club.curahq.core.game.eotw;


import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import club.curahq.core.Core;
import club.curahq.core.faction.event.FactionClaimChangeEvent;
import club.curahq.core.faction.event.cause.ClaimChangeCause;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;

public class EotwListener implements Listener {
    private final Core plugin;

    public EotwListener(final Core plugin) {
        this.plugin = plugin;
    }


    @SuppressWarnings("incomplete-switch")
	@EventHandler
    public void onMobSpawnFromSpawner(CreatureSpawnEvent e){
        if(plugin.getEotwHandler().isEndOfTheWorld()) {
            switch (e.getSpawnReason()) {
                case SPAWNER: {
                    if (e.getEntity().getType() != EntityType.PIG) {
                        e.setCancelled(true);
                    }
                }
                case SPAWNER_EGG: {
                    if (e.getEntity().getType() != EntityType.PIG) {
                        e.setCancelled(true);
                    }
                }
                case DISPENSE_EGG: {
                    if (e.getEntity().getType() != EntityType.PIG) {
                        e.setCancelled(true);
                    }
                }
            }
        }

    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(final FactionClaimChangeEvent event) {
        if(this.plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM) {
            final Faction faction = event.getClaimableFaction();
            if(faction instanceof PlayerFaction) {
                event.setCancelled(true);
                event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
            }
        }
    }
}
