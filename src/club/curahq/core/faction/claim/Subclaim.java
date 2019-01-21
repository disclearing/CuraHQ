package club.curahq.core.faction.claim;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import club.curahq.core.faction.type.Faction;
import club.curahq.core.util.GenericUtils;
import club.curahq.core.util.cuboid.Cuboid;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Subclaim extends Claim implements Cloneable, ConfigurationSerializable {
    private final Set<UUID> accessibleMembers;

    public Subclaim(final Map<String, Object> map) {
        super(map);
        (this.accessibleMembers = new HashSet<UUID>()).addAll(GenericUtils.createList(map.get("accessibleMembers"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    public Subclaim(final Faction faction, final Location location) {
        super(faction, location, location);
        this.accessibleMembers = new HashSet<UUID>();
    }

    public Subclaim(final Faction faction, final Location location1, final Location location2) {
        super(faction, location1, location2);
        this.accessibleMembers = new HashSet<UUID>();
    }

    public Subclaim(final Faction faction, final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        super(faction, world, x1, y1, z1, x2, y2, z2);
        this.accessibleMembers = new HashSet<UUID>();
    }

    public Subclaim(final Faction faction, final Cuboid cuboid) {
        this(faction, cuboid.getWorld(), cuboid.getX1(), cuboid.getY1(), cuboid.getZ1(), cuboid.getX2(), cuboid.getY2(), cuboid.getZ2());
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.remove("subclaims");
        map.put("accessibleMembers", this.accessibleMembers.stream().map(UUID::toString).collect(Collectors.toList()));
        return map;
    }

    public Set<UUID> getAccessibleMembers() {
        return this.accessibleMembers;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Subclaim)) {
            return false;
        }
        if(!super.equals(o)) {
            return false;
        }
        final Subclaim blocks = (Subclaim) o;
        if(this.accessibleMembers != null) {
            if(!this.accessibleMembers.equals(blocks.accessibleMembers)) {
                return false;
            }
        } else if(blocks.accessibleMembers != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.accessibleMembers != null) ? this.accessibleMembers.hashCode() : 0);
        return result;
    }

    @Override
    public Subclaim clone() {
        return (Subclaim) super.clone();
    }
}
