package club.curahq.core.classes.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData {
    public static final double MIN_ENERGY = 0.0;
    public static final double MAX_ENERGY = 120.0;
    public static final long MAX_ENERGY_MILLIS = 120000L;
    private static final double ENERGY_PER_MILLISECOND = 1.25;
    public long buffCooldown;
    public BukkitTask heldTask;
    private long energyStart;

    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }

    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }

    public long getEnergyMillis() {
        if(this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(MAX_ENERGY_MILLIS, (long) (ENERGY_PER_MILLISECOND * (System.currentTimeMillis() - this.energyStart)));
    }

    public double getEnergy() {
        final double value = this.getEnergyMillis() / 1000.0;
        return Math.round(value * 10.0) / 10.0;
    }

    public void setEnergy(final double energy) {
        Preconditions.checkArgument(energy >= MIN_ENERGY, (Object) "Energy cannot be less than 0.0");
        Preconditions.checkArgument(energy <= MAX_ENERGY, (Object) "Energy cannot be more than 120.0");
        this.energyStart = (long) (System.currentTimeMillis() - 1000.0 * energy);
    }
}
