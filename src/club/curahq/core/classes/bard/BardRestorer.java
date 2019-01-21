package club.curahq.core.classes.bard;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import club.curahq.core.Core;
import club.curahq.core.classes.event.PvpClassUnequipEvent;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class BardRestorer
  implements Listener
{
  private final Table<UUID, PotionEffectType, PotionEffect> restores;
  
  public BardRestorer(Core plugin)
  {
    this.restores = HashBasedTable.create();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPvpClassUnequip(PvpClassUnequipEvent event)
  {
    this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
  }
  
  public void setRestoreEffect(Player player, PotionEffect effect) {
      boolean shouldCancel = true;
      Collection<PotionEffect> activeList = player.getActivePotionEffects();
      for (PotionEffect active : activeList) {
          if (!active.getType().equals(effect.getType()))
              continue;

          // If the current potion effect has a higher amplifier, ignore this one.
          if (effect.getAmplifier() < active.getAmplifier()) {
              return;
          } else if (effect.getAmplifier() == active.getAmplifier()) {
              // If the current potion effect has a longer duration, ignore this one.
              if (effect.getDuration() < active.getDuration()) {
                  return;
              }
          }

          restores.put(player.getUniqueId(), active.getType(), active);
          shouldCancel = false;
          break;
      }

      // Cancel the previous restore.
      //NEED FIXING
      player.addPotionEffect(effect, true);
      if (shouldCancel && effect.getDuration() > BardClass.HELD_EFFECT_DURATION_TICKS && effect.getDuration() < BardClass.DEFAULT_MAX_DURATION) {
          restores.remove(player.getUniqueId(), effect.getType());
      }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPotionEffectExpire(PotionEffectExpireEvent event)
  {
    LivingEntity livingEntity = event.getEntity();
    if ((livingEntity instanceof Player))
    {
      Player player = (Player)livingEntity;
      PotionEffect previous = (PotionEffect)this.restores.remove(player.getUniqueId(), event.getEffect().getType());
      if (previous != null)
      {
        event.setCancelled(true);
        player.addPotionEffect(previous, true);
      }
    }
  }
}
