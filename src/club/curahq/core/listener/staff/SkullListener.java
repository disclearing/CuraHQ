package club.curahq.core.listener.staff;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.apache.commons.lang3.text.WordUtils;

public class SkullListener implements Listener {
    private static final String KILL_BEHEAD_PERMISSION = "kill.behead";

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        if(killer != null && killer.hasPermission(KILL_BEHEAD_PERMISSION)) {
            final ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            final SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(player.getName());
            skull.setItemMeta((ItemMeta) meta);
            event.getDrops().add(skull);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = event.getPlayer();
            final BlockState state = event.getClickedBlock().getState();
            if(state instanceof Skull) {
                final Skull skull = (Skull) state;
                player.sendMessage(ChatColor.YELLOW + "This head belongs to " + ChatColor.WHITE + ((skull.getSkullType() == SkullType.PLAYER && skull.hasOwner()) ? skull.getOwner() : ("a " + WordUtils.capitalizeFully(skull.getSkullType().name()) + " skull")) + ChatColor.YELLOW + '.');
            }
        }
    }
}
