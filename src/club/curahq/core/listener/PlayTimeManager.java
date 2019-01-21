package club.curahq.core.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import club.curahq.core.util.Config;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

public class PlayTimeManager
implements Listener {
    private final TObjectLongMap<UUID> totalPlaytimeMap = new TObjectLongHashMap<UUID>();
    private final TObjectLongMap<UUID> sessionTimestamps = new TObjectLongHashMap<UUID>();
    private final Config config;

    public PlayTimeManager(JavaPlugin plugin) {
        this.config = new Config(plugin, "play-times");
        this.reloadPlaytimeData();
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.totalPlaytimeMap.put(uuid, this.getTotalPlayTime(uuid));
        this.sessionTimestamps.remove((Object)uuid);
    }

    @SuppressWarnings("deprecation")
	public void reloadPlaytimeData() {
        Object object = this.config.get("playing-times");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            for (Object id : section.getKeys(false)) {
                this.totalPlaytimeMap.put(UUID.fromString((String)id), this.config.getLong("playing-times." + (String)id, 0));
            }
        }
        long millis = System.currentTimeMillis();
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            this.sessionTimestamps.put(target.getUniqueId(), millis);
        }
    }

    @SuppressWarnings("deprecation")
	public void savePlaytimeData() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            this.totalPlaytimeMap.put(player.getUniqueId(), this.getTotalPlayTime(player.getUniqueId()));
        }
        this.totalPlaytimeMap.forEachEntry((uuid, l) -> {
            this.config.set("playing-times." + uuid.toString(), (Object)l);
            return true;
        }
        );
        this.config.save();
    }

    public long getSessionPlayTime(UUID uuid) {
        long session = this.sessionTimestamps.get((Object)uuid);
        return session != this.sessionTimestamps.getNoEntryValue() ? System.currentTimeMillis() - session : 0;
    }

    public long getPreviousPlayTime(UUID uuid) {
        long stamp = this.totalPlaytimeMap.get((Object)uuid);
        return stamp == this.totalPlaytimeMap.getNoEntryValue() ? 0 : stamp;
    }

    public long getTotalPlayTime(UUID uuid) {
        return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
    }
}