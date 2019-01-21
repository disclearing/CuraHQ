package club.curahq.core.faction.type;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.md_5.bungee.api.ChatColor;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable {
    public EndPortalFaction() {
        super("EndPortal");
    }

    public EndPortalFaction(final Map<String, Object> map) {
        super(map);
    }

    public String getDisplayName(final CommandSender sender) {
        return ChatColor.DARK_AQUA + this.getName().replace("EndPortal", "End Portal");
    }

    public boolean isDeathban() {
        return true;
    }
}
