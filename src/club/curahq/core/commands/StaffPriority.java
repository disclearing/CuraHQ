package club.curahq.core.commands;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

@SuppressWarnings({ "unchecked", "rawtypes" })
public enum StaffPriority {
    OWNER(6),
    HEADADMIN(5),
    STAFFMANAGER(4),
    ADMIN(3),
    MODERATOR(2),
    TRIAL(1),
    NONE(0);
    
    private static final ImmutableMap<Integer, StaffPriority> BY_ID;
    private final int priorityLevel;

    private StaffPriority(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public static StaffPriority of(int level) {
        return (StaffPriority)((Object)BY_ID.get((Object)level));
    }

    public static StaffPriority of(Player player) {
        for (StaffPriority staffPriority : StaffPriority.values()) {
            if (!player.hasPermission("staffpriority." + staffPriority.priorityLevel)) continue;
            return staffPriority;
        }
        return NONE;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    public boolean isMoreThan(StaffPriority other) {
        return this.priorityLevel > other.priorityLevel;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (StaffPriority staffPriority : StaffPriority.values()) {
            builder.put((Object)staffPriority.priorityLevel, (Object)staffPriority);
        }
        BY_ID = builder.build();
    }
}
