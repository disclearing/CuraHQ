package club.curahq.core.deathban;

import java.util.UUID; 

import org.bukkit.entity.Player;

import club.curahq.core.util.core.ConfigUtil;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public abstract interface DeathbanManager
{
public static final long MAX_DEATHBAN_TIME = ConfigUtil.DEFAULT_DEATHBAN_DURATION;
  
  public abstract TObjectIntMap<UUID> getLivesMap();
  
  public abstract int getLives(UUID paramUUID);
  
  public abstract int setLives(UUID paramUUID, int paramInt);
  
  public abstract int addLives(UUID paramUUID, int paramInt);
  
  public abstract int takeLives(UUID paramUUID, int paramInt);
  
  public abstract long getDeathBanMultiplier(Player paramPlayer);
  
  public abstract Deathban applyDeathBan(Player paramPlayer, String paramString);
  
  public abstract Deathban applyDeathBan(UUID paramUUID, Deathban paramDeathban);
  
  public abstract void reloadDeathbanData();
  
  public abstract void saveDeathbanData();
}
