package club.curahq.core.classes.archer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.classes.PvpClass;
import club.curahq.core.util.core.Cooldowns;

public class ArcherClass
  extends PvpClass
  implements Listener
{
  public ArcherClass(Core plugin)
  {
    super("Archer", TimeUnit.SECONDS.toMillis(1L));
    this.archerSpeedCooldowns = new TObjectLongHashMap<UUID>();
    this.plugin = plugin;
    this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
  }
  
  public boolean onEquip(Player player)
  {
    return super.onEquip(player);
  }
  
  @EventHandler
  public void onPlayerClickSugar(PlayerInteractEvent e)
  {
    final Player p = e.getPlayer();
    if ((this.plugin.getPvpClassManager().getEquippedClass(p) != null) && (this.plugin.getPvpClassManager().getEquippedClass(p).equals(this)) && (p.getItemInHand().getType() == Material.SUGAR))
    {
      if (Cooldowns.isOnCooldown("Archer_item_cooldown", p))
      {
        p.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + Core.getRemaining(Cooldowns.getCooldownForPlayerLong("Archer_item_cooldown", p), true) + ChatColor.RED + '.');
        e.setCancelled(true);
        return;
      }
      Cooldowns.addCooldown("Archer_item_cooldown", p, ARCHER_SPEED_COOLDOWN_DELAY);
      if (p.getItemInHand().getAmount() == 1) {
        p.getInventory().remove(p.getItemInHand());
      } else {
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
      }
      p.removePotionEffect(PotionEffectType.SPEED);
      p.addPotionEffect(ARCHER_SPEED_EFFECT);
      new BukkitRunnable()
      {
        public void run()
        {
          if (ArcherClass.this.isApplicableFor(p))
          {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
          }
        }
      }.runTaskLater(this.plugin, 180L);
    }
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e)
  {
    if (TAGGED.containsKey(e.getPlayer().getUniqueId())) {
      TAGGED.remove(e.getPlayer().getUniqueId());
    }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onEntityDamage(EntityDamageByEntityEvent event)
  {
    Entity entity = event.getEntity();
    Entity damager = event.getDamager();
    if (((entity instanceof Player)) && ((damager instanceof Arrow)))
    {
      Arrow arrow = (Arrow)damager;
      ProjectileSource source = arrow.getShooter();
      if ((source instanceof Player))
      {
        Player damaged = (Player)event.getEntity();
        Player shooter = (Player)source;
        PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
        if ((equipped == null) || (!equipped.equals(this))) {
          return;
        }
        if (this.plugin.getTimerManager().archerTimer.getRemaining((Player)entity) == 0L)
        {
          if ((this.plugin.getPvpClassManager().getEquippedClass(damaged) != null) && (this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this))) {
            return;
          }
          this.plugin.getTimerManager().archerTimer.setCooldown((Player)entity, entity.getUniqueId());
          TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
          List<Player> onlinePlayers = new ArrayList<Player>();
          Player[] arrayOfPlayer;
          int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
          for (int i = 0; i < j; i++)
          {
            Player player = arrayOfPlayer[i];
            onlinePlayers.add(player);
            this.plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(onlinePlayers);
          }
          double distance = shooter.getLocation().distance(damaged.getLocation());
          double round = Math.round(distance*100) / 100;
          shooter.sendMessage(ChatColor.YELLOW + "You marked " + ChatColor.BOLD + damaged.getName() + " for 10 seconds " + ChatColor.GRAY + "[distance: " + round + ChatColor.GRAY + ", damage: " + ChatColor.RED + event.getDamage() + ChatColor.GRAY + "]");
          damaged.sendMessage(ChatColor.RED + "Archer Marked! " + ChatColor.YELLOW + "You have been Archer Tagged, any damage you take by a player will be increased by 25%");
        }
      }
    }
  }
  
  public boolean isApplicableFor(Player player)
  {
    PlayerInventory playerInventory = player.getInventory();
    ItemStack helmet = playerInventory.getHelmet();
    if ((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)) {
      return false;
    }
    ItemStack chestplate = playerInventory.getChestplate();
    if ((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)) {
      return false;
    }
    ItemStack leggings = playerInventory.getLeggings();
    if ((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)) {
      return false;
    }
    ItemStack boots = playerInventory.getBoots();
    return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
  }
  
  public static final HashMap<UUID, UUID> TAGGED = new HashMap<UUID, UUID>();
  private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 180, 3);
  private static final int ARCHER_SPEED_COOLDOWN_DELAY = 60;
  public final TObjectLongMap<UUID> archerSpeedCooldowns;
  private final Core plugin;
}
