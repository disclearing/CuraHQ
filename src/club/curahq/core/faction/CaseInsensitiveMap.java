package club.curahq.core.faction;

import java.util.Map;
import net.minecraft.util.gnu.trove.map.hash.TCustomHashMap;

public class CaseInsensitiveMap<V>
  extends TCustomHashMap<String, V>
{
  @SuppressWarnings("unchecked")
public CaseInsensitiveMap()
  {
    super(CaseInsensitiveHashingStrategy.INSTANCE);
  }
  
  @SuppressWarnings("unchecked")
public CaseInsensitiveMap(Map<? extends String, ? extends V> map)
  {
    super(CaseInsensitiveHashingStrategy.INSTANCE, map);
  }
}
