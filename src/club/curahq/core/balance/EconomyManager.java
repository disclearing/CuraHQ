package club.curahq.core.balance;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public abstract interface EconomyManager
{
  public static final char ECONOMY_SYMBOL = '$';
  
  public abstract TObjectIntMap<UUID> getBalanceMap();
  
  public abstract int getBalance(UUID paramUUID);
  
  public abstract int setBalance(UUID paramUUID, int paramInt);
  
  public abstract int addBalance(UUID paramUUID, int paramInt);
  
  public abstract int subtractBalance(UUID paramUUID, int paramInt);
  
  public abstract void reloadEconomyData();
  
  public abstract void saveEconomyData();
}
