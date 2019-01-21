package club.curahq.core.sotw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import club.curahq.core.Core;


public class SotwTimer {

    private SotwRunnable sotwRunnable;

    Core plugin;

    public boolean cancel() {
        if (this.sotwRunnable != null) {
            this.sotwRunnable.cancel();
            this.sotwRunnable = null;
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.sotwRunnable == null) {
            this.sotwRunnable = new SotwRunnable(this, millis);
            this.sotwRunnable.runTaskLater(Core.getPlugin(), millis / 50L);
        }
    }

    public static class SotwRunnable extends BukkitRunnable {

        private SotwTimer sotwTimer;
        private long startMillis;
        private static long endMillis;

        @SuppressWarnings("static-access")
		public SotwRunnable(SotwTimer sotwTimer, long duration) {
            this.sotwTimer = sotwTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m---*------------------------*---"));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe &6&lSOTW &ehas ended."));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m---*------------------------*---"));
            this.cancel();
            this.sotwTimer.sotwRunnable = null;
        }
    }

    public SotwRunnable getSotwRunnable() {
        return sotwRunnable;
    }

	public long getRemaining() {
        return SotwRunnable.endMillis - System.currentTimeMillis();
	}
}
