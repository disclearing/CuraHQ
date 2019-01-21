package club.curahq.core.faction.type;

import org.bukkit.command.CommandSender;

import club.curahq.core.util.core.ConfigUtil;

import java.util.Map;

public class WildernessFaction extends Faction {
	public WildernessFaction() {
		super("Wilderness");
	}

	public WildernessFaction(final Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getDisplayName(final CommandSender sender) {
		return ConfigUtil.WILDERNESS_COLOUR + "The " + this.getName();
	}
}
