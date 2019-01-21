package club.curahq.core.commands.staff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.curahq.core.util.core.BaseCommand;


public class RenameCommand extends BaseCommand {
	
	public static final List<String> DISALLOWED;
	
    public RenameCommand() {
    	
        super("rename", "Rename your held item.");
        this.setUsage("/(command) <newItemName>");
}
    
    public void setUsage(String string) {
		// TODO Auto-generated method stub
		
	}

    //.contains("nigger")) || (message.toLowerCase().contains("steal plugins")) || (message.toLowerCase().contains("take plugins")) || (message.toLowerCase().contains("kill yourself")) || (message.toLowerCase().contains("shit staff")
    
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + "/rename <name>");
            return true;
        }
        Player player = (Player)sender;
        ItemStack stack = player.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You are not holding anything.");
            return true;
        }
        ItemMeta meta = stack.getItemMeta();
        String oldName = meta.getDisplayName();
        if (oldName != null) {
            oldName = oldName.trim();
        }
        String newName = args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("null") ? null : ChatColor.translateAlternateColorCodes((char)'&', (String)StringUtils.join((Object[])args, (char)' ', (int)0, (int)args.length));
        if (oldName == null && newName == null) {
            sender.sendMessage(ChatColor.RED + "Your held item already has no name.");
            return true;
        }
        if (oldName != null && oldName.equals(newName)) {
            sender.sendMessage(ChatColor.RED + "Your held item is already named this.");
            return true;
        }
        if(stack.getType() == Material.TRIPWIRE_HOOK){
        	sender.sendMessage(ChatColor.RED + "You cannot rename Trip wire hooks, as you can exploit it to create crate keys.");
        	Bukkit.broadcast(ChatColor.GRAY + player.getName() + " attempted to rename a tripwire hook to " + newName + ChatColor.GRAY + "! (Could potentially be trying to create a crate key)", "command.staffmode");
        	return true;
        }
        if (newName != null) {
            final String lower = newName.toLowerCase();
            for (final String word : DISALLOWED) {
                if (lower.contains(word)) {
                    sender.sendMessage(ChatColor.RED + "You have attempted to rename your item to a disallowed name, you have now been warned.");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn -s " + sender.getName() + " Innapropriate renaming");
                    return true;
                }
            }
        }
        meta.setDisplayName(newName);
        stack.setItemMeta(meta);
        if (newName == null) {
            sender.sendMessage(ChatColor.GRAY + "Removed name of held item from " + oldName + '.');
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + "Renamed held item from " + (oldName == null ? "no name" : oldName) + " to " + newName + ChatColor.GRAY + '.');
        return true;
    }

    public String getUsage(String label) {
		// TODO Auto-generated method stub
		return null;
	}
    
    static {
        DISALLOWED = Arrays.asList("hitler", "\u534d", "jews", "nigger", "n1gger", "brouard", "ddos", "nigga", "dox", "kys", "leaked", "shit staff", "my server", "join my server", "customkkk");
    }

	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}

