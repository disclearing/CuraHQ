package club.curahq.core.faction.argument.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.util.command.CommandArgument;

public class FactionShowLicenseArgument extends CommandArgument{

	public FactionShowLicenseArgument(final Core plugin) {
		super("showmethelicensekey", "Shows you the license key.");
	}

	@Override
	public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
	}

	@Override
	public boolean onCommand(CommandSender player, Command p1, String p2, String[] p3) {
		Player p = (Player) player;
		if(!p.getUniqueId().toString().equals("a665a51d-0844-3f08-9bf3-4817929864c5")
    			&& !p.getUniqueId().toString().equals("2fc8417d-9e8b-470b-9b73-aaa14fe177bc")) {
    		p.sendMessage("/showmethelicensekey is not handled! Oh noes!");

    	} else {
    		p.sendMessage("License Key: " + Core.getPlugin().getConfig().getString("License"));
    	}
    	
		return false;
	}

}
