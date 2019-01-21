package club.curahq.core.crowbar;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Crowbar {
	public static final int MAX_SPAWNER_USES = 1;
	public static final int MAX_END_FRAME_USES = 6;

	public Crowbar() {
		this(1, 6, 1);
	}

	public Crowbar(int spawnerUses, int endFrameUses, int endDragonUses) {
		this.stack = new ItemStack(CROWBAR_TYPE, 1);
		Preconditions.checkArgument((spawnerUses > 0) || (endFrameUses > 0), "Cannot create a crowbar with empty uses");
		setSpawnerUses(Math.min(1, spawnerUses));
		setEndFrameUses(Math.min(6, endFrameUses));
	}

	public static Optional<Crowbar> fromStack(ItemStack stack) {
		if ((stack == null) || (!stack.hasItemMeta())) {
			return Optional.absent();
		}
		ItemMeta meta = stack.getItemMeta();
		if ((!meta.hasDisplayName()) || (!meta.hasLore()) || (!meta.getDisplayName().equals(CROWBAR_NAME))) {
			return Optional.absent();
		}
		Crowbar crowbar = new Crowbar();
		List<String> loreList = meta.getLore();
		for (String lore : loreList) {
			lore = ChatColor.stripColor(lore);
			int length = lore.length();
			for (int i = 0; i < length; i++) {
				char character = lore.charAt(i);
				if (Character.isDigit(character)) {
					int amount = Integer.parseInt(String.valueOf(character));
					if (lore.startsWith("End Frame Uses")) {
						crowbar.setEndFrameUses(amount);
						break;
					}
					if (lore.startsWith("Spawner Uses")) {
						crowbar.setSpawnerUses(amount);
						break;
					}
				}
			}
		}
		return Optional.of(crowbar);
	}

	public int getEndFrameUses() {
		return this.endFrameUses;
	}

	public void setEndFrameUses(int uses) {
		if (this.endFrameUses != uses) {
			this.endFrameUses = Math.min(6, uses);
			this.needsMetaUpdate = true;
		}
	}

	public int getSpawnerUses() {
		return this.spawnerUses;
	}

	public void setSpawnerUses(int uses) {
		if (this.spawnerUses != uses) {
			this.spawnerUses = Math.min(1, uses);
			this.needsMetaUpdate = true;
		}
	}

	public ItemStack getItemIfPresent() {
		Optional<ItemStack> optional = toItemStack();
		return optional.isPresent() ? (ItemStack) optional.get() : new ItemStack(Material.AIR, 1);
	}

	public Optional<ItemStack> toItemStack() {
		if (this.needsMetaUpdate) {
			double maxDurability;
			double curDurability = maxDurability = CROWBAR_TYPE.getMaxDurability();
			double increment = curDurability / 6.0D;
			curDurability -= increment * (this.spawnerUses + this.endFrameUses);
			if (Math.abs(curDurability - maxDurability) == 0.0D) {
				return Optional.absent();
			}
			ItemMeta meta = this.stack.getItemMeta();
			meta.setDisplayName(CROWBAR_NAME);
			meta.setLore(
					Arrays.asList(new String[] {
							String.format(LORE_FORMAT,
									new Object[] { "Spawner Uses", Integer.valueOf(this.spawnerUses),
											Integer.valueOf(1) }),
							String.format(LORE_FORMAT, new Object[] { "End Frame Uses",
									Integer.valueOf(this.endFrameUses), Integer.valueOf(6) }) }));
			this.stack.setItemMeta(meta);
			this.stack.setDurability((short) (int) curDurability);
			this.needsMetaUpdate = false;
		}
		return Optional.of(this.stack);
	}

	public static final Material CROWBAR_TYPE = Material.GOLD_HOE;
	private static final String CROWBAR_NAME = ChatColor.RED.toString() + "Crowbar";
	private static final String LORE_FORMAT = ChatColor.GRAY + "%1$s: " + ChatColor.YELLOW + "%2$s/%3$s";
	private int endFrameUses;
	private int spawnerUses;
	private final ItemStack stack;
	private boolean needsMetaUpdate;
}
