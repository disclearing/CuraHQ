package club.curahq.core.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.claim.ClaimHandler;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.command.CommandArgument;

import java.util.UUID;

public class FactionClaimArgument extends CommandArgument {
    private final Core plugin;
    private final int CHUNK_RADIUS = 7;

    public FactionClaimArgument(final Core plugin) {
        super("claim", "Claim land in the Wilderness.", new String[]{"claimland"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <wand|chunk>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if(playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if(playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }
        final PlayerInventory inventory = player.getInventory();
        if(inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if(args[1].equalsIgnoreCase("wand")) {
            if (!inventory.addItem(new ItemStack[]{ClaimHandler.CLAIM_WAND}).isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your inventory is full.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "A claiming wand has been added to your inventory. \nRemember you can always use " + ChatColor.DARK_AQUA + "/" + label + " " + this.getName() +" chunk");
            return true;
        }else{
            if(!args[1].equalsIgnoreCase("chunk")){
                sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            final Location location = player.getLocation();
            this.plugin.getClaimHandler().tryPurchasing(player, new Claim(playerFaction, location.clone().add(CHUNK_RADIUS, 0.0, CHUNK_RADIUS), location.clone().add(-CHUNK_RADIUS, 256.0, -CHUNK_RADIUS)));
        }
        return true;
        }
}
