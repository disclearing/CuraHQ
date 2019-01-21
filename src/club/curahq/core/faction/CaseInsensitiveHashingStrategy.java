package club.curahq.core.faction;

import net.minecraft.util.gnu.trove.strategy.HashingStrategy;

public class CaseInsensitiveHashingStrategy
  implements HashingStrategy
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 7144066967511044711L;
static final CaseInsensitiveHashingStrategy INSTANCE = new CaseInsensitiveHashingStrategy();
  
  public int computeHashCode(Object object)
  {
    return ((String)object).toLowerCase().hashCode();
  }
  
  public boolean equals(Object o1, Object o2)
  {
    return (o1.equals(o2)) || (((o1 instanceof String)) && ((o2 instanceof String)) && (((String)o1).toLowerCase().equals(((String)o2).toLowerCase())));
  }
}
