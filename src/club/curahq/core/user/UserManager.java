package club.curahq.core.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.base.Preconditions;

import club.curahq.core.Core;
import club.curahq.core.util.Config;
import club.curahq.core.util.core.ServerParticipator;
import compat.com.google.common.collect.GuavaCompat;


public class UserManager implements Listener
{
    private Core plugin;
    private final Map<UUID, FactionUser> users;
    private Config userConfig;
    private final ConsoleUser console;
    private Map<UUID, ServerParticipator> participators;
    
    public UserManager(final Core plugin) {
        this.users = new HashMap<UUID, FactionUser>();
        this.reloadParticipatorData();
        final ServerParticipator participator = this.participators.get(ConsoleUser.CONSOLE_UUID);
        if (participator != null) {
            this.console = (ConsoleUser)participator;
        }
        else {
            this.console = new ConsoleUser();
            this.participators.put(ConsoleUser.CONSOLE_UUID, this.console);
        }
    }
    
    public ConsoleUser getConsole() {
        return this.console;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.users.putIfAbsent(uuid, new FactionUser(uuid));
    }
    
    public Map<UUID, FactionUser> getUsers() {
        return this.users;
    }
    
    public FactionUser getUserAsync(final UUID uuid) {
        synchronized (this.users) {
            final FactionUser revert;
            final FactionUser user = this.users.putIfAbsent(uuid, revert = new FactionUser(uuid));
            // monitorexit(this.users)
            return GuavaCompat.firstNonNull(user, revert);
        }
    }
    
    public FactionUser getUser(final UUID uuid) {
        final FactionUser revert;
        final FactionUser user = this.users.putIfAbsent(uuid, revert = new FactionUser(uuid));
        return GuavaCompat.firstNonNull(user, revert);
    }
    
    public void reloadUserData() {
        this.userConfig = new Config(this.plugin, "faction-users");
        final Object object = this.userConfig.get("users");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Collection<String> keys = (Collection<String>)section.getKeys(false);
            for (final String id : keys) {
                this.users.put(UUID.fromString(id), (FactionUser)this.userConfig.get(String.valueOf(section.getCurrentPath()) + '.' + id));
            }
        }
    }
    
    public void saveUserData() {
        final Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
        final Map<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
        for (final Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("users", (Object)saveMap);
        this.userConfig.save();
    }
    
    public ServerParticipator getParticipator(final CommandSender sender) {
        Preconditions.checkNotNull((Object)sender, (Object)"CommandSender cannot be null");
        if (sender instanceof ConsoleCommandSender) {
            return this.console;
        }
        if (sender instanceof Player) {
            return this.participators.get(((Player)sender).getUniqueId());
        }
        return null;
    }
    
    public ServerParticipator getParticipator(final UUID uuid) {
        Preconditions.checkNotNull((Object)uuid, (Object)"Unique ID cannot be null");
        return this.participators.get(uuid);
    }
    
    public void reloadParticipatorData() {
        this.userConfig = new Config(Core.getPlugin(), "participators");
        final Object object = this.userConfig.get("participators");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            this.participators = new HashMap<UUID, ServerParticipator>(keys.size());
            for (final String id : keys) {
                this.participators.put(UUID.fromString(id), (ServerParticipator)this.userConfig.get("participators." + id));
            }
        }
        else {
            this.participators = new HashMap<UUID, ServerParticipator>();
        }
    }
    
    public void saveParticipatorData() {
        final LinkedHashMap<String, ServerParticipator> saveMap = new LinkedHashMap<String, ServerParticipator>(this.participators.size());
        for (final Map.Entry<UUID, ServerParticipator> entry : this.participators.entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("participators", (Object)saveMap);
        this.userConfig.save();
    }
}
