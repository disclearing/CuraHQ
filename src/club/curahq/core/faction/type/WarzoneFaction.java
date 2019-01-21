package club.curahq.core.faction.type;

import org.bukkit.command.CommandSender;

import club.curahq.core.util.core.ConfigUtil;

import java.util.Map;

public class WarzoneFaction extends Faction {
	public WarzoneFaction() {
		super("Warzone");
	}

	public WarzoneFaction(final Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getDisplayName(final CommandSender sender) {
		return ConfigUtil.WARZONE_COLOUR + this.getName();
	}
}
