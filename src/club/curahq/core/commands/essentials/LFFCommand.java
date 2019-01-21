package club.curahq.core.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.core.Cooldowns;

public class LFFCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This is a player only command");
            return true;
        }
        if (Cooldowns.isOnCooldown("lff_cooldown", (Player)sender)) {
            sender.sendMessage(ChatColor.RED + "You are still on cooldown for " + ChatColor.RED + ChatColor.BOLD.toString() + Core.getRemaining(Cooldowns.getCooldownForPlayerLong("lff_cooldown", (Player)sender), true));
            return true;
        }
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
        Bukkit.broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + sender.getName() + ChatColor.GRAY + " is looking for a faction!");
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");
        Cooldowns.addCooldown("lff_cooldown", (Player)sender, 900);
        return false;
    }
}
