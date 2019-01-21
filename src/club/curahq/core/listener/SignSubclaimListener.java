package club.curahq.core.listener;

import com.google.common.collect.Lists;

import club.curahq.core.Core;
import club.curahq.core.faction.struct.Role;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.core.ConfigUtil;
import me.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SignSubclaimListener implements Listener {
    private static final int MAX_SIGN_LINE_CHARS = 16;
    private static final String SUBCLAIM_PREFIX;
    private static final BlockFace[] SIGN_FACES;

    static {
        SUBCLAIM_PREFIX = ChatColor.RED.toString() + "[Subclaim]";
        SIGN_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
    }

    private final Core plugin;

    public SignSubclaimListener(final Core plugin) {
        this.plugin = plugin;
    }

    private boolean isSubclaimable(final Block block) {
        final Material type = block.getType();
        return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
    }

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(final SignChangeEvent event) {
        final String[] lines = event.getLines();
        if(!StringUtils.containsIgnoreCase(lines[0],  "subclaim")) {
            return;
        }
        final Block block = event.getBlock();
        final MaterialData materialData = block.getState().getData();
        if(materialData instanceof Sign) {
            final Sign sign = (Sign) materialData;
            final Block attatchedBlock = block.getRelative(sign.getAttachedFace());
            if(this.isSubclaimable(attatchedBlock)) {
                final Player player = event.getPlayer();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                final Role role;
                if(playerFaction == null || (role = playerFaction.getMember(player).getRole()) == Role.MEMBER) {
                    return;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(block.getLocation());
                if(playerFaction.equals(factionAt)) {
                    final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(attatchedBlock);
                    for(final org.bukkit.block.Sign attachedSign : attachedSigns) {
                        if(attachedSign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                            player.sendMessage(ChatColor.RED + "There is already a subclaim sign on this " + attatchedBlock.getType().toString() + '.');
                            return;
                        }
                    }
                    final List<String> memberList = new ArrayList<String>(3);
                    for(int i = 1; i < lines.length; ++i) {
                        final String line = lines[i];
                        if(StringUtils.isNotBlank((CharSequence) line)) {
                            memberList.add(line);
                        }
                    }
                    if(memberList.isEmpty()) {
                        event.setLine(1, player.getName());
                        player.sendMessage(ChatColor.YELLOW+"Since no name was specified, this subclaim is now for you.");
                    }
                    final boolean leaderChest = lines[1].equals(Role.LEADER.getAstrix()) || StringUtils.containsIgnoreCase(lines[1], "leader");
                    final boolean captainChest = lines[1].equals(Role.CAPTAIN.getAstrix()) || StringUtils.containsIgnoreCase(lines[1], "captain") || StringUtils.containsIgnoreCase(lines[1], "captains");
                    if(captainChest){
                        event.setLine(2, (String) null);
                        event.setLine(3, (String) null);
                        event.setLine(1, ChatColor.YELLOW+"Captains Only");
                    }
                    if(leaderChest) {
                        if(role != Role.LEADER) {
                            player.sendMessage(ChatColor.RED + "Only faction leaders can create leader subclaimed objects.");
                            return;
                        }
                        event.setLine(2, (String) null);
                        event.setLine(3, (String) null);
                        event.setLine(1, ChatColor.DARK_RED+"Leaders Only");
                    }
                    event.setLine(0, SignSubclaimListener.SUBCLAIM_PREFIX);
                    final List<String> actualMembers = memberList.stream().filter(member -> playerFaction.getMember(member) != null).collect(Collectors.toList());
                    playerFaction.broadcast(ConfigUtil.TEAMMATE_COLOUR + player.getName() + ChatColor.YELLOW + " has created a subclaim on block type " + ChatColor.LIGHT_PURPLE + attatchedBlock.getType().toString() + ChatColor.YELLOW + " at " + ChatColor.WHITE + '[' + attatchedBlock.getX() + ", " + attatchedBlock.getZ() + ']' + ChatColor.YELLOW + " for " + (leaderChest ? "leaders" : (actualMembers.isEmpty() ? "captains" : ("members " + ChatColor.GRAY + '[' +  ChatColor.DARK_GREEN+ StringUtils.join((Iterable<String>) actualMembers, ", ") +ChatColor.GRAY+ ']'))));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
            return;
        }
        final Block block = event.getBlock();
        final BlockState state = block.getState();
        if(state instanceof org.bukkit.block.Sign || this.isSubclaimable(block)) {
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if(playerFaction == null) {
                return;
            }
            final boolean hasAccess = playerFaction.getMember(player).getRole() != Role.MEMBER;
            if(hasAccess) {
                return;
            }
            if(state instanceof org.bukkit.block.Sign) {
                final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) state;
                if(sign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break subclaim signs");
                }
                return;
            }
            final Faction factionAt = this.plugin.getFactionManager().getFactionAt(block);
            final String search = this.getShortenedName(player.getName());
            if(playerFaction.equals(factionAt) && !playerFaction.isRaidable()) {
                final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
                for(final org.bukkit.block.Sign attachedSign : attachedSigns) {
                    final String[] lines = attachedSign.getLines();
                    if(!lines[0].equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                        continue;
                    }
                    for(int i = 1; i < lines.length; ++i) {
                        if(lines[i].contains(search)) {
                            return;
                        }
                    }
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break this subclaimed " + block.getType().toString() + '.');
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryMoveItem(final InventoryMoveItemEvent event) {
        if(this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final InventoryHolder holder = event.getSource().getHolder();
        Collection<Block> sourceBlocks;
        if(holder instanceof Chest) {
            sourceBlocks = Collections.singletonList(((Chest) holder).getBlock());
        } else {
            if(!(holder instanceof DoubleChest)) {
                return;
            }
            final DoubleChest doubleChest = (DoubleChest) holder;
            sourceBlocks = Lists.newArrayList(new Block[]{((Chest) doubleChest.getLeftSide()).getBlock(), ((Chest) doubleChest.getRightSide()).getBlock()});
        }
        for(final Block block : sourceBlocks) {
            final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
            for(final org.bukkit.block.Sign attachedSign : attachedSigns) {
                if(attachedSign.getLine(0).equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    private String getShortenedName(String originalName) {
        if(originalName.length() == MAX_SIGN_LINE_CHARS) {
            originalName = originalName.substring(0, 15);
        }
        return originalName;
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
            return;
        }
        if(this.plugin.getEotwHandler().isEndOfTheWorld()) {
            return;
        }
        final Block block = event.getClickedBlock();
        if(this.isSubclaimable(block)) {
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if(playerFaction == null || playerFaction.isRaidable()) {
                return;
            }
            final Role role = playerFaction.getMember(player).getRole();
            if(role == Role.LEADER) {
                return;
            }
            if(role == Role.COLEADER) {
                return;
            }
            if(playerFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                final Collection<org.bukkit.block.Sign> attachedSigns = this.getAttachedSigns(block);
                if(attachedSigns.isEmpty()) {
                    return;
                }
                final String search = this.getShortenedName(player.getName());
                for(final org.bukkit.block.Sign attachedSign : attachedSigns) {
                    final String[] lines = attachedSign.getLines();
                    if(!lines[0].equals(SignSubclaimListener.SUBCLAIM_PREFIX)) {
                        continue;
                    }
                    if(!Role.LEADER.getAstrix().equals(lines[1])) {
                        for(int i = 1; i < lines.length; ++i) {
                            if(lines[i].contains(search)) {
                                return;
                            }
                        }
                    }
                    if(role == Role.CAPTAIN){
                        if(lines[1].contains("Leader")){
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + block.getType().toString() + '.');
                            break;
                        }
                            return;
                    }
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + block.getType().toString() + '.');
                    break;
                }
            }
        }
    }

    public Collection<org.bukkit.block.Sign> getAttachedSigns(final Block block) {
        final Set<org.bukkit.block.Sign> results = new HashSet<org.bukkit.block.Sign>();
        this.getSignsAround(block, results);
        final BlockState state = block.getState();
        if(state instanceof Chest) {
            final Inventory chestInventory = ((Chest) state).getInventory();
            if(chestInventory instanceof DoubleChestInventory) {
                final DoubleChest doubleChest = ((DoubleChestInventory) chestInventory).getHolder();
                final Block left = ((Chest) doubleChest.getLeftSide()).getBlock();
                final Block right = ((Chest) doubleChest.getRightSide()).getBlock();
                this.getSignsAround(left.equals(block) ? right : left, results);
            }
        }
        return results;
    }

    private Set<org.bukkit.block.Sign> getSignsAround(final Block block, final Set<org.bukkit.block.Sign> results) {
        for(final BlockFace face : SignSubclaimListener.SIGN_FACES) {
            final Block relative = block.getRelative(face);
            final BlockState relativeState = relative.getState();
            if(relativeState instanceof org.bukkit.block.Sign) {
                final Sign materialSign = (Sign) relativeState.getData();
                if(relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
                    results.add((org.bukkit.block.Sign) relative.getState());
                }
            }
        }
        return results;
    }
}
