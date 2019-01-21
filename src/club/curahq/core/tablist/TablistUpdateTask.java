package club.curahq.core.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistUpdateTask implements Runnable
{
	@Override
	public void run()
	{
		TablistManager manager = TablistManager.INSTANCE;
		if (manager == null)
			return;
		for(Player online : Bukkit.getOnlinePlayers()) {
			Tablist tablist = manager.getTablist(online);
			if(tablist != null) {
				tablist.hideRealPlayers().update();
			}
		}
	}
}
