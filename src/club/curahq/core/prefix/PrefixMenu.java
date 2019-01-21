package club.curahq.core.prefix;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import club.curahq.core.Core;
import club.curahq.core.util.core.ConfigUtil;
import net.md_5.bungee.api.ChatColor;

public class PrefixMenu implements Listener {

	public static File configFile = new File(Core.getPlugin().getDataFolder() + "/prefixes.yml");
	public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	private static String inventoryName = config.getString("inventory_name").replace("&", "§");

	public static void openPrefix(Player player) {
		int prefixesAmount = config.getInt("prefixes_amount");
		Inventory prefixMenu = Bukkit.createInventory(null, config.getInt("menu_size"), inventoryName);
		for (int i = 1; i <= prefixesAmount; i++) {
			ItemStack prefixItem = new ItemStack(Material.PAPER);
			ItemMeta prefixMeta = prefixItem.getItemMeta();

			String displayName = config.getString("prefixes.prefix" + i + ".display_name");
			displayName = displayName.replace("&", "§");

			String permissionInformation = hasPermission(player, i) ? "§aYou have permissions to use this prefix." : "§cYou do not have permissions to use this prefix, purchase it at §7§n"
							+ Core.getPlugin().getConfig().getString("store");
			String spacer = hasPermission(player, i) ? "§7§m--------------------------------" : "§7§m--------------------------------§7§7§m--------------------------------";

			prefixMeta.setDisplayName(displayName.replace("&", "§"));

			prefixMeta.setLore(Arrays.asList(spacer, permissionInformation, spacer));

			prefixItem.setItemMeta(prefixMeta);

			prefixMenu.setItem(config.getInt("prefixes.prefix" + i + ".inventory_location"), prefixItem);
		}

		ItemStack redRose = new ItemStack(Material.RECORD_4);
		ItemMeta redMeta = redRose.getItemMeta();

		redMeta.setDisplayName("§cClose");

		redRose.setItemMeta(redMeta);
		prefixMenu.setItem(config.getInt("menu_size") - 5, redRose);

		player.openInventory(prefixMenu);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() == null) {
			return;
		}
		if (e.getCurrentItem() == null) {
			return;
		}

		if (e.getCurrentItem().getType() == Material.AIR) {

		}
		if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {

		}
		if (!e.getInventory().getName().equals(inventoryName)) {
			return;
		}
		if (e.getCurrentItem().getType().equals(Material.RED_ROSE)) {
			e.getWhoClicked().closeInventory();
		} else {
			int prefixesAmount = config.getInt("prefixes_amount");
			for (int i = 1; i < prefixesAmount + 1; i++) {

				String prefixName = config.getString("prefixes.prefix" + i + ".display_name").replace("&", "§");
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(prefixName)) {

					Player player = (Player) e.getWhoClicked();
					if (!hasPermission(player, i)) {
						player.sendMessage(ConfigUtil.PREFIX + "§cYou are not permitted to use this prefix.");
					} else {
						try {
							setPrefix(player, i);
							player.sendMessage(ConfigUtil.PREFIX + "§aYou have successfully set your prefix to "
											+ prefixName + "&a.".replace("&", "§"));
						} catch (Exception ex) {
							ex.printStackTrace();
							player.sendMessage("§c§lERROR§c, check the console for stacktrace.");

						}
					}
				}
			}
		}
	}

	private static boolean hasPermission(Player player, int i) {
		return player.hasPermission("prefixes." + config.getString("prefixes.prefix" + i + ".name"));
	}

	private void setPrefix(Player player, int i) {
		this.config.set(player.getUniqueId() + ".prefix",
				config.getString("prefixes.prefix" + i + ".display_name").replace("&", "§"));
		try {
			savePrefixFile(config, configFile);
			reloadPrefixFile(config, configFile);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static String getPrefix(Player player) {
		if (config.getString(player.getUniqueId() + ".prefix") == null) {
			return "";
		} else {
			return config.getString(player.getUniqueId() + ".prefix".replace("&", "§"))  + " ";
		}
	}

	public static void createPrefix(String prefixName, String displayName) {
		int prefixes = config.getInt("prefixes_amount") + 1;
		config.set("prefixes.prefix" + prefixes + ".name", prefixName);
		config.set("prefixes.prefix" + prefixes + ".display_name", displayName);
		config.set("prefixes.prefix" + prefixes + ".inventory_location", prefixes - 1);
		config.set("prefixes_amount", config.getInt("prefixes_amount") + 1);
	}

	public static void savePrefixFile(FileConfiguration ymlConfig, File ymlFile) throws InvalidConfigurationException {
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reloadPrefixFile(FileConfiguration ymlConfig, File ymlFile)
			throws InvalidConfigurationException {
		try {
			ymlConfig.load(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@EventHandler
	public void onMessage(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/prefix")) {
			try {
				this.savePrefixFile(config, configFile);
				this.reloadPrefixFile(config, configFile);

			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

}


