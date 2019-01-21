package club.curahq.core.kitmap;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;

public class Kit
{
    public ItemStack frp;
    public ItemStack pois;
    
    public Kit() {
        this.frp = new ItemStack(Material.POTION, 1, (short)8259);
        this.pois = new ItemStack(Material.POTION, 1, (short)16388);
    }
    
    public static void giveArcherKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        p.getActivePotionEffects().clear();
        inv.setHelmet(new ItemStack(Material.AIR));
        inv.setChestplate(new ItemStack(Material.AIR));
        inv.setLeggings(new ItemStack(Material.AIR));
        inv.setBoots(new ItemStack(Material.AIR));
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        bow.addEnchantment(Enchantment.DURABILITY, 3);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        final ItemStack sugar = new ItemStack(Material.SUGAR, 32);
        new ItemStack(Material.FEATHER, 32);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        final ItemStack arrow = new ItemStack(Material.ARROW, 1);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(2, bow);
        inv.setItem(3, sugar);
        inv.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inv.setItem(9, arrow);
        for (int i = 0; i < 35; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
    
    public static void giveScoutKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        p.getActivePotionEffects().clear();
        inv.setHelmet(new ItemStack(Material.AIR));
        inv.setChestplate(new ItemStack(Material.AIR));
        inv.setLeggings(new ItemStack(Material.AIR));
        inv.setBoots(new ItemStack(Material.AIR));
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack sugar = new ItemStack(Material.SUGAR, 32);
        new ItemStack(Material.FEATHER, 32);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(2, sugar);
        inv.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        for (int i = 0; i < 37; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
    
    public static void giveBardKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.clear();
        p.getActivePotionEffects().clear();
        inv.setHelmet(new ItemStack(Material.AIR));
        inv.setChestplate(new ItemStack(Material.AIR));
        inv.setLeggings(new ItemStack(Material.AIR));
        inv.setBoots(new ItemStack(Material.AIR));
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.GOLD_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(4, new ItemStack(Material.BLAZE_POWDER, 16));
        inv.setItem(3, new ItemStack(Material.SUGAR, 64));
        inv.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inv.setItem(2, new ItemStack(Material.FEATHER, 8));
        inv.setItem(9, new ItemStack(Material.IRON_INGOT, 8));
        for (int i = 0; i < 35; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
    
    public static void giveBuildKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.clear();
        p.getActivePotionEffects().clear();
        inv.setHelmet(new ItemStack(Material.AIR));
        inv.setChestplate(new ItemStack(Material.AIR));
        inv.setLeggings(new ItemStack(Material.AIR));
        inv.setBoots(new ItemStack(Material.AIR));
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack spade = new ItemStack(Material.DIAMOND_SPADE);
        spade.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
        pick.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        new ItemStack(Material.POTION, 1, (short)8226);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));
        inv.addItem(new ItemStack[] { axe });
        inv.addItem(new ItemStack[] { spade });
        inv.addItem(new ItemStack[] { pick });
        inv.addItem(new ItemStack[] { new ItemStack(Material.ANVIL, 2) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.CHEST, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.WORKBENCH, 2) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.WATER_BUCKET, 1) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.COBBLESTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.MYCEL, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.NETHER_BRICK, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.GLOWSTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.LOG, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.LOG_2, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STRING, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STRING, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.GLASS, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.GLASS, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.GLASS, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE_PLATE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.REDSTONE_BLOCK, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.REDSTONE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.PISTON_BASE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.PISTON_STICKY_BASE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.REDSTONE_COMPARATOR, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.DIODE, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.REDSTONE_TORCH_OFF, 64) });
        inv.addItem(new ItemStack[] { new ItemStack(Material.STONE_BUTTON, 64) });
        for (int i = 0; i < 35; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
    
    public static void giveDiamondKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.clear();
        p.getActivePotionEffects().clear();
        inv.setHelmet(new ItemStack(Material.AIR));
        inv.setChestplate(new ItemStack(Material.AIR));
        inv.setLeggings(new ItemStack(Material.AIR));
        inv.setBoots(new ItemStack(Material.AIR));
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        final ItemStack swp = new ItemStack(Material.POTION, 1, (short)8226);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(2, swp);
        inv.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inv.setItem(9, swp);
        inv.setItem(18, swp);
        inv.setItem(27, swp);
        inv.setItem(28, swp);
        for (int i = 0; i < 35; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
    
    public static void giveRogueKit(final Player p) {
        final PlayerInventory inv = p.getInventory();
        inv.clear();
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        final ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        final ItemStack pot = new ItemStack(Material.POTION, 1, (short)16421);
        new ItemStack(Material.POTION, 1, (short)8226);
        final ItemStack gs = new ItemStack(Material.GOLD_SWORD, 1);
        new ItemStack(Material.POTION, 1, (short)8259);
        new ItemStack(Material.POTION, 1, (short)16388);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inv.setItem(2, gs);
        inv.setItem(3, gs);
        inv.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inv.setItem(9, gs);
        inv.setItem(10, gs);
        inv.setItem(18, gs);
        inv.setItem(27, gs);
        inv.setItem(28, gs);
        for (int i = 0; i < 35; ++i) {
            inv.addItem(new ItemStack[] { pot });
        }
        
    }
}
