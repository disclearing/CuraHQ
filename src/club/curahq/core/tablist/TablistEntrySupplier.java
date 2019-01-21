package club.curahq.core.tablist;

import org.bukkit.entity.*;
import com.google.common.collect.*;

public interface TablistEntrySupplier
{
    net.minecraft.util.com.google.common.collect.Table<Integer, Integer, String> getEntries(final Player p0);
    
    String getHeader(final Player p0);
    
    String getFooter(final Player p0);
}
