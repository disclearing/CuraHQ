package club.curahq.core.signs;

import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.block.*;
import org.bukkit.event.*;

public class KitSignListener implements Listener
{
    @EventHandler
    public void onclicksigncrowbarbuy(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final BlockState state = e.getClickedBlock().getState();
            if (state instanceof Sign) {
                final Sign s = (Sign)state;
                if (s.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', "")) && s.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&c[Kit]")) && s.getLine(2).equals(ChatColor.AQUA + "Diamond") && s.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', ""))) {
                    Kit.giveDiamondKit(p);
                }
                else if (s.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', "")) && s.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&c[Kit]")) && s.getLine(2).equals(ChatColor.YELLOW + "Bard") && s.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', ""))) {
                	Kit.giveBardKit(p);
                }
                else if (s.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', "")) && s.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&c[Kit]")) && s.getLine(2).equals(ChatColor.GREEN + "Archer") && s.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', ""))) {
                    Kit.giveArcherKit(p);
                }
                else if (s.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', "")) && s.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&c[Kit]")) && s.getLine(2).equals(ChatColor.GOLD + "Builder") && s.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', ""))) {
                    Kit.giveBuildKit(p);
                }
                else if (s.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', "")) && s.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&c[Kit]")) && s.getLine(2).equals(ChatColor.DARK_AQUA + "Rogue") && s.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', ""))) {
                    Kit.giveRogueKit(p);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignCreate(final SignChangeEvent event) {
        final Player player = event.getPlayer();
        if (player != null && player.hasPermission("sign.colour")) {
            final String[] lines = event.getLines();
            for (int i = 0; i < lines.length; ++i) {
                if (!player.hasPermission("base.sign.admin") && (event.getLine(i).contains(ChatColor.translateAlternateColorCodes('&', "Sell")) || event.getLine(i).contains("Buy") || event.getLine(i).contains("Kit"))) {
                    player.sendMessage(ChatColor.RED + "You have used a sign that you're not allowed.");
                    event.setCancelled(true);
                }
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
            }
        }
    }
}

