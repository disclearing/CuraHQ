package club.curahq.core.commands.essentials;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class SkullCommand implements Listener, CommandExecutor
{  
    private ItemStack playerSkullForName(final String name) {
        final ItemStack is = new ItemStack(Material.SKULL_ITEM, 1);
        is.setDurability((short)3);
        final ItemMeta meta = is.getItemMeta();
        ((SkullMeta) meta).setOwner(name);
        is.setItemMeta(meta);
        return is;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("skull") && args.length == 1) {
            if (sender instanceof Player) {
                final Player p = (Player)sender;
                p.getInventory().addItem(new ItemStack[] { this.playerSkullForName(args[0]) });
                sender.sendMessage(ChatColor.GRAY + "Added " + ChatColor.GOLD + args[0] + ChatColor.GRAY + "'s skull to your inventory");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "You must run this command as a player.");
        }
        return false;
    }
}

