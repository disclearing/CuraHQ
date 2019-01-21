package club.curahq.core.classes.bard;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.classes.PvpClass;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;

public class BardClass
  extends PvpClass
  implements Listener
{
  public static final int HELD_EFFECT_DURATION_TICKS = 100;
  
  public BardClass(Core plugin)
  {
    super("Bard", TimeUnit.SECONDS.toMillis(1L));
    this.bardDataMap = new HashMap<UUID, BardData>();
    this.bardEffects = new EnumMap<Material, BardEffect>(Material.class);
    this.msgCooldowns = new TObjectLongHashMap<UUID>();
    this.plugin = plugin;
    this.bardRestorer = new BardRestorer(plugin);
    this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    this.bardEffects.put(Material.SUGAR, new BardEffect(45, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
    this.bardEffects.put(Material.BLAZE_POWDER, new BardEffect(45, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0), null));
    this.bardEffects.put(Material.IRON_INGOT, new BardEffect(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
    this.bardEffects.put(Material.GHAST_TEAR, new BardEffect(30, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
    this.bardEffects.put(Material.FEATHER, new BardEffect(40, new PotionEffect(PotionEffectType.JUMP, 120, 3), new PotionEffect(PotionEffectType.JUMP, 100, 1)));
    this.bardEffects.put(Material.SPIDER_EYE, new BardEffect(55, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
    this.bardEffects.put(Material.MAGMA_CREAM, new BardEffect(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
  }
  
  public boolean onEquip(final Player player)
  {
    if (!super.onEquip(player)) {
      return false;
    }
    if  (BardClass.this.plugin.getTimerManager().invincibilityTimer.getRemaining(player.getUniqueId()) > 0L) {
    	player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it first.");
    	return false;
    }
    BardData bardData = new BardData();
    this.bardDataMap.put(player.getUniqueId(), bardData);
    bardData.startEnergyTracking();
    bardData.heldTask = new BukkitRunnable()
    {
      int lastEnergy;
      
      @SuppressWarnings("deprecation")
	public void run()
      {
        ItemStack held = player.getItemInHand();
        if (held != null)
        {
          BardEffect bardEffect = (BardEffect)BardClass.this.bardEffects.get(held.getType());
          if ((bardEffect != null) && (!BardClass.this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()))
          {
            PlayerFaction playerFaction = BardClass.this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null)
            {
              Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
              for (Entity nearby : nearbyEntities) {
                if (((nearby instanceof Player)) && (!player.equals(nearby)))
                {
                  Player target = (Player)nearby;
                  if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
                    BardClass.this.bardRestorer.setRestoreEffect(target, bardEffect.heldable);
                    
                    //NEED FIXING
                  }
                }
              }
            }
          }
        }
        int energy = (int)BardClass.this.getEnergy(player);
        if ((energy != 0) && (energy != this.lastEnergy) && ((energy % 10 == 0) || (this.lastEnergy - energy - 1 > 0) || (energy == 120.0D)))
        {
          this.lastEnergy = energy;
          player.sendMessage(ChatColor.YELLOW + BardClass.this.name + " Energy: " + ChatColor.GOLD + energy);
        }
      }
    }.runTaskTimer(this.plugin, 0L, 20L);
    return true;
  }
  
  public void onUnequip(Player player)
  {
    super.onUnequip(player);
    clearBardData(player.getUniqueId());
  }
  
  private void clearBardData(UUID uuid)
  {
    BardData bardData = (BardData)this.bardDataMap.remove(uuid);
    if ((bardData != null) && (bardData.heldTask != null)) {
      bardData.heldTask.cancel();
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    clearBardData(event.getPlayer().getUniqueId());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerKick(PlayerKickEvent event)
  {
    clearBardData(event.getPlayer().getUniqueId());
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onItemHeld(PlayerItemHeldEvent event)
  {
    Player player = event.getPlayer();
    PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
    if ((equipped == null) || (!equipped.equals(this))) {
      return;
    }
    UUID uuid = player.getUniqueId();
    long lastMessage = this.msgCooldowns.get(uuid);
    long millis = System.currentTimeMillis();
    if ((lastMessage != this.msgCooldowns.getNoEntryValue()) && (lastMessage - millis > 0L)) {
      return;
    }
    player.getInventory().getItem(event.getNewSlot());

  }
  
  @SuppressWarnings("deprecation")
@EventHandler(ignoreCancelled=false, priority=EventPriority.MONITOR)
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    if (!event.hasItem()) {
      return;
    }
    Action action = event.getAction();
    if ((action == Action.RIGHT_CLICK_AIR) || ((!event.isCancelled()) && (action == Action.RIGHT_CLICK_BLOCK)))
    {
      ItemStack stack = event.getItem();
      BardEffect bardEffect = (BardEffect)this.bardEffects.get(stack.getType());
      if ((bardEffect == null) || (bardEffect.clickable == null)) {
        return;
      }
      event.setUseItemInHand(Result.DENY);
      Player player = event.getPlayer();
      BardData bardData = (BardData)this.bardDataMap.get(player.getUniqueId());
      if (bardData != null)
      {
        if (!canUseBardEffect(player, bardData, bardEffect, true)) {
          return;
        }
        if (stack.getAmount() > 1) {
          stack.setAmount(stack.getAmount() - 1);
        } else {
          player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
        if ((bardEffect != null) && (!this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()))
        {
          PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
          if ((playerFaction != null) && (!bardEffect.clickable.getType().equals(PotionEffectType.WITHER)))
          {
            Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
            for (Entity nearby : nearbyEntities) {
              if (((nearby instanceof Player)) && (!player.equals(nearby)))
              {
                Player target = (Player)nearby;
                if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
                  this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                }
              }
            }
          }
          else if ((playerFaction != null) && (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)))
          {
            Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
            for (Entity nearby : nearbyEntities) {
              if (((nearby instanceof Player)) && (!player.equals(nearby)))
              {
                Player target = (Player)nearby;
                if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
                  this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                }
              }
            }
          }
          else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER))
          {
            Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);
            for (Entity nearby : nearbyEntities) {
              if (((nearby instanceof Player)) && (!player.equals(nearby)))
              {
                Player target = (Player)nearby;
                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
              }
            }
          }
        }
        if ((bardEffect.clickable.getType().equals(PotionEffectType.INCREASE_DAMAGE)))
        {
          this.bardRestorer.setRestoreEffect(player, bardEffect.clickable);
          player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1, 0));
        }
        this.bardRestorer.setRestoreEffect(player, bardEffect.clickable);
        setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
        bardData.buffCooldown = (System.currentTimeMillis() + BUFF_COOLDOWN_MILLIS);
      }
    }
  }
  
  private boolean canUseBardEffect(Player player, BardData bardData, BardEffect bardEffect, boolean sendFeedback)
  {
    String errorFeedback = null;
    double currentEnergy = bardData.getEnergy();
    if (bardEffect.energyCost > currentEnergy) {
      errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD + currentEnergy + ChatColor.RED + '.';
    }
    long remaining = bardData.getRemainingBuffDelay();
    if (remaining > 0L) {
      errorFeedback = ChatColor.RED + "You still have a cooldown on this " + ChatColor.GREEN + ChatColor.BOLD + "Bard" + ChatColor.RED + " buff for another " + Core.getRemaining(remaining, true, false) + ChatColor.RED + '.';
    }
    Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
    if (factionAt.isSafezone()) {
      errorFeedback = ChatColor.RED + "Bard Buffs are disabled in safe-zones.";
    }
    if ((sendFeedback) && (errorFeedback != null)) {
      player.sendMessage(errorFeedback);
    }
    return errorFeedback == null;
  }
  
  public boolean isApplicableFor(Player player)
  {
    ItemStack helmet = player.getInventory().getHelmet();
    if ((helmet == null) || (helmet.getType() != Material.GOLD_HELMET)) {
      return false;
    }
    ItemStack chestplate = player.getInventory().getChestplate();
    if ((chestplate == null) || (chestplate.getType() != Material.GOLD_CHESTPLATE)) {
      return false;
    }
    ItemStack leggings = player.getInventory().getLeggings();
    if ((leggings == null) || (leggings.getType() != Material.GOLD_LEGGINGS)) {
      return false;
    }
    ItemStack boots = player.getInventory().getBoots();
    return (boots != null) && (boots.getType() == Material.GOLD_BOOTS);
  }
  
  public long getRemainingBuffDelay(Player player)
  {
    synchronized (this.bardDataMap)
    {
      BardData bardData = (BardData)this.bardDataMap.get(player.getUniqueId());
      return bardData == null ? 0L : bardData.getRemainingBuffDelay();
    }
  }
  
  public double getEnergy(Player player)
  {
    synchronized (this.bardDataMap)
    {
      BardData bardData = (BardData)this.bardDataMap.get(player.getUniqueId());
      return bardData == null ? 0.0D : bardData.getEnergy();
    }
  }
  
  public long getEnergyMillis(Player player)
  {
    synchronized (this.bardDataMap)
    {
      BardData bardData = (BardData)this.bardDataMap.get(player.getUniqueId());
      return bardData == null ? 0L : bardData.getEnergyMillis();
    }
  }
  
  public double setEnergy(Player player, double energy)
  {
    BardData bardData = (BardData)this.bardDataMap.get(player.getUniqueId());
    if (bardData == null) {
      return 0.0D;
    }
    bardData.setEnergy(energy);
    return bardData.getEnergy();
  }
  
  private static final long BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(8L);
  private final Map<UUID, BardData> bardDataMap;
  private final Map<Material, BardEffect> bardEffects;
  private final BardRestorer bardRestorer;
  private final Core plugin;
  private final TObjectLongMap<UUID> msgCooldowns;
}
