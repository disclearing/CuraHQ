package club.curahq.core.faction.argument;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.MapSorting;
import club.curahq.core.util.command.CommandArgument;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class FactionListArgument extends CommandArgument {
    private static final int MAX_FACTIONS_PER_PAGE = 10;
    private final Core plugin;

    public FactionListArgument(final Core plugin) {
        super("list", "See a list of all factions.");
        this.plugin = plugin;
        this.aliases = new String[]{"l"};
    }

    @SuppressWarnings("unused")
	private static net.md_5.bungee.api.ChatColor fromBukkit(final ChatColor chatColor) {
        return net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar());
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Integer page;
        if(args.length < 2) {
            page = 1;
        } else {
            page = JavaUtils.tryParseInt(args[1]);
            if(page == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        new BukkitRunnable() {
            public void run() {
                FactionListArgument.this.showList(page, label, sender);
            }
        }.runTaskAsynchronously(this.plugin);
        return true;
    }

    @SuppressWarnings("deprecation")
	private void showList(final int pageNumber, final String label, final CommandSender sender) {
        if(pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
        final Player senderPlayer = (Player) sender;
        for(final Player target : Bukkit.getOnlinePlayers()) {
            if(senderPlayer == null || senderPlayer.canSee(target)) {
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
                if(playerFaction == null) {
                    continue;
                }
                factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
            }
        }
        final Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
        final List<Map.Entry<PlayerFaction, Integer>> sortedMap = MapSorting.sortedValues( factionOnlineMap,  Comparator.reverseOrder());
        for(final Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();
            List<BaseComponent[]> results = pages.get(currentPage);
            if(results == null || results.size() >= MAX_FACTIONS_PER_PAGE) {
                pages.put(++currentPage, results = new ArrayList<>(MAX_FACTIONS_PER_PAGE));
            }
            final PlayerFaction playerFaction2 = entry.getKey();
            final String displayName = playerFaction2.getName();
            final int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * MAX_FACTIONS_PER_PAGE) : 0) + 1;
            final ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.WHITE);
            builder.append(displayName).color(net.md_5.bungee.api.ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "Click to view faction. " + displayName + ChatColor.GREEN + '.').create()));
            builder.append(ChatColor.GRAY + " (" + ChatColor.GRAY + entry.getValue() + '/' + playerFaction2.getMembers().size() + ") "+ ChatColor.GRAY + "(DTR " + playerFaction2.getDtrColour() + JavaUtils.format(playerFaction2.getDeathsUntilRaidable()) + ChatColor.GRAY + '/' + ChatColor.GRAY + JavaUtils.format(playerFaction2.getMaximumDeathsUntilRaidable())+ ChatColor.GRAY + ")", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.GREEN);
            results.add(builder.create());
        }
        final int maxPages = pages.size();
        if(pageNumber > maxPages) {
            sender.sendMessage(ChatColor.RED + "There " + ((maxPages == 1) ? ("is only " + maxPages + " page") : ("No factions to be displayed at this time")) + ".");
            return;
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GOLD + " Faction List " + ChatColor.YELLOW + "(Page " + pageNumber + '/' + maxPages + ')');
        final Player player = (Player) sender;
        final Collection<BaseComponent[]> components = pages.get(pageNumber);
        for(final BaseComponent[] component : components) {
            if(component == null) {
                continue;
            }
            if(player != null) {
                player.spigot().sendMessage(component);
            } else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }
        sender.sendMessage(ChatColor.GOLD + " To view other pages, use " + '/' + label + ' ' + this.getName() + " <#>");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
