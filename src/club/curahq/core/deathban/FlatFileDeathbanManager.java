package club.curahq.core.deathban;

import java.util.LinkedHashMap; 
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import club.curahq.core.Core;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.util.Config;
import club.curahq.core.util.PersistableLocation;
import club.curahq.core.util.core.ConfigUtil;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

public class FlatFileDeathbanManager
implements DeathbanManager {
    private final Core plugin;
    private TObjectIntMap<UUID> livesMap;
    private Config livesConfig;
    public FlatFileDeathbanManager(Core plugin) {
        this.plugin = plugin;
        this.reloadDeathbanData();
    }

    @Override
    public TObjectIntMap<UUID> getLivesMap() {
        return this.livesMap;
    }

    @Override
    public int getLives(UUID uuid) {
        return this.livesMap.get((Object)uuid);
    }
 
    
    @Override
    public int setLives(UUID uuid, int lives) {
    	
        this.livesMap.put((UUID)uuid, lives);
        return lives;
    }

    @Override
    public int addLives(UUID uuid, int amount) {
        return this.livesMap.adjustOrPutValue((UUID)uuid, amount, amount);
    }

    @Override
    public int takeLives(UUID uuid, int amount) {
        return this.setLives(uuid, this.getLives(uuid) - amount);
    }

    @Override
    public long getDeathBanMultiplier(Player player) {
        int i = 5;
        while ((long)i < TimeUnit.MILLISECONDS.toSeconds(DeathbanManager.MAX_DEATHBAN_TIME)) {
            if (player.hasPermission("hcf.deathban.seconds." + i)) {
                return i * 1000;
            }
            ++i;
        }
        return ConfigUtil.DEFAULT_DEATHBAN_DURATION;
    }

    @Override
    public Deathban applyDeathBan(Player player, String reason) {
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = this.getDeathBanMultiplier(player);
        if (factionAt.isSafezone()) {
            duration = 0;
        }
        if (!factionAt.isDeathban()) {
            duration /= 2;
        }
        if (player.hasPermission("hcf.deathban.bypass")) {
            return null;
        }
        return applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
    }

    @Override
    public Deathban applyDeathBan(UUID uuid, Deathban deathban) {
        plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }

    @Override
    public void reloadDeathbanData() {
        this.livesConfig = new Config((JavaPlugin)this.plugin, "lives");
        Object object = this.livesConfig.get("lives");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            this.livesMap = new TObjectIntHashMap<UUID>(keys.size(), 0.5f, 0);
            for (String id : keys) {
                this.livesMap.put((UUID)UUID.fromString(id), this.livesConfig.getInt(String.valueOf(section.getCurrentPath()) + "." + id));
            }
        } else {
            this.livesMap = new TObjectIntHashMap<UUID>(10, 0.5f, 0);
        }
    }

    @Override
    public void saveDeathbanData() {
        LinkedHashMap<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
        this.livesMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        }
        );
        this.livesConfig.set("lives", saveMap);
        this.livesConfig.save();
    }
}