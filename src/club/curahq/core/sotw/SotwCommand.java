package club.curahq.core.sotw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import club.curahq.core.Core;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.core.ConfigUtil;
import me.apache.commons.lang3.time.DurationFormatUtils;

public class SotwCommand implements CommandExecutor, TabCompleter {

    private static List<String> COMPLETIONS = ImmutableList.of("start", "end");

    private final Core plugin;
    public static ArrayList<UUID> enabled;
    
    public SotwCommand(Core plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    	Player p = (Player) sender;
            if (args.length > 0) {
            	
                if (args[0].equalsIgnoreCase("start")) {
                	if(!sender.hasPermission("sotw.start")) {
                		sender.sendMessage("§cYou do not have permission to use this command.");
                	}
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <duration>");
                        return true;
                    }
                    final long duration = JavaUtils.parse(args[1]);
                    if (duration == -1L) {
                        sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                        return true;
                    }
                    if (duration < 1000L) {
                        sender.sendMessage(ChatColor.RED + "SOTW protection time must last for at least 20 ticks.");
                        return true;
                    }
                    final SotwTimer.SotwRunnable sotwRunnable2 = this.plugin.getSotwTimer().getSotwRunnable();
                    if (sotwRunnable2 != null) {
                        sender.sendMessage(ChatColor.RED + "SOTW Protection is already enabled. use /" + label + " cancel to end it.");
                        return true;
                    }
                    this.plugin.getSotwTimer().start(duration);
        			Bukkit.broadcastMessage(("§7§m--------------------------------"));
        			Bukkit.broadcastMessage((""));
        			Bukkit.broadcastMessage(("§7The §6§lSOTW §7has started for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ". §6§lGOOD LUCK§7."));
        			Bukkit.broadcastMessage((""));
        			Bukkit.broadcastMessage(("§7§m--------------------------------"));
                    return true;
                }
                    if (args[0].equalsIgnoreCase("pause")) {
                    	if(!p.hasPermission("sotw.pause")) {
                    		return true;
                    	}
                        final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
                        if (sotwRunnable == null) {
                            sender.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou are not permitted to execute this command while there is no §a§lSOTW §cactive.");
                            return true;
                        }
                        if (SotwCommand.enabled.contains(p.getUniqueId())) {
                        	if(Core.getPlugin().getTimerManager().getCombatTimer().getRemaining(p) > 0L) {
                                sender.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou are not allowed to use that while still in " + Core.getPlugin().getTimerManager().getCombatTimer().getScoreboardPrefix()
                                		+ " §a§lSOTW §cTimer.");
                                return true;
                        	}
                        	SotwCommand.enabled.remove(p.getUniqueId());
                            sender.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou have §7§nUNPAUSED§r §cyour §a§lSOTW §cTimer.");
                        }
                        else {
                            SotwCommand.enabled.add(p.getUniqueId());
                            sender.sendMessage(ConfigUtil.FACTION_PREFIX + "§cYou have §7§nPAUSED§r §cyour §a§lSOTW §cTimer.");
                        }
                        return true;
                    }
            	
                else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
                	if(!sender.hasPermission("sotw.end")) {
                		sender.sendMessage("§cYou do not have permission to use this command.");
                	}
                    if (this.plugin.getSotwTimer().cancel()) {
            			Bukkit.broadcastMessage(("§7§m--------------------------------"));
            			Bukkit.broadcastMessage((""));
            			Bukkit.broadcastMessage(("§7The §6§lSOTW §7has ended. §6§lGOOD LUCK§7."));
            			Bukkit.broadcastMessage((""));
            			Bukkit.broadcastMessage(("§7§m--------------------------------"));
                        SotwCommand.enabled.remove(p.getUniqueId());
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "SOTW protection is not active.");
                    return true;
                }            
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " start <time>, pause, end");
            return true;
        }
        if (args.length <= 0) {
            sender.sendMessage("§8§m" + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage("§7/sotw enable");
            sender.sendMessage("§8§m" + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
		return true;
       }


    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, SotwCommand.COMPLETIONS) : Collections.emptyList();
    }
    
    static {
        COMPLETIONS = (List)ImmutableList.of((Object)"start", (Object)"end");
        SotwCommand.enabled = new ArrayList<UUID>();
    }
}
