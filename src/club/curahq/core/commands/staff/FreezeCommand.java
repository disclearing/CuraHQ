package club.curahq.core.commands.staff;

import java.util.Collections;  
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;
import club.curahq.core.commands.PlayerFreezeEvent;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.chat.ClickAction;
import club.curahq.core.util.chat.Text;
import club.curahq.core.util.core.ConfigUtil;
import me.apache.commons.lang3.time.DurationFormatUtils;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

@SuppressWarnings("deprecation")
public class FreezeCommand implements Listener, CommandExecutor {
	private final static TObjectLongMap<UUID> frozenPlayers = new TObjectLongHashMap<UUID>();
	public static long defaultFreezeDuration;
	private long serverFrozenMillis;
	public static HashSet<String> frozen = new HashSet<String>();

	public FreezeCommand(Core plugin) {
		FreezeCommand.defaultFreezeDuration = TimeUnit.MINUTES.toMillis(60);
		Bukkit.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage((Object) ChatColor.RED + "Usage: /Freeze <all|player>");
			return true;
		}
		String reason = null;
		Long freezeTicks = FreezeCommand.defaultFreezeDuration;
		long millis = System.currentTimeMillis();
		if (args[0].equalsIgnoreCase("all") && sender.hasPermission(command.getPermission() + ".all")) {
			long oldTicks = this.getRemainingServerFrozenMillis();
			if (oldTicks > 0) {
				freezeTicks = (long) 0;
			}
			this.serverFrozenMillis = millis + FreezeCommand.defaultFreezeDuration;
			Bukkit.getServer()
					.broadcastMessage((Object) ChatColor.GRAY + "The server is " + (freezeTicks > 0
							? new StringBuilder().append("now frozen for ")
									.append(DurationFormatUtils.formatDurationWords((long) freezeTicks, (boolean) true,
											(boolean) true))
									.toString()
							: "no longer frozen")
							+ (reason == null ? ""
									: new StringBuilder().append(" with reason ").append(reason).toString())
							+ '.');
			return true;
		}
		Player target = Bukkit.getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage((Object) ChatColor.GOLD + "Player '" + (Object) ChatColor.WHITE + args[0]
					+ (Object) ChatColor.GOLD + "' not found.");
			return true;
		}
		if (target.equals((Object) sender)) {
			sender.sendMessage((Object) ChatColor.RED + "You cannot freeze yourself.");
			return true;
		}
		UUID targetUUID = target.getUniqueId();
		boolean shouldFreeze = FreezeCommand.getRemainingPlayerFrozenMillis(targetUUID) > 0;
		PlayerFreezeEvent playerFreezeEvent = new PlayerFreezeEvent(target, shouldFreeze);
		Bukkit.getServer().getPluginManager().callEvent((Event) playerFreezeEvent);
		if (playerFreezeEvent.isCancelled()) {
			sender.sendMessage((Object) ChatColor.RED + "Unable to freeze " + target.getName() + '.');
			return false;
		}
		if (shouldFreeze) {
			FreezeCommand.frozen.remove(target.getName());
			FreezeCommand.frozenPlayers.remove((Object) targetUUID);
			target.sendMessage((Object) ChatColor.GREEN + "You have been un-frozen.");
			Command.broadcastCommandMessage((CommandSender) sender,
					(String) ((Object) ChatColor.GRAY + target.getName() + " is no longer frozen."));
		} else {
			FreezeCommand.frozen.add(target.getName());
			FreezeCommand.frozenPlayers.put(targetUUID, millis + freezeTicks);
			String timeString = DurationFormatUtils.formatDurationWords((long) freezeTicks, (boolean) true,
					(boolean) true);
			this.Message(target.getName());
			Command.broadcastCommandMessage((CommandSender) sender,
					(String) ((Object) ChatColor.GRAY + target.getName() + " is now frozen for " + timeString + '.'));
		}
		return true;
	}

	private void Message(final String name) {
		new HashMap<Object, Object>();
		final Player p = Bukkit.getPlayer((String) name);
		new BukkitRunnable() {

			public void run() {
				if (FreezeCommand.frozen.contains(name)) {
					p.sendMessage("");
					p.sendMessage(ChatColor.GRAY + "§8§m---------§8§m-----------§8§m------");
					p.sendMessage(ChatColor.GRAY + "You have been frozen by a staff member.");
					p.sendMessage(ChatColor.GRAY + "If you disconnect you will be " + (Object) ChatColor.DARK_RED
							+ (Object) ChatColor.BOLD + "BANNED" + (Object) ChatColor.GRAY + '.');
					p.sendMessage(
							ChatColor.GRAY + "Please connect to our Teamspeak" + (Object) ChatColor.GRAY + '.');
					new Text(ChatColor.GRAY + " (" + ConfigUtil.TEAMSPEAK_URL + ") "
							+ (Object) ChatColor.ITALIC + "Click me to download" + (Object) ChatColor.GRAY + '.')
									.setClick(ClickAction.OPEN_URL, "http://www.teamspeak.com/downloads")
									.send((CommandSender) p);
					p.sendMessage(ChatColor.GRAY + "§8§m---------§8§m-----------§8§m------");
					p.sendMessage("");
				} else {
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously((Plugin) Core.getPlugin(), 0, 30);
	}
	public void onFreezeGUI(Player p) {

		Inventory inv = Bukkit.getServer().createInventory(null, 9, ChatColor.RED + "You are frozen!");

		ItemStack redglass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemStack freezepaper = new ItemStack(Material.PAPER);

		ItemMeta redglassmeta = redglass.getItemMeta();
		ItemMeta freezepapermeta = freezepaper.getItemMeta();

		redglassmeta.setDisplayName(ChatColor.RED + "You are frozen!");
		freezepapermeta.setDisplayName(ChatColor.RED + "Teamspeak: (ts.kitmap.us)");

		redglass.setItemMeta(redglassmeta);
		freezepaper.setItemMeta(freezepapermeta);

		inv.setItem(0, redglass);
		inv.setItem(1, redglass);
		inv.setItem(2, redglass);
		inv.setItem(3, redglass);
		inv.setItem(4, freezepaper);
		inv.setItem(5, redglass);
		inv.setItem(6, redglass);
		inv.setItem(7, redglass);
		inv.setItem(8, redglass);

		p.openInventory(inv);
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (FreezeCommand.frozen.contains(player)) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDrop(PlayerDropItemEvent e) {
		Player player = (Player) e.getPlayer();
		if (FreezeCommand.frozen.contains(player)) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		Player player = (Player) e.getPlayer();
		if (FreezeCommand.frozen.contains(player)) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBreak(BlockPlaceEvent e) {
		Player player = (Player) e.getPlayer();
		if (FreezeCommand.frozen.contains(player)) {
			e.setCancelled(true);
		}
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return args.length == 1 ? null : Collections.emptyList();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, false);
			if (attacker == null) {
				return;
			}
			Player player = (Player) entity;
			if (!(this.getRemainingServerFrozenMillis() <= 0
					&& FreezeCommand.getRemainingPlayerFrozenMillis(player.getUniqueId()) <= 0
					|| player.hasPermission("command.freeze.bypass"))) {
				attacker.sendMessage(
						(Object) ChatColor.RED + player.getName() + " is currently frozen, you may not attack.");
				event.setCancelled(true);
				return;
			}
			if (!(this.getRemainingServerFrozenMillis() <= 0
					&& FreezeCommand.getRemainingPlayerFrozenMillis(attacker.getUniqueId()) <= 0
					|| attacker.hasPermission("command.freeze.bypass"))) {
				event.setCancelled(true);
				attacker.sendMessage((Object) ChatColor.RED + "You may not attack players whilst frozen.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPreCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (!(this.getRemainingServerFrozenMillis() <= 0
				&& FreezeCommand.getRemainingPlayerFrozenMillis(player.getUniqueId()) <= 0
				|| player.hasPermission("command.freeze.bypass"))) {
			event.setCancelled(true);
			player.sendMessage((Object) ChatColor.RED + "You may not use commands whilst frozen.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}
		Player player = event.getPlayer();
		if (!(this.getRemainingServerFrozenMillis() <= 0
				&& FreezeCommand.getRemainingPlayerFrozenMillis(player.getUniqueId()) <= 0
				|| player.hasPermission("command.freeze.bypass"))) {
			event.setTo(event.getFrom());
		}
	}

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        if (FreezeCommand.frozen.contains(e.getPlayer().getName())) {
            for (final Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("command.command.freeze")) {
                    continue;
                }
                new Text(ChatColor.GRAY + e.getPlayer().getName() + " has " + ChatColor.DARK_RED + "QUIT" + ChatColor.GRAY + " while frozen. " + ChatColor.GRAY + ChatColor.ITALIC + "(Click here to ban)").setHoverText(ChatColor.GRAY + "Click here to permanently ban " + ChatColor.GRAY + e.getPlayer().getName()).setClick(ClickAction.RUN_COMMAND, "/ban " + e.getPlayer().getName() + " Disconnected whilst Frozen").send((CommandSender)online);
            }
        }
    }

	public long getRemainingServerFrozenMillis() {
		return this.serverFrozenMillis - System.currentTimeMillis();
	}

	public static long getRemainingPlayerFrozenMillis(UUID uuid) {
		long remaining = frozenPlayers.get((Object) uuid);
		if (remaining == frozenPlayers.getNoEntryValue()) {
			return 0;
		}
		return remaining - System.currentTimeMillis();
	}

}