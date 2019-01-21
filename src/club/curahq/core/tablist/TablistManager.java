package club.curahq.core.tablist;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.NonNull;

public class TablistManager implements Listener
{
	public static TablistManager INSTANCE;
	private final JavaPlugin plugin;
	private final Map<UUID, Tablist> tablists;
	@NonNull private TablistEntrySupplier supplier;
	private int updateTaskId;

	public TablistManager(@NonNull final JavaPlugin plugin, @NonNull final TablistEntrySupplier supplier, long updateTime)
	{
		this.updateTaskId = -1;

		boolean startUpdater = true;
		if (TablistManager.INSTANCE != null)
		{
			for (int i = 0; i < 7; ++i)
			{
				Bukkit.getLogger().warning("");
			}
			Bukkit.getLogger().warning("WARNING! AN INSTANCE OF TABLISTMANAGER ALREADY EXISTS!");
			Bukkit.getLogger().warning("IT IS RECOMMENDED TO ONLY USE ONE OTHERWISE IT CAN CAUSE FLICKERING AND OTHER ISSUES!");
			for (int i = 0; i < 7; ++i)
			{
				Bukkit.getLogger().warning("");
			}
		}
		final long remainder = updateTime % 50L;

		TablistManager.INSTANCE = this;
		this.tablists = new ConcurrentHashMap<UUID, Tablist>();
		this.supplier = supplier;
		this.plugin = plugin;
		if (startUpdater)
		{
			this.updateTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin) plugin, (Runnable) new TablistUpdateTask(), updateTime / 50L, updateTime / 50L).getTaskId();
		}
		Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
		for(Player online : Bukkit.getOnlinePlayers()) {
			this.getTablist(online, true);
		}
	}

	@Deprecated
	public Tablist getTablist(final Player player)
	{
		return this.getTablist(player, false);
	}

	@Deprecated
	public Tablist getTablist(final Player player, final boolean create)
	{
		final UUID uniqueId = player.getUniqueId();
		Tablist tablist = this.tablists.get(uniqueId);
		if (tablist == null && create)
		{
			this.tablists.put(uniqueId, tablist = new Tablist(player));
		}
		return tablist;
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		final Tablist tablist = this.getTablist(player, true);
	}

	@EventHandler
	public void onDisable(final PluginDisableEvent event)
	{
		if (event.getPlugin() == this.plugin)
		{
			this.tablists.forEach((id, tablist) -> tablist.hideFakePlayers().clear());
			this.tablists.clear();
			HandlerList.unregisterAll((Listener) this);
			if (this.updateTaskId != -1)
			{
				Bukkit.getScheduler().cancelTask(this.updateTaskId);
			}
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event)
	{
		final Player player = event.getPlayer();
		final UUID uniqueId = player.getUniqueId();
		final Tablist tablist;
		if ((tablist = this.tablists.remove(uniqueId)) != null)
		{
			tablist.hideFakePlayers().clear();
		}
	}

	public JavaPlugin getPlugin()
	{
		return this.plugin;
	}

	public void setSupplier(@NonNull final TablistEntrySupplier supplier)
	{
		if (supplier == null) { throw new NullPointerException("supplier"); }
		this.supplier = supplier;
	}

	@NonNull
	public TablistEntrySupplier getSupplier()
	{
		return this.supplier;
	}
}
