package club.curahq.core.faction.claim;

import com.google.common.base.Preconditions;

import club.curahq.core.Core;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.PlayerFaction;
import club.curahq.core.util.cuboid.Cuboid;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class ClaimSelection implements Cloneable {
    private final UUID uuid;
    private final World world;
    private long lastUpdateMillis;
    private Location pos1;
    private Location pos2;

    public ClaimSelection(final World world) {
        this.uuid = UUID.randomUUID();
        this.world = world;
    }

    public ClaimSelection(final World world, final Location pos1, final Location pos2) {
        this.uuid = UUID.randomUUID();
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public World getWorld() {
        return this.world;
    }

    public int getPrice(final PlayerFaction playerFaction, final boolean selling) {
        Preconditions.checkNotNull((Object) playerFaction, (Object) "Player faction cannot be null");
        return (this.pos1 == null || this.pos2 == null) ? 0 : Core.getPlugin().getClaimHandler().calculatePrice(new Cuboid(this.pos1, this.pos2), playerFaction.getClaims().size(), selling);
    }

    public Claim toClaim(final Faction faction) {
        Preconditions.checkNotNull((Object) faction, (Object) "Faction cannot be null");
        return (this.pos1 == null || this.pos2 == null) ? null : new Claim(faction, this.pos1, this.pos2);
    }

    public long getLastUpdateMillis() {
        return this.lastUpdateMillis;
    }

    public Location getPos1() {
        return this.pos1;
    }

    public void setPos1(final Location location) {
        Preconditions.checkNotNull((Object) location, (Object) "The location cannot be null");
        this.pos1 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    public Location getPos2() {
        return this.pos2;
    }

    public void setPos2(final Location location) {
        Preconditions.checkNotNull((Object) location, (Object) "The location is null");
        this.pos2 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    public boolean hasBothPositionsSet() {
        return this.pos1 != null && this.pos2 != null;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ClaimSelection)) {
            return false;
        }
        final ClaimSelection that = (ClaimSelection) o;
        Label_0054:
        {
            if(this.uuid != null) {
                if(this.uuid.equals(that.uuid)) {
                    break Label_0054;
                }
            } else if(that.uuid == null) {
                break Label_0054;
            }
            return false;
        }
        Label_0087:
        {
            if(this.world != null) {
                if(this.world.equals(that.world)) {
                    break Label_0087;
                }
            } else if(that.world == null) {
                break Label_0087;
            }
            return false;
        }
        Label_0120:
        {
            if(this.pos1 != null) {
                if(this.pos1.equals((Object) that.pos1)) {
                    break Label_0120;
                }
            } else if(that.pos1 == null) {
                break Label_0120;
            }
            return false;
        }
        if(this.pos2 != null) {
            if(!this.pos2.equals((Object) that.pos2)) {
                return false;
            }
        } else if(that.pos2 != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (this.uuid != null) ? this.uuid.hashCode() : 0;
        result = 31 * result + ((this.world != null) ? this.world.hashCode() : 0);
        result = 31 * result + ((this.pos1 != null) ? this.pos1.hashCode() : 0);
        result = 31 * result + ((this.pos2 != null) ? this.pos2.hashCode() : 0);
        return result;
    }

    public ClaimSelection clone() {
        try {
            return (ClaimSelection) super.clone();
        } catch(CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
