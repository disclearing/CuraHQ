package club.curahq.core.timer.type;

import java.util.Collection; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.google.common.base.Optional;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.event.FactionClaimChangedEvent;
import club.curahq.core.faction.event.PlayerClaimEnterEvent;
import club.curahq.core.faction.event.cause.ClaimChangeCause;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.faction.type.RoadFaction;
import club.curahq.core.faction.type.SpawnFaction;
import club.curahq.core.timer.PlayerTimer;
import club.curahq.core.timer.TimerCooldown;
import club.curahq.core.timer.event.TimerClearEvent;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.config.PlayerData;
import club.curahq.core.util.core.ConfigUtil;
import club.curahq.core.visualise.VisualType;

@SuppressWarnings("deprecation")
public class PvPTimerProtection
  extends PlayerTimer
  implements Listener
{
  private static final long ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
  private final Map<UUID, Long> itemUUIDPickupDelays = new HashMap<UUID, Long>();
  private final Core plugin;
  
  public PvPTimerProtection(Core plugin)
  {
    super(ConfigUtil.PVP_TIMER, TimeUnit.MINUTES.toMillis(30L));
    this.plugin = plugin;
  }
  
  public String getScoreboardPrefix()
  {
    return ChatColor.GREEN.toString() + ChatColor.BOLD;
  }
  
  public void onExpire(UUID userUUID)
  {
    Player player = Bukkit.getPlayer(userUUID);
    if (player != null) {
      PlayerData.getInstance().getConfig().set("UUID." + player.getUniqueId() + ".Pvp-Timer", 0);
      PlayerData.getInstance().saveConfig();
      this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onTimerStop(TimerClearEvent event)
  {
    if (event.getTimer() == this)
    {
      Optional<UUID> optionalUserUUID = event.getUserUUID();
      if (optionalUserUUID.isPresent()) {
        onExpire((UUID)optionalUserUUID.get());
      }
    }
  }
  
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onClaimChange(FactionClaimChangedEvent event)
  {
    if (event.getCause() != ClaimChangeCause.CLAIM) {
      return;
    }
    Collection<Claim> claims = event.getAffectedClaims();
    for (Claim claim : claims)
    {
      Collection<Player> players = claim.getPlayers();
      if (!players.isEmpty())
      {
        Location location = new Location(claim.getWorld(), claim.getMinimumX() - 1, 0.0D, claim.getMinimumZ() - 1);
        location = BukkitUtils.getHighestLocation(location, location);
        for (Player player : players) {
          if ((getRemaining(player) > 0L) && (player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN))) {
            player.sendMessage(ChatColor.YELLOW + "Faction Land has been claimed at your position. As you still have your " + getDisplayName() + ChatColor.YELLOW + ", you were teleported away.");
          }
        }
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerRespawn(PlayerRespawnEvent event)
  {
    Player player = event.getPlayer();
    if ((ConfigUtil.KIT_MAP) == false) {
	    if (setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)) {
	      setPaused(player.getUniqueId(), true);
	    }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    Player player = event.getEntity();
    World world = player.getWorld();
    Location location = player.getLocation();
    Collection<ItemStack> drops = event.getDrops();
    if (!drops.isEmpty())
    {
      Iterator<ItemStack> iterator = drops.iterator();
      
      long stamp = System.currentTimeMillis() + ITEM_PICKUP_DELAY;
      while (iterator.hasNext())
      {
        this.itemUUIDPickupDelays.put(world.dropItemNaturally(location, (ItemStack)iterator.next()).getUniqueId(), Long.valueOf(stamp));
        iterator.remove();
      }
    }
    clearCooldown(player);
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onBlockIgnite(BlockIgniteEvent event)
  {
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }
    long remaining = getRemaining(player);
    if (remaining > 0L)
    {
      event.setCancelled(true);
      player.sendMessage(ChatColor.YELLOW + "You cannot ignite blocks with a " + getDisplayName() + ".");
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onItemPickup(PlayerPickupItemEvent event)
  {
    Player player = event.getPlayer();
    long remaining = getRemaining(player);
    if (remaining > 0L)
    {
      UUID itemUUID = event.getItem().getUniqueId();
      Long delay = (Long)this.itemUUIDPickupDelays.get(itemUUID);
      if (delay == null) {
        return;
      }
      long millis = System.currentTimeMillis();
      if (delay.longValue() - millis > 0L)
      {
        event.setCancelled(true);
        
        List<MetadataValue> value = player.getMetadata("pickupMessageDelay");
        if ((value != null) && (!value.isEmpty()) && (((MetadataValue)value.get(0)).asLong() - millis <= 0L))
        {
          player.setMetadata("pickupMessageDelay", new FixedMetadataValue(this.plugin, Long.valueOf(millis + 1250L)));
          player.sendMessage(ChatColor.RED + "You cannot pick this item up with a Invincibility.");
        }
      }
      else
      {
        this.itemUUIDPickupDelays.remove(itemUUID);
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
	TimerCooldown runnable = (TimerCooldown) this.cooldowns.get(player.getUniqueId());
    if ((runnable != null) && (runnable.getRemaining() > 0L)) {
      runnable.setPaused(true);
      PlayerData.getInstance().getConfig().set("UUID." + player.getUniqueId() + ".Pvp-Timer", runnable.getRemaining());
      PlayerData.getInstance().saveConfig();
    }
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
	 if ((PlayerData.getInstance().getConfig().getLong("UUID." + event.getPlayer().getUniqueId() + ".Pvp-Timer") > 0) 
			 && (PlayerData.getInstance().getConfig().contains("UUID." + event.getPlayer().getUniqueId() + ".Pvp-Timer"))) {
		long pvptimer = PlayerData.getInstance().getConfig().getLong("UUID." + event.getPlayer().getUniqueId() + ".Pvp-Timer");
		setCooldown(event.getPlayer(), event.getPlayer().getUniqueId(), pvptimer, true);
	 }
	 if (Core.getPlugin().getFactionManager().getFactionAt(event.getPlayer().getLocation()) instanceof SpawnFaction) {
		this.setPaused(event.getPlayer().getUniqueId(), true);
	}
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event)
  {
    Player player = event.getPlayer();
    if (!player.hasPlayedBefore())
    {
      if ((setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)))
      {
        setPaused(player.getUniqueId(), true);
        player.sendMessage(ChatColor.YELLOW + "You now have a " + getDisplayName() + ChatColor.YELLOW + ".");
      }
    }
    else if ((isPaused(player)) && (getRemaining(player) > 0L) && (!this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone())) {
      setPaused(player.getUniqueId(), false);
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
  public void onPlayerClaimEnterMonitor(PlayerClaimEnterEvent event)
  {
    Player player = event.getPlayer();
    if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END)
    {
      clearCooldown(player);
      return;
    }
    boolean flag = getRemaining(player.getUniqueId()) > 0L;
    if (flag)
    {
      Faction toFaction = event.getToFaction();
      Faction fromFaction = event.getFromFaction();
      if ((fromFaction.isSafezone()) && (!toFaction.isSafezone()))
      {
        setPaused(player.getUniqueId(), false);
      }
      else if ((!fromFaction.isSafezone()) && (toFaction.isSafezone()))
      {
        setPaused(player.getUniqueId(), true);
      }
    }
  }
  
@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onPlayerClaimEnter(PlayerClaimEnterEvent event)
  {
    Player player = event.getPlayer();
    Faction toFaction = event.getToFaction();
    if (((toFaction instanceof ClaimableFaction)) && ((getRemaining(player)) > 0L))
    {
      if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT)
      {
        PlayerFaction playerFaction;
        if (((toFaction instanceof PlayerFaction)) && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null) && (playerFaction == toFaction))
        {
          player.sendMessage(ChatColor.YELLOW + "You have entered your own claim, therefore your " + getDisplayName() + ChatColor.YELLOW + " was removed.");
          clearCooldown(player);
          return;
        }
      }
      if ((!toFaction.isSafezone()) && (!(toFaction instanceof RoadFaction)))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot enter " + toFaction.getDisplayName(player) + ChatColor.RED + " whilst your " + getDisplayName() + ChatColor.RED + " is active." + " Use '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + 
          "' to remove this timer.");
      }
    }
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    Entity entity = event.getEntity();
    if ((entity instanceof Player))
    {
      Player attacker = BukkitUtils.getFinalAttacker(event, true);
      if (attacker == null) {
        return;
      }
      Player player = (Player)entity;
      if ((getRemaining(player)) > 0L)
      {
        event.setCancelled(true);
        attacker.sendMessage(ChatColor.RED + player.getName() + " has their " + getDisplayName() + ChatColor.RED + '.');
        
        return;
      }
      if ((getRemaining(attacker)) > 0L)
      {
        event.setCancelled(true);
        attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst your " + getDisplayName() + ChatColor.RED + " is active.");
      }
    }
  }
  
@EventHandler(ignoreCancelled=true, priority=EventPriority.NORMAL)
  public void onPotionSplash(PotionSplashEvent event)
  {
    ThrownPotion potion = event.getPotion();
    if (((potion.getShooter() instanceof Player)) && (BukkitUtils.isDebuff(potion))) {
      for (LivingEntity livingEntity : event.getAffectedEntities()) {
        if (((livingEntity instanceof Player)) && 
          (getRemaining((Player)livingEntity) > 0L)) {
          event.setIntensity(livingEntity, 0.0D);
        }
      }
    }
  }
  
  @Override
  public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite) {
	if (!this.plugin.getEotwHandler().isEndOfTheWorld()
			&& super.setCooldown(player, playerUUID, duration, overwrite)) {
		return true;
	}
	return false;
  }
}
