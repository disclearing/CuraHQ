package club.curahq.core.faction.argument.staff;

import com.sk89q.worldedit.bukkit.WorldEditPlugin; 
import com.sk89q.worldedit.bukkit.selections.Selection;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.cuboid.Cuboid;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TREHOME on 01/20/2016.
 */
public class FactionClaimForArgument extends CommandArgument {
    private static final int MIN_EVENT_CLAIM_AREA;


    private final Core plugin;

    public FactionClaimForArgument(final Core plugin) {
        super("claimfor", " Lock all factions");
        this.plugin = plugin;
        this.permission = "command.faction.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factioName> [shouldClearClaims]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof ClaimableFaction)){
            sender.sendMessage(ChatColor.RED + "This is not a claimable faction (cannot contain claims)");
            return true;
        }
        if(faction instanceof PlayerFaction){
            sender.sendMessage(ChatColor.RED + "You cannot claim player factions like this. Try /f claim wand");
            return true;
        }
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if(worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        final Player player = (Player) sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if(selection.getWidth() < MIN_EVENT_CLAIM_AREA || selection.getLength() < MIN_EVENT_CLAIM_AREA) {
            sender.sendMessage(ChatColor.RED + "Event claim areas must be at least " + MIN_EVENT_CLAIM_AREA + 'x' + MIN_EVENT_CLAIM_AREA + '.');
            return true;
        }
        ClaimableFaction claimableFaction = (ClaimableFaction) faction;
        if(args.length == 3 && (args[2].toLowerCase().contains("true") || args[2].toLowerCase().contains("yes")) && ((ClaimableFaction) faction).getClaims().size() > 0){
            sender.sendMessage(ChatColor.YELLOW + "Set claim for " + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
            for(Claim claim : claimableFaction.getClaims()){
                claimableFaction.removeClaim(claim, sender);
            }
            claimableFaction.getClaims().clear();
            claimableFaction.setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), player);
            return true;
        }
        claimableFaction.addClaim(new Claim(claimableFaction, selection.getMinimumPoint(), selection.getMaximumPoint()), player);
        sender.sendMessage(ChatColor.YELLOW + "Added claim for " + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        switch (args.length){
            case 2 :{
                final List<String> results = new ArrayList<>(plugin.getFactionManager().getClaimableFactions().size());
                for(ClaimableFaction claimableFaction : plugin.getFactionManager().getClaimableFactions()){
                    results.add(claimableFaction.getName());
                }
                return results;
            }
             default: {
                    return Collections.emptyList();
             }
        }
    }
    static {
        MIN_EVENT_CLAIM_AREA = 2;
    }

}
