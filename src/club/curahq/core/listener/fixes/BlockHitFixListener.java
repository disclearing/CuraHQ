package club.curahq.core.listener.fixes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import club.curahq.core.util.BukkitUtils;

public class BlockHitFixListener
  implements Listener
{
	@SuppressWarnings("unused")
	private static final long THRESHOLD = 850L;
	private final Map<UUID, Long> lastInteractTimes = new HashMap<UUID, Long>();
  
  
  @EventHandler(ignoreCancelled=false, priority=EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if ((event.hasBlock()) && (event.getAction() != Action.PHYSICAL) && (NON_TRANSPARENT_ATTACK_INTERACT_TYPES.contains(event.getClickedBlock().getType()))) {
      cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
    }
  }
  
  @EventHandler(ignoreCancelled=false, priority=EventPriority.MONITOR)
  public void onBlockBreak(BlockBreakEvent event)
  {
    if ((event.isCancelled()) && (NON_TRANSPARENT_ATTACK_BREAK_TYPES.contains(event.getBlock().getType()))) {
      cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageEvent event)
  {
    Player attacker = BukkitUtils.getFinalAttacker(event, true);
    if (attacker != null)
    {
      Long lastInteractTime = (Long)this.lastInteractTimes.get(attacker.getUniqueId());
      if ((lastInteractTime != null) && (lastInteractTime.longValue() - System.currentTimeMillis() > 0L)) {
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerLogout(PlayerQuitEvent event)
  {
    this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event)
  {
    this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
  }
  
  public void cancelAttackingMillis(UUID uuid, long delay)
  {
    this.lastInteractTimes.put(uuid, Long.valueOf(System.currentTimeMillis() + delay));
  }
  
  private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_BREAK_TYPES = Sets.immutableEnumSet(Material.GLASS, new Material[] { Material.STAINED_GLASS, Material.STAINED_GLASS_PANE });
  private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_INTERACT_TYPES = Sets.immutableEnumSet(Material.IRON_DOOR_BLOCK, new Material[] { Material.IRON_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR });
}
