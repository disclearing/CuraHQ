package club.curahq.core.listener.fixes;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import club.curahq.core.Core;

public class BlockClipListener implements Listener{
	
		public Core a;
	
	
	  public BlockClipListener(Core paramMain)
	  {
	    this.a = paramMain;
	  }
	  
	  public void onClip(PlayerMoveEvent e)
	  {
		  
	  }
}
