package club.curahq.core.util.itemdb;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract interface ItemDb
{
  public abstract void reloadItemDatabase();
  
  public abstract ItemStack getPotion(String paramString);
  
  public abstract ItemStack getPotion(String paramString, int paramInt);
  
  public abstract ItemStack getItem(String paramString);
  
  public abstract ItemStack getItem(String paramString, int paramInt);
  
  public abstract String getName(ItemStack paramItemStack);
  
  @Deprecated
  public abstract String getPrimaryName(ItemStack paramItemStack);
  
  public abstract String getNames(ItemStack paramItemStack);
  
  public abstract List<ItemStack> getMatching(Player paramPlayer, String[] paramArrayOfString);
}
