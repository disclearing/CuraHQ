package club.curahq.core.classes.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import club.curahq.core.Core;
import club.curahq.core.classes.PvpClass;
import club.curahq.core.util.core.ConfigUtil;
import club.curahq.core.util.core.Cooldowns;

public class RogueClass
  extends PvpClass
  implements Listener
{
  private final Core plugin;
  
  public RogueClass(Core plugin)
  {
    super("Rogue", TimeUnit.SECONDS.toMillis(1L));
    this.plugin = plugin;
    this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
    this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    Entity entity = event.getEntity();
    Entity damager = event.getDamager();
    if (((entity instanceof Player)) && ((damager instanceof Player)))
    {
      Player attacker = (Player)damager;
      PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(attacker);
      if ((equipped != null) && (equipped.equals(this)))
      {
        ItemStack stack = attacker.getItemInHand();
        if ((stack != null) && (stack.getType() == Material.GOLD_SWORD) && (stack.getEnchantments().isEmpty()))
        {
          Player player = (Player)entity;
          if (player.isDead()) {
        	  return;
          }
          if (Cooldowns.isOnCooldown("rogue_cooldown", attacker)) {
        	  attacker.sendMessage(ChatColor.RED + "You are on cooldown for another " + Cooldowns.getCooldownForPlayerInt("rogue_cooldown", attacker) + "s.");
        	  return;
          }
          if (rpGetPlayerDirection(attacker).equals(rpGetPlayerDirection(player))) {
	          player.sendMessage(ConfigUtil.ENEMY_COLOUR + attacker.getName() + ChatColor.YELLOW + " has backstabbed you.");
	          player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
	          attacker.sendMessage(ChatColor.YELLOW + "You have backstabbed " + ConfigUtil.ENEMY_COLOUR + player.getName() + ChatColor.YELLOW + '.');
	          attacker.setItemInHand(new ItemStack(Material.AIR, 1));
	          attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
	          attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.valueOf(150), 1));
	          double hp = ((CraftPlayer)player).getHealth();
	          if (hp == 0) {
	           return;
	          }
	          double amount = hp - 6.0;
	          player.setHealth(amount);
	          Cooldowns.addCooldown("rogue_cooldown", attacker, 2);  
          }
        }
      }
    }
  }
  
  public String rpGetPlayerDirection(Player playerSelf)
  {
    String dir = "";
    float y = playerSelf.getLocation().getYaw();
    if (y < 0.0F) {
      y += 360.0F;
    }
    y %= 360.0F;
    int i = (int)((y + 8.0F) / 22.5D);
    if ((i == 0) || (i == 1) || (i == 15)) {
      dir = "west";
    } else if ((i == 4) || (i == 5) || (i == 6) || (i == 2) || (i == 3)) {
      dir = "north";
    } else if ((i == 8) || (i == 7) || (i == 9)) {
      dir = "east";
    } else if ((i == 11) || (i == 10) || (i == 12) || (i == 13) || (i == 14)) {
      dir = "south";
    } else {
      dir = "west";
    }
    return dir;
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
