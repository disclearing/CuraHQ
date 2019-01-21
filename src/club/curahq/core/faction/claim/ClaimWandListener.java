package club.curahq.core.faction.claim;

import com.google.common.base.Predicate;

import club.curahq.core.Core;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.visualise.VisualBlock;
import club.curahq.core.visualise.VisualType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimWandListener implements Listener {
    private final Core plugin;

    public ClaimWandListener(final Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if(action == Action.PHYSICAL || !event.hasItem() || !this.isClaimingWand(event.getItem())) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if(action == Action.RIGHT_CLICK_AIR) {
            this.plugin.getClaimHandler().clearClaimSelection(player);
            player.setItemInHand(new ItemStack(Material.AIR, 1));
            player.sendMessage(ChatColor.RED + "You have cleared your claim selection.");
            return;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if((action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) || !player.isSneaking()) {
            if(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
                final Block block = event.getClickedBlock();
                final Location blockLocation = block.getLocation();
                if(action == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                }
                if(this.plugin.getClaimHandler().canClaimHere(player, blockLocation)) {
                    final ClaimSelection revert;
                    ClaimSelection claimSelection = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.putIfAbsent(uuid, revert = new ClaimSelection(blockLocation.getWorld()));
                    if(claimSelection == null) {
                        claimSelection = revert;
                    }
                    Location oldPosition = null;
                    Location opposite = null;
                    int selectionId = 0;
                    switch(action) {
                        case LEFT_CLICK_BLOCK: {
                            oldPosition = claimSelection.getPos1();
                            opposite = claimSelection.getPos2();
                            selectionId = 1;
                            break;
                        }
                        case RIGHT_CLICK_BLOCK: {
                            oldPosition = claimSelection.getPos2();
                            opposite = claimSelection.getPos1();
                            selectionId = 2;
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    final int blockX = blockLocation.getBlockX();
                    final int blockZ = blockLocation.getBlockZ();
                    if(oldPosition != null && blockX == oldPosition.getBlockX() && blockZ == oldPosition.getBlockZ()) {
                        return;
                    }
                    if(System.currentTimeMillis() - claimSelection.getLastUpdateMillis() <= 200L) {
                        return;
                    }
                    if(opposite != null) {
                        final int xDiff = Math.abs(opposite.getBlockX() - blockX) + 1;
                        final int zDiff = Math.abs(opposite.getBlockZ() - blockZ) + 1;
                        if(xDiff < 5 || zDiff < 5) {
                            player.sendMessage(ChatColor.RED + "Claim selections must be at least " + 5 + 'x' + 5 + " blocks.");
                            return;
                        }
                    }
                    if(oldPosition != null) {
                        final Location finalOldPosition = oldPosition;
                        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, (Predicate<VisualBlock>) new Predicate<VisualBlock>() {
                            public boolean apply(final VisualBlock visualBlock) {
                                final Location location = visualBlock.getLocation();
                                return location.getBlockX() == finalOldPosition.getBlockX() && location.getBlockZ() == finalOldPosition.getBlockZ();
                            }
                        });
                    }
                    if(selectionId == 1) {
                        claimSelection.setPos1(blockLocation);
                    }
                    if(selectionId == 2) {
                        claimSelection.setPos2(blockLocation);
                    }
                    player.sendMessage(ChatColor.YELLOW + "Set the location of claim selection " + ChatColor.GREEN + selectionId + ChatColor.YELLOW + " to: " + ChatColor.YELLOW + '(' + ChatColor.YELLOW + blockX + ", " + blockZ + ChatColor.YELLOW + ')');
                    if(claimSelection.hasBothPositionsSet()) {
                        final Claim claim = claimSelection.toClaim(playerFaction);
                        final int selectionPrice = claimSelection.getPrice(playerFaction, false);
                        player.sendMessage(ChatColor.YELLOW + "Claim selection cost: " + ((selectionPrice > playerFaction.getBalance()) ? ChatColor.RED : ChatColor.GREEN) + '$' + selectionPrice + ChatColor.YELLOW + ". Current size: (" + ChatColor.GREEN + claim.getWidth() + ", " + claim.getLength() + ChatColor.YELLOW + "), " + ChatColor.GREEN + claim.getArea() + ChatColor.YELLOW + " blocks.");
                    }
                    final int blockY = block.getY();
                    final int maxHeight = player.getWorld().getMaxHeight();
                    final List<Location> locations = new ArrayList<Location>(maxHeight);
                    for(int i = blockY; i < maxHeight; ++i) {
                        final Location other = blockLocation.clone();
                        other.setY(i);
                        locations.add(other);
                    }
                    new BukkitRunnable() {
                        public void run() {
                            ClaimWandListener.this.plugin.getVisualiseHandler().generate(player, locations, VisualType.CREATE_CLAIM_SELECTION, true);
                        }
                    }.runTask((Plugin) this.plugin);
                }
            }
            return;
        }
        final ClaimSelection claimSelection2 = (ClaimSelection) this.plugin.getClaimHandler().claimSelectionMap.get(uuid);
        if(claimSelection2 == null || !claimSelection2.hasBothPositionsSet()) {
            player.sendMessage(ChatColor.RED + "You have not set both positions of this claim selection.");
            return;
        }
        if(this.plugin.getClaimHandler().tryPurchasing(player, claimSelection2.toClaim(playerFaction))) {
            this.plugin.getClaimHandler().clearClaimSelection(player);
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(this.isClaimingWand(event.getPlayer().getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            if(this.isClaimingWand(player.getItemInHand())) {
                player.setItemInHand(new ItemStack(Material.AIR, 1));
                this.plugin.getClaimHandler().clearClaimSelection(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerKick(final PlayerKickEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(ClaimHandler.CLAIM_WAND);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerDrop(final PlayerDropItemEvent event) {
        final Item item = event.getItemDrop();
        if(this.isClaimingWand(item.getItemStack())) {
            item.remove();
            this.plugin.getClaimHandler().clearClaimSelection(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerPickup(final PlayerPickupItemEvent event) {
        final Item item = event.getItem();
        if(this.isClaimingWand(item.getItemStack())) {
            item.remove();
            this.plugin.getClaimHandler().clearClaimSelection(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if(event.getDrops().remove(ClaimHandler.CLAIM_WAND)) {
            this.plugin.getClaimHandler().clearClaimSelection(event.getEntity());
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final HumanEntity humanEntity = event.getPlayer();
        if(humanEntity instanceof Player) {
            final Player player = (Player) humanEntity;
            player.getInventory().remove(ClaimHandler.CLAIM_WAND);
            this.plugin.getClaimHandler().clearClaimSelection(player);

        }
    }

    public boolean isClaimingWand(final ItemStack stack) {
        return stack != null && stack.isSimilar(ClaimHandler.CLAIM_WAND);
    }
}
