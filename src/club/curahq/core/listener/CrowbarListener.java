package club.curahq.core.listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Optional;

import club.curahq.core.Core;
import club.curahq.core.crowbar.Crowbar;
import club.curahq.core.faction.FactionMember;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.listener.factions.FactionsCoreListener;
import club.curahq.core.util.ItemBuilder;
import club.curahq.core.util.ParticleEffect;
import club.curahq.core.util.core.ConfigUtil;
import compat.com.google.common.collect.GuavaCompat;
import me.apache.commons.lang3.text.WordUtils;

public class CrowbarListener implements Listener {
    private final Core plugin;

    public CrowbarListener(final Core plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()) {
            final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(event.getItem());
            if (crowbarOptional.isPresent()) {
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final World world = player.getWorld();
                if (world.getEnvironment() != World.Environment.NORMAL) {
                    player.sendMessage(ChatColor.RED + "Crowbars may only be used in the overworld.");
                    return;
                }
                final Block block = event.getClickedBlock();
                final Location blockLocation = block.getLocation();
                if (!FactionsCoreListener.attemptBuild((Entity) player, blockLocation, ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.')) {
                    return;
                }
                final Crowbar crowbar = (Crowbar)crowbarOptional.get();
                final BlockState blockState = block.getState();
                if (blockState instanceof CreatureSpawner) {
                    final int remainingUses = crowbar.getSpawnerUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "This crowbar has no more Spawner uses.");
                        return;
                    }
                    crowbar.setSpawnerUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());
                    final CreatureSpawner spawner = (CreatureSpawner)blockState;
                    block.setType(Material.AIR);
                    blockState.update();
                    world.dropItemNaturally(blockLocation, new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").data((short)spawner.getData().getData()).loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner.getSpawnedType().name())).build());
                }
                 else if(block.getType() == Material.ENDER_PORTAL_FRAME){
                    if (block.getType() != Material.ENDER_PORTAL_FRAME) {
                        return;
                    }
                    final int remainingUses = crowbar.getEndFrameUses();
                    if (remainingUses <= 0) {
                        player.sendMessage(ChatColor.RED + "This crowbar has no more End Portal Frame uses.");
                        return;
                    }
                    boolean destroyed = false;
                    final int blockX = blockLocation.getBlockX();
                    final int blockY = blockLocation.getBlockY();
                    final int blockZ = blockLocation.getBlockZ();
                    for (int searchRadius = 4, x = blockX - searchRadius; x <= blockX + searchRadius; ++x) {
                        for (int z = blockZ - searchRadius; z <= blockZ + searchRadius; ++z) {
                            final Block next = world.getBlockAt(x, blockY, z);
                            if (next.getType() == Material.ENDER_PORTAL) {
                                next.setType(Material.AIR);
                                next.getState().update();
                                destroyed = true;
                            }
                        }
                    }
                    if (destroyed) {
                        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Ender Portal is no longer active");
                        if (playerFaction != null) {
                            boolean informFaction = false;
                            for (final Claim claim : playerFaction.getClaims()) {
                                if (claim.contains(blockLocation)) {
                                    informFaction = true;
                                    break;
                                }
                            }
                            if (informFaction) {
                                final FactionMember factionMember = playerFaction.getMember(player);
                                final String astrix = factionMember.getRole().getAstrix();
                                playerFaction.broadcast(astrix + ConfigUtil.TEAMMATE_COLOUR + " has used a Crowbar de-activating one of the factions' end portals.", player.getUniqueId());
                            }
                        }
                    }
                    crowbar.setEndFrameUses(remainingUses - 1);
                    player.setItemInHand(crowbar.getItemIfPresent());
                    block.setType(Material.AIR);
                    blockState.update();
                    world.dropItemNaturally(blockLocation, new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
                }
                if (event.getItem().getType() == Material.AIR) {
                    player.playSound(blockLocation, Sound.ITEM_BREAK, 1.0f, 1.0f);
                }
                else {
                    ParticleEffect.ENCHANTMENT_TABLE.display(player, blockLocation, 0.125f, 50);
                    player.playSound(blockLocation, Sound.LEVEL_UP, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final ItemStack stack = event.getItemInHand();
        final Player player = event.getPlayer();
        if (block.getState() instanceof CreatureSpawner && stack.hasItemMeta()) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta.hasLore() && meta.hasDisplayName()) {
                final CreatureSpawner spawner = (CreatureSpawner)block.getState();
                final List<String> lore = meta.getLore();
                if (!lore.isEmpty()) {
                    final String spawnerName = ChatColor.stripColor(lore.get(0).toUpperCase());
                    final Optional<EntityType> entityTypeOptional = GuavaCompat.getIfPresent(EntityType.class, spawnerName);
                    if (entityTypeOptional.isPresent()) {
                        spawner.setSpawnedType(entityTypeOptional.get());
                        spawner.update(true, true);
                        player.sendMessage(ChatColor.YELLOW + "Placed a " + ChatColor.BLUE + spawnerName + ChatColor.YELLOW + " spawner.");
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPrepareCrowbarCraft(final PrepareItemCraftEvent event) {
        final CraftingInventory inventory = event.getInventory();
        if (event.isRepair() && event.getRecipe().getResult().getType() == Crowbar.CROWBAR_TYPE) {
            int endFrameUses = 0;
            int spawnerUses = 0;
            int dragonUses = 0;
            boolean changed = false;
            final ItemStack[] matrix2;
            matrix2 = inventory.getMatrix();
            for (final ItemStack ingredient : matrix2) {
                final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(ingredient);
                if (crowbarOptional.isPresent()) {
                    final Crowbar crowbar = (Crowbar)crowbarOptional.get();
                    spawnerUses += crowbar.getSpawnerUses();
                    endFrameUses += crowbar.getEndFrameUses();
                    changed = true;
                }
            }
            if (changed) {
                inventory.setResult(new Crowbar(spawnerUses, endFrameUses, dragonUses).getItemIfPresent());
            }
        }
    }
}