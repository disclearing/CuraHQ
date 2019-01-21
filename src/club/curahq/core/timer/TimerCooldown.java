package club.curahq.core.timer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import club.curahq.core.Core;
import club.curahq.core.timer.event.TimerExpireEvent;

public class TimerCooldown {

    private BukkitTask eventNotificationTask;

    private final Timer timer;
    private final UUID owner; // the UUID of user that this timer runnable is for
    private long expiryMillis; // the milliseconds until this expires
    private long pauseMillis; // milliseconds that were remaining when was paused

    public TimerCooldown(Timer timer, long duration) {
        this.owner = null;
        this.timer = timer;
        this.setRemaining(duration);

    }

    public TimerCooldown(Timer timer, UUID playerUUID, long duration) {
        this.timer = timer;
        this.owner = playerUUID;
        this.setRemaining(duration);

    }

    public Timer getTimer() {
        return timer;
    }

    public long getRemaining() {
        return this.getRemaining(false);
    }

    public long getRemaining(boolean ignorePaused) {
        if (!ignorePaused && this.pauseMillis != 0L) {
            return this.pauseMillis; 
        }

        return this.expiryMillis - System.currentTimeMillis();
    }

    public long getExpiryMillis() {
        return this.expiryMillis;
    }

    public void setRemaining(long remaining) {
        this.setExpiryMillis(remaining);
    }

    private void setExpiryMillis(long remainingMillis) {
        long expiryMillis = System.currentTimeMillis() + remainingMillis;
        if (expiryMillis == this.expiryMillis) {
            return;
        }

        this.expiryMillis = expiryMillis;
        if (remainingMillis > 0L) {
            if (this.eventNotificationTask != null) {
                this.eventNotificationTask.cancel();
            }

            this.eventNotificationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new TimerExpireEvent(TimerCooldown.this.owner, TimerCooldown.this.timer));
                }
            }.runTaskLater(Core.getPlugin(), remainingMillis / 50L);
        }
    }

    public long getPauseMillis() {
        return this.pauseMillis;
    }

    public void setPauseMillis(long pauseMillis) {
        this.pauseMillis = pauseMillis;
    }

    public boolean isPaused() {
        return this.pauseMillis != 0L;
    }

    public void setPaused(boolean paused) {
        if (paused != this.isPaused()) {
            if (paused) {
                this.pauseMillis = getRemaining(true);
                this.cancel();
            } else {
                this.setExpiryMillis(this.pauseMillis);
                this.pauseMillis = 0L;
            }
        }
    }
    public boolean cancel() {
        if (this.eventNotificationTask != null) {
            this.eventNotificationTask.cancel();
            return true;
        }

        return false;
    }
}
