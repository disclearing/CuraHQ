package club.curahq.core.faction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import club.curahq.core.Core;
import club.curahq.core.faction.argument.FactionAcceptArgument;
import club.curahq.core.faction.argument.FactionAllyArgument;
import club.curahq.core.faction.argument.FactionAnnouncementArgument;
import club.curahq.core.faction.argument.FactionChatArgument;
import club.curahq.core.faction.argument.FactionClaimArgument;
import club.curahq.core.faction.argument.FactionClaimsArgument;
import club.curahq.core.faction.argument.FactionCoLeaderArgument;
import club.curahq.core.faction.argument.FactionCreateArgument;
import club.curahq.core.faction.argument.FactionDemoteArgument;
import club.curahq.core.faction.argument.FactionDepositArgument;
import club.curahq.core.faction.argument.FactionDisbandArgument;
import club.curahq.core.faction.argument.FactionHelpArgument;
import club.curahq.core.faction.argument.FactionHomeArgument;
import club.curahq.core.faction.argument.FactionInviteArgument;
import club.curahq.core.faction.argument.FactionInvitesArgument;
import club.curahq.core.faction.argument.FactionKickArgument;
import club.curahq.core.faction.argument.FactionLeaderArgument;
import club.curahq.core.faction.argument.FactionLeaveArgument;
import club.curahq.core.faction.argument.FactionListArgument;
import club.curahq.core.faction.argument.FactionMapArgument;
import club.curahq.core.faction.argument.FactionMessageArgument;
import club.curahq.core.faction.argument.FactionOpenArgument;
import club.curahq.core.faction.argument.FactionPromoteArgument;
import club.curahq.core.faction.argument.FactionRenameArgument;
import club.curahq.core.faction.argument.FactionSetHomeArgument;
import club.curahq.core.faction.argument.FactionShowArgument;
import club.curahq.core.faction.argument.FactionStuckArgument;
import club.curahq.core.faction.argument.FactionUnallyArgument;
import club.curahq.core.faction.argument.FactionUnclaimArgument;
import club.curahq.core.faction.argument.FactionUninviteArgument;
import club.curahq.core.faction.argument.FactionWithdrawArgument;
import club.curahq.core.faction.argument.staff.FactionChatSpyArgument;
import club.curahq.core.faction.argument.staff.FactionClaimForArgument;
import club.curahq.core.faction.argument.staff.FactionClearClaimsArgument;
import club.curahq.core.faction.argument.staff.FactionForceJoinArgument;
import club.curahq.core.faction.argument.staff.FactionForceLeaderArgument;
import club.curahq.core.faction.argument.staff.FactionForcePromoteArgument;
import club.curahq.core.faction.argument.staff.FactionLockArgument;
import club.curahq.core.faction.argument.staff.FactionMuteArgument;
import club.curahq.core.faction.argument.staff.FactionRemoveArgument;
import club.curahq.core.faction.argument.staff.FactionSetDeathbanMultiplierArgument;
import club.curahq.core.faction.argument.staff.FactionSetDtrArgument;
import club.curahq.core.faction.argument.staff.FactionSetDtrRegenArgument;
import club.curahq.core.faction.argument.staff.FactionShowLicenseArgument;
import club.curahq.core.util.command.ArgumentExecutor;
import club.curahq.core.util.command.CommandArgument;

public class FactionExecutor extends ArgumentExecutor {
    private final CommandArgument helpArgument;

    public FactionExecutor(final Core plugin) {
        super("faction");
        this.addArgument(new FactionLockArgument(plugin));
        this.addArgument(new FactionCoLeaderArgument(plugin));
        this.addArgument(new FactionAcceptArgument(plugin));
        this.addArgument(new FactionAllyArgument(plugin));
        this.addArgument(new FactionChatArgument(plugin));
        this.addArgument(new FactionChatSpyArgument(plugin));
        this.addArgument(new FactionClaimArgument(plugin));
        addArgument(new FactionClaimForArgument(plugin));
        this.addArgument(new FactionClaimsArgument(plugin));
        this.addArgument(new FactionClearClaimsArgument(plugin));
        this.addArgument(new FactionCreateArgument(plugin));
        this.addArgument(new FactionAnnouncementArgument(plugin));
        this.addArgument(new FactionDemoteArgument(plugin));
        this.addArgument(new FactionDisbandArgument(plugin));
        this.addArgument(new FactionSetDtrRegenArgument(plugin));
        addArgument(new FactionDepositArgument(plugin));
        addArgument(new FactionWithdrawArgument(plugin));
        this.addArgument(new FactionForceJoinArgument(plugin));
        this.addArgument(new FactionForceLeaderArgument(plugin));
        this.addArgument(new FactionMuteArgument(plugin));
        this.addArgument(new FactionForcePromoteArgument(plugin));
        this.addArgument(this.helpArgument = new FactionHelpArgument(this));
        this.addArgument(new FactionHomeArgument(this, plugin));
        this.addArgument(new FactionInviteArgument(plugin));
        this.addArgument(new FactionInvitesArgument(plugin));
        this.addArgument(new FactionKickArgument(plugin));
        this.addArgument(new FactionLeaderArgument(plugin));
        this.addArgument(new FactionLeaveArgument(plugin));
        this.addArgument(new FactionListArgument(plugin));
        this.addArgument(new FactionMapArgument(plugin));
        this.addArgument(new FactionMessageArgument(plugin));
        this.addArgument(new FactionOpenArgument(plugin));
        this.addArgument(new FactionRemoveArgument(plugin));
        this.addArgument(new FactionRenameArgument(plugin));
        this.addArgument(new FactionPromoteArgument(plugin));
        this.addArgument(new FactionSetDtrArgument(plugin));
        this.addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument(new FactionSetHomeArgument(plugin));
        this.addArgument(new FactionShowArgument(plugin));
        this.addArgument(new FactionStuckArgument(plugin));
        this.addArgument(new FactionUnclaimArgument(plugin));
        this.addArgument(new FactionUnallyArgument(plugin));
        this.addArgument(new FactionUninviteArgument(plugin));
        this.addArgument(new FactionShowLicenseArgument(plugin));
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        final CommandArgument argument = this.getArgument(args[0]);
        if(argument != null) {
            final String permission = argument.getPermission();
            if(permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }


}
