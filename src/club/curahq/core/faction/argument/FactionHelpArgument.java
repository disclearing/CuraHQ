package club.curahq.core.faction.argument;

import com.google.common.collect.ArrayListMultimap; 
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import club.curahq.core.faction.FactionExecutor;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.JavaUtils;
import club.curahq.core.util.chat.ClickAction;
import club.curahq.core.util.chat.Text;
import club.curahq.core.util.command.CommandArgument;
import club.curahq.core.util.core.ConfigUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionHelpArgument extends CommandArgument {
    private static final int HELP_PER_PAGE = 10;
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, Text> pages;

    public FactionHelpArgument(final FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
        this.isPlayerOnly = true;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        final Integer page = JavaUtils.tryParseInt(args[1]);
        if(page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }

    private void showPage(final CommandSender sender, final String label, final int pageNumber) {
        if(this.pages == null) {
            final boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            final Multimap<Integer, Text> pages = ArrayListMultimap.create();
            for(final CommandArgument argument : this.executor.getArguments()) {
                if(argument.equals((Object) this)) {
                    continue;
                }
                final String permission = argument.getPermission();
                if(permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                if(argument.isPlayerOnly() && !isPlayer) {
                    continue;
                }
                ++count;
                pages.get(val).add(new Text(ChatColor.BLUE + "   /" + label + ' ' + argument.getName() + ChatColor.BLUE + " > " + ChatColor.GRAY + argument.getDescription()).setColor(ChatColor.GOLD).setClick(ClickAction.SUGGEST_COMMAND, "/"+label +" "+argument.getName()));
                if(count % HELP_PER_PAGE != 0) {
                    continue;
                }
                ++val;
            }
            this.pages = ImmutableMultimap.copyOf(pages);
        }
        final int totalPageCount = this.pages.size() / HELP_PER_PAGE;
        if(pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if(pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        if (pageNumber == 1) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(C(ConfigUtil.PRIMAIRY_COLOR  + " Faction Help " + ConfigUtil.THIRD_COLOR + "(Page " + pageNumber + '/' + totalPageCount + ')'));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f focus &6- &7Focus a player in another faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f accept &6- &7Accept a join request from an existing faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f ally &6- &7Make an ally pact with other factions."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f announcement &6- &7Set your faction announcement."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f chat &6- &7Toggle faction chat only mode on or off."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f claim &6- &7Claim land in the Wilderness."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f claimchunk &6- &7Claim a chunk of land in the Wilderness."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f claims &6- &7View all claims for a faction."));
            sender.sendMessage(C(ConfigUtil.SECONDAIRY_COLOR + " To view other pages, use " + ConfigUtil.THIRD_COLOR + '/' + label + ' ' + this.getName() + " <#>"));
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        }
        if (pageNumber == 2) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(C(ConfigUtil.PRIMAIRY_COLOR  + " Faction Help " + ConfigUtil.THIRD_COLOR + "(Page " + pageNumber + '/' + totalPageCount + ')'));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f create &6- &7Create a faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f coleader &6- &7S."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f demote &6- &7Make an ally pact with other factions."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f deposit &6- &7Set your faction announcement."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f disband &6- &7Disband your faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f home &6- &7Teleport to the faction home."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f invite &6- &7Invite a player to the faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f invites &6- &7View faction invitations."));
            sender.sendMessage(C(ConfigUtil.SECONDAIRY_COLOR + " To view other pages, use " + ConfigUtil.THIRD_COLOR + '/' + label + ' ' + this.getName() + " <#>"));
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        }
        if (pageNumber == 3) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(C(ConfigUtil.PRIMAIRY_COLOR  + " Faction Help " + ConfigUtil.THIRD_COLOR + "(Page " + pageNumber + '/' + totalPageCount + ')'));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f kick &6- &7Kick a player from the faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f leader &6- &7Sets the new leader for your faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f leave &6- &7Leave your current faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f list &6- &7See a list of all factions"));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f map &6- &7View all claims around your chunk."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f message &6- &7Sends a message to your faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f open &6- &7Opens the faction to the public."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f uninvite &6- &7Revokes a players invitation."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f mute &6- &7Mute a factions members."));
            sender.sendMessage(C(ConfigUtil.SECONDAIRY_COLOR + " To view other pages, use " + ConfigUtil.THIRD_COLOR + '/' + label + ' ' + this.getName() + " <#>"));
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        }
        if (pageNumber == 4) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(C(ConfigUtil.PRIMAIRY_COLOR  + " Faction Help " + ConfigUtil.THIRD_COLOR + "(Page " + pageNumber + '/' + totalPageCount + ')'));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f rename &6- &7Change your factions name."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f promote &6- &7Promotes a player to captain."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f sethome &6- &7Sets the faction home location."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f show &6- &7Get details about a faction"));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f stuck &6- &7Teleport to a safe position."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f unclaim &6- &7Unclaims land from your faction."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f unally &6- &7Removes an ally pact with other factions."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f withdraw &6- &7Withdraws money from the faction balance."));
            sender.sendMessage(C(" " + ConfigUtil.THIRD_COLOR + "/f unmute &6- &7Unmute a factions members"));
            sender.sendMessage(C(ConfigUtil.SECONDAIRY_COLOR + " To view other pages, use " + ConfigUtil.THIRD_COLOR + '/' + label + ' ' + this.getName() + " <#>"));
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        }
        /*/sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.YELLOW  + " Faction Help " + ChatColor.WHITE + "(" + pageNumber + '/' + totalPageCount + ')');
        for(final Text message : this.pages.get(pageNumber)) {
            message.send(sender);
        }
        sender.sendMessage(ChatColor.YELLOW + " To view other pages, use " + ChatColor.YELLOW + '/' + label + ' ' + this.getName() + " <#>" );
        if(pageNumber == 1){
        }
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);/*/
    }
    
    public String C (String string) {
    	String message = ChatColor.translateAlternateColorCodes('&', string);
    	return message;
    }
}
