package club.curahq.core.commands.essentials;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.BaseCommand;
import me.apache.commons.lang3.StringUtils;

public class SudoCommand extends BaseCommand {
    public SudoCommand() {
        super("sudo", "Forces a player to run command."); 
    }

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean force;
        if (args.length < 3) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + "/(command) <force> <all|playerName> <command args...> \n[Warning!] Forcing will give player temporary OP until executed.");
            return true;
        }
        try {
            force = Boolean.parseBoolean(args[0]);
        }
        catch (IllegalArgumentException ex) {
            sender.sendMessage((Object)ChatColor.RED + "Usage: " + "/(command) <force> <all|playerName> <command args...> \n[Warning!] Forcing will give player temporary OP until executed.");
            return true;
        }
        String executingCommand = StringUtils.join((Object[])args, (char)' ', (int)2, (int)args.length);
        if (args[1].equalsIgnoreCase("all")) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                this.executeCommand(target, executingCommand, force);
            }
            sender.sendMessage((Object)ChatColor.RED + "Forcing all players to run " + executingCommand + (force ? " with permission bypasses" : "") + '.');
            return true;
        }
        Player target2 = Bukkit.getPlayer((String)args[1]);
        if (target2 == null || !BaseCommand.canSee(sender, target2)) {
            sender.sendMessage((Object)ChatColor.RED + "Player not found.");
            return true;
        }
        this.executeCommand(target2, executingCommand, force);
        Command.broadcastCommandMessage((CommandSender)sender, (String)((Object)ChatColor.RED + sender.getName() + (Object)ChatColor.RED + " made " + target2.getName() + " run " + executingCommand + (force ? " with permission bypasses" : "") + '.'));
        sender.sendMessage((Object)ChatColor.RED + "Making " + target2.getName() + " to run " + executingCommand + (force ? " with permission bypasses" : "") + '.');
        return true;
    }

	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> results;
        if (args.length == 1) {
            results = new ArrayList<String>(2);
            results.add("true");
            results.add("false");
        } else {
            if (args.length != 2) {
                return Collections.emptyList();
            }
            results = new ArrayList();
            results.add("ALL");
            Player senderPlayer = sender instanceof Player ? (Player)sender : null;
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (senderPlayer != null && !senderPlayer.canSee(target)) continue;
                results.add(target.getName());
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean executeCommand(Player target, String executingCommand, boolean force) {
        if (target.isOp()) {
            force = false;
        }
        try {
            if (force) {
                target.setOp(true);
            }
            target.performCommand(executingCommand);
            boolean bl = true;
            return bl;
        }
        catch (Exception ex) {
            boolean bl = false;
            return bl;
        }
        finally {
            if (force) {
                target.setOp(false);
            }
        }
    }
}

