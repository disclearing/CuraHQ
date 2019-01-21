package club.curahq.core.classes.type;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.classes.PvpClass;
import club.curahq.core.classes.event.PvpClassUnequipEvent;
import club.curahq.core.util.core.Cooldowns;

public class AssassinClass
  extends PvpClass
  implements Listener
{
  private final Core plugin;
  public HashMap<String, Integer> firstAssassinEffects;
  
  public AssassinClass(Core plugin)
  {
    super("Reaper", TimeUnit.SECONDS.toMillis(3L));
    this.firstAssassinEffects = new HashMap<String, Integer>();
    this.plugin = plugin;
    this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
  }
  
  @SuppressWarnings({ "deprecation", "unlikely-arg-type" })
@EventHandler
  public void onUnEquip(PvpClassUnequipEvent e)
  {
    Player p = e.getPlayer();
    Player[] arrayOfPlayer;
    int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
    for (int i = 0; i < j; i++)
    {
      Player on = arrayOfPlayer[i];
      if ((!on.canSee(p)) && (!on.hasPermission("base.command.vanish"))) {
        on.showPlayer(p);
      }
    }
    this.firstAssassinEffects.remove(p);
  }
  
  @EventHandler
  public void onDamageSelf(EntityDamageEvent e)
  {
    if ((e.getEntity() instanceof Player))
    {
      Player p = (Player)e.getEntity();
      if ((this.plugin.getPvpClassManager().getEquippedClass(p) == null) || (!this.plugin.getPvpClassManager().getEquippedClass(p).equals(this))) {
        return;
      }
      if ((this.firstAssassinEffects.containsKey(p.getName())) && (((Integer)this.firstAssassinEffects.get(p.getName())).intValue() == 1)) {
        for (Entity entity : p.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
          if ((entity instanceof Player))
          {
            Player players = (Player)entity;
            players.sendMessage(ChatColor.YELLOW + "A reaper has taken damage in stealth mode near you: " + ChatColor.GRAY + ChatColor.ITALIC + "(20 x 20)");
          }
        }
      }
    }
  }
  
  @EventHandler
  public void onHitOtherPlayers(EntityDamageByEntityEvent e)
  {
    if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player)))
    {
      Player p = (Player)e.getDamager();
      e.getEntity();
      if ((this.firstAssassinEffects.containsKey(p.getName())) && (((Integer)this.firstAssassinEffects.get(p.getName())).intValue() == 1)) {
        afterFiveSeconds(p, true);
      }
    }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler
  public void onClickItem(PlayerInteractEvent e)
  {
    final Player p = e.getPlayer();
    if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.RIGHT_CLICK_AIR))
    {
      PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(p);
      if ((equipped == null) || (!equipped.equals(this))) {
        return;
      }
      if (p.getItemInHand().getType() == Material.QUARTZ)
      {
        if (Cooldowns.isOnCooldown("Assassin_item_cooldown", p))
        {
          p.sendMessage(ChatColor.RED + "You still have an " + ChatColor.GREEN + ChatColor.BOLD + "Reaper" + ChatColor.RED + " cooldown for another " + Core.getRemaining(Cooldowns.getCooldownForPlayerLong("Assassin_item_cooldown", p), true) + ChatColor.RED + '.');
          return;
        }
        if (p.getItemInHand().getAmount() == 1) {
          p.getInventory().remove(p.getItemInHand());
        }
        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
        p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GRAY + "Stealth" + ChatColor.YELLOW + " Mode");
        Player[] arrayOfPlayer;
        int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
        for (int i = 0; i < j; i++)
        {
          Player on = arrayOfPlayer[i];
          on.playEffect(p.getLocation().add(0.5D, 2.0D, 0.5D), Effect.ENDER_SIGNAL, 5);
          on.playEffect(p.getLocation().add(0.5D, 1.5D, 0.5D), Effect.ENDER_SIGNAL, 5);
          on.playEffect(p.getLocation().add(0.5D, 1.0D, 0.5D), Effect.ENDER_SIGNAL, 5);
          on.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
          if (!on.hasPermission("base.command.vanish")) {
            on.hidePlayer(p);
          }
        }
        Cooldowns.addCooldown("Assassin_item_cooldown", p, 60);
        p.removePotionEffect(PotionEffectType.SPEED);
        this.firstAssassinEffects.put(p.getName(), Integer.valueOf(1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0), true);
        new BukkitRunnable()
        {
          public void run()
          {
            if ((AssassinClass.this.isApplicableFor(p)) && (AssassinClass.this.firstAssassinEffects.containsKey(p.getName())) && (((Integer)AssassinClass.this.firstAssassinEffects.get(p.getName())).intValue() == 1)) {
              AssassinClass.this.afterFiveSeconds(p, false);
            }
          }
        }.runTaskLater(this.plugin, 100L);
      }
    }
  }
  
  @SuppressWarnings("deprecation")
public void afterFiveSeconds(final Player p, boolean force)
  {
    if ((this.firstAssassinEffects.containsKey(p.getName())) && (isApplicableFor(p)))
    {
      Player[] arrayOfPlayer;
      int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
      for (int i = 0; i < j; i++)
      {
        Player on = arrayOfPlayer[i];
        if ((!on.canSee(p)) && (!on.hasPermission("base.command.vanish"))) {
          on.showPlayer(p);
        }
        on.playEffect(p.getLocation().add(0.0D, 2.0D, 0.0D), Effect.ENDER_SIGNAL, 3);
        on.playEffect(p.getLocation().add(0.0D, 1.5D, 0.0D), Effect.ENDER_SIGNAL, 3);
        on.playEffect(p.getLocation().add(0.0D, 1.0D, 0.0D), Effect.ENDER_SIGNAL, 3);
        on.playEffect(p.getLocation().add(0.0D, 2.0D, 0.0D), Effect.BLAZE_SHOOT, 5);
        on.playEffect(p.getLocation().add(0.0D, 1.5D, 0.0D), Effect.BLAZE_SHOOT, 5);
        on.playEffect(p.getLocation().add(0.0D, 1.0D, 0.0D), Effect.BLAZE_SHOOT, 5);
      }
      new BukkitRunnable()
      {
        public void run()
        {
          if ((AssassinClass.this.firstAssassinEffects.containsKey(p.getName())) && (((Integer)AssassinClass.this.firstAssassinEffects.get(p.getName())).intValue() == 2))
          {
            AssassinClass.this.firstAssassinEffects.remove(p.getName());
            p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GREEN + "Normal" + ChatColor.YELLOW + " Mode");
            if (AssassinClass.this.isApplicableFor(p))
            {
              p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
              p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
              p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
            }
          }
        }
      }.runTaskLater(this.plugin, 100L);
      if (force)
      {
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.firstAssassinEffects.remove(p.getName());
        this.firstAssassinEffects.put(p.getName(), Integer.valueOf(2));
        p.sendMessage(ChatColor.YELLOW + "You have been forced into " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
        return;
      }
      this.firstAssassinEffects.remove(p.getName());
      this.firstAssassinEffects.put(p.getName(), Integer.valueOf(2));
      p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
      p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
      p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
      p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
      p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
      p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
      p.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
  }
  
  public boolean isApplicableFor(Player player)
  {
    PlayerInventory playerInventory = player.getInventory();
    ItemStack helmet = playerInventory.getHelmet();
    if ((helmet == null) || (helmet.getType() != Material.CHAINMAIL_HELMET)) {
      return false;
    }
    ItemStack chestplate = playerInventory.getChestplate();
    if ((chestplate == null) || (chestplate.getType() != Material.CHAINMAIL_CHESTPLATE)) {
      return false;
    }
    ItemStack leggings = playerInventory.getLeggings();
    if ((leggings == null) || (leggings.getType() != Material.CHAINMAIL_LEGGINGS)) {
      return false;
    }
    ItemStack boots = playerInventory.getBoots();
    return (boots != null) && (boots.getType() == Material.CHAINMAIL_BOOTS);
  }
}
