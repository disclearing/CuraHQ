package club.curahq.core.faction.claim;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import club.curahq.core.Core;
import club.curahq.core.faction.CaseInsensitiveMap;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.util.GenericUtils;
import club.curahq.core.util.cuboid.Cuboid;
import club.curahq.core.util.cuboid.NamedCuboid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Claim extends NamedCuboid implements Cloneable, ConfigurationSerializable {
    private static final Random RANDOM = new Random();
    @SuppressWarnings("rawtypes")
	private final Map subclaims = new CaseInsensitiveMap();
    private final UUID claimUniqueID;
    private final UUID factionUUID;
    private Faction faction;
    private boolean loaded = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Claim(Map map) {
        super(map);
        this.name = (String) map.get("name");
        this.claimUniqueID = UUID.fromString((String) map.get("claimUUID"));
        this.factionUUID = UUID.fromString((String) map.get("factionUUID"));
        Iterator var2 = GenericUtils.createList(map.get("subclaims"), Subclaim.class).iterator();

        while(var2.hasNext()) {
            Subclaim subclaim = (Subclaim) var2.next();
            this.subclaims.put(subclaim.getName(), subclaim);
        }

    }

    public Claim(Faction faction, Location location) {
        super(location, location);
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public Claim(Faction faction, Location location1, Location location2) {
        super(location1, location2);
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public Claim(Faction faction, World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        super(world, x1, y1, z1, x2, y2, z2);
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    public Claim(Faction faction, Cuboid cuboid) {
        super(cuboid);
        this.name = this.generateName();
        this.factionUUID = faction.getUniqueID();
        this.claimUniqueID = UUID.randomUUID();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> serialize() {
        final Map<String, Object> map =  super.serialize();
        map.put("name", this.name);
        map.put("claimUUID", this.claimUniqueID.toString());
        map.put("factionUUID", this.factionUUID.toString());
        map.put("subclaims", new ArrayList(this.subclaims.values()));
        return map;
    }

    private String generateName() {
        return String.valueOf(Claim.RANDOM.nextInt(899) + 100);
    }

    public UUID getClaimUniqueID() {
        return this.claimUniqueID;
    }

    public ClaimableFaction getFaction() {
        if(!this.loaded && this.faction == null) {
            this.faction = Core.getPlugin().getFactionManager().getFaction(this.factionUUID);
            this.loaded = true;
        }
        return (this.faction instanceof ClaimableFaction) ? ((ClaimableFaction) this.faction) : null;
    }

    @SuppressWarnings("unchecked")
	public Collection<Subclaim> getSubclaims() {
        return this.subclaims.values();
    }

    public Subclaim getSubclaim(String name) {
        return (Subclaim) this.subclaims.get(name);
    }

    public String getFormattedName() {
        return this.getName() + ": (" + this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ") - (" + this.worldName + ", " + this.x2 + ", " + this.y2 + ", " + this.z2 + ')';
    }

    public Claim clone() {
        return (Claim) super.clone();
    }

    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Claim blocks = (Claim) o;
        if(this.loaded != blocks.loaded) {
            return false;
        }
        Label_0077:
        {
            if(this.subclaims != null) {
                if(this.subclaims.equals(blocks.subclaims)) {
                    break Label_0077;
                }
            } else if(blocks.subclaims == null) {
                break Label_0077;
            }
            return false;
        }
        Label_0110:
        {
            if(this.claimUniqueID != null) {
                if(this.claimUniqueID.equals(blocks.claimUniqueID)) {
                    break Label_0110;
                }
            } else if(blocks.claimUniqueID == null) {
                break Label_0110;
            }
            return false;
        }
        Label_0143:
        {
            if(this.factionUUID != null) {
                if(this.factionUUID.equals(blocks.factionUUID)) {
                    break Label_0143;
                }
            } else if(blocks.factionUUID == null) {
                break Label_0143;
            }
            return false;
        }
        if(this.faction != null) {
            if(!this.faction.equals(blocks.faction)) {
                return false;
            }
        } else if(blocks.faction != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (this.subclaims != null) ? this.subclaims.hashCode() : 0;
        result = 31 * result + ((this.claimUniqueID != null) ? this.claimUniqueID.hashCode() : 0);
        result = 31 * result + ((this.factionUUID != null) ? this.factionUUID.hashCode() : 0);
        result = 31 * result + ((this.faction != null) ? this.faction.hashCode() : 0);
        result = 31 * result + (this.loaded ? 1 : 0);
        return result;
    }


}
