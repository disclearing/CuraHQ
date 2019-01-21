package club.curahq.core.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public abstract class PvpClass
{
  public PvpClass(String name, long warmupDelay)
  {
    this.passiveEffects = new HashSet<PotionEffect>();
    this.name = name;
    this.warmupDelay = warmupDelay;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public long getWarmupDelay()
  {
    return this.warmupDelay;
  }
  
  public boolean onEquip(Player player)
  {
    for (PotionEffect effect : this.passiveEffects) {
      player.addPotionEffect(effect, true);
    }
    player.sendMessage(ChatColor.YELLOW + "Class " + ChatColor.GOLD + this.name + ChatColor.YELLOW + " has been equipped.");
    return true;
  }
  
  public void onUnequip(Player player)
  {
    for (PotionEffect effect : this.passiveEffects) {
      for (PotionEffect active : player.getActivePotionEffects()) {
        if ((active.getDuration() > DEFAULT_MAX_DURATION) && (active.getType().equals(effect.getType())) && (active.getAmplifier() == effect.getAmplifier()))
        {
          player.removePotionEffect(effect.getType());
          break;
        }
      }
    }
    player.sendMessage(ChatColor.YELLOW + "Class " + ChatColor.GOLD + this.name + ChatColor.YELLOW + " has been un-equipped.");
  }
  
  public static final long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
  protected final Set<PotionEffect> passiveEffects;
  protected final String name;
  protected final long warmupDelay;
  
  public abstract boolean isApplicableFor(Player paramPlayer);
}
