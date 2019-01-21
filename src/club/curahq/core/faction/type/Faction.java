package club.curahq.core.faction.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import club.curahq.core.Core;
import club.curahq.core.faction.event.FactionRenameEvent;
import club.curahq.core.faction.struct.Relation;
import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.ConfigUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public abstract class Faction implements ConfigurationSerializable, Listener {
    protected final UUID uniqueID;
    public long lastRenameMillis;
    protected String name;
    protected boolean needSave;
    protected long creationMillis;
    protected double dtrLossMultiplier = 1;
    protected double deathbanMultiplier = 1;
    protected boolean safezone;
    protected boolean loading;
    protected boolean locked;


    public Faction(String name) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.needSave = false;
        loading = false;
    }

    @SuppressWarnings("rawtypes")
	public Faction(Map map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.name = (String) map.get("name");
        this.creationMillis = Long.parseLong((String) map.get("creationMillis"));
        this.lastRenameMillis = Long.parseLong((String) map.get("lastRenameMillis"));
        this.deathbanMultiplier = ((Double) map.get("deathbanMultiplier")).doubleValue();
        this.safezone = ((Boolean) map.get("safezone")).booleanValue();
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("name", this.name);
        map.put("creationMillis", Long.toString(this.creationMillis));
        map.put("lastRenameMillis", Long.toString(this.lastRenameMillis));
        map.put("deathbanMultiplier", this.deathbanMultiplier);
        map.put("safezone", this.safezone);
        return map;
    }

    public void flagForSave(boolean value) {
        this.needSave = value;
    }
    public boolean isSaveNeeded(){
        return needSave;
    }
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getName() {
        return this.name;
    }

    public boolean setName(final String name) {
        return this.setName(name, (CommandSender) Bukkit.getConsoleSender());
    }

    public boolean setName(final String name, final CommandSender sender) {
        if(this.name.equals(name)) {
            return false;
        }
        final FactionRenameEvent event = new FactionRenameEvent(this, sender, this.name, name);
        Bukkit.getPluginManager().callEvent((Event) event);
        if(event.isCancelled()) {
            return false;
        }
        this.lastRenameMillis = System.currentTimeMillis();
        this.name = name;
        return true;
    }

    public Relation getFactionRelation(final Faction faction) {
        if(faction == null) {
            return Relation.ENEMY;
        }
        if(faction instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction) faction;
            if(playerFaction.equals(this)) {
                return Relation.MEMBER;
            }
            if(playerFaction.getAllied().contains(this.uniqueID)) {
                return Relation.ALLY;
            }
        }
        return Relation.ENEMY;
    }

    @SuppressWarnings("deprecation")
	public Relation getRelation(final CommandSender sender) {
        if(!(sender instanceof Player)) {
            return Relation.ENEMY;
        }
        final Player player = (Player) sender;
        return this.getFactionRelation(Core.getPlugin().getFactionManager().getPlayerFaction(player));
    }

    public String getDisplayName(final CommandSender sender) {
        return (this.safezone ? ConfigUtil.SAFEZONE_COLOUR : this.getRelation(sender).toChatColour()) + this.name;
    }

    public String getDisplayName(final Faction other) {
        return this.getFactionRelation(other).toChatColour() + this.name;
    }

    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + this.getDisplayName(sender));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }

    public boolean isDeathban() {
        return !this.safezone && this.deathbanMultiplier > 0.0;
    }

    public void setDeathban(final boolean deathban) {
        if(deathban != this.isDeathban()) {
            this.deathbanMultiplier = (deathban ? 1.0 : 0.0);
        }
    }

    public double getDeathbanMultiplier() {
        return this.deathbanMultiplier;
    }

    public void setDeathbanMultiplier(final double deathbanMultiplier) {
        Preconditions.checkArgument(deathbanMultiplier >= 0.0, (Object) "Deathban multiplier may not be negative");
        this.deathbanMultiplier = deathbanMultiplier;
    }

    public double getDtrLossMultiplier() {
        return this.dtrLossMultiplier;
    }

    public void setDtrLossMultiplier(final double dtrLossMultiplier) {
        this.dtrLossMultiplier = dtrLossMultiplier;
    }

    public void setLocked(boolean locked) {this.locked = locked; }

    public boolean isLocked(){ return this.locked; }

    public boolean isSafezone() {
        return this.safezone;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Faction)) {
            return false;
        }
        final Faction faction = (Faction) o;
        if(this.creationMillis != faction.creationMillis) {
            return false;
        }
        if(this.lastRenameMillis != faction.lastRenameMillis) {
            return false;
        }
        if(Double.compare(faction.dtrLossMultiplier, this.dtrLossMultiplier) != 0) {
            return false;
        }
        if(Double.compare(faction.deathbanMultiplier, this.deathbanMultiplier) != 0) {
            return false;
        }
        if(this.safezone != faction.safezone) {
            return false;
        }
        Label_0127:
        {
            if(this.uniqueID != null) {
                if(this.uniqueID.equals(faction.uniqueID)) {
                    break Label_0127;
                }
            } else if(faction.uniqueID == null) {
                break Label_0127;
            }
            return false;
        }
        if(this.name != null) {
            if(!this.name.equals(faction.name)) {
                return false;
            }
        } else if(faction.name != null) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        int result = (this.uniqueID != null) ? this.uniqueID.hashCode() : 0;
        result = 31 * result + ((this.name != null) ? this.name.hashCode() : 0);
        result = 31 * result + (int) (this.creationMillis ^ this.creationMillis >>> 32);
        result = 31 * result + (int) (this.lastRenameMillis ^ this.lastRenameMillis >>> 32);
        long temp = Double.doubleToLongBits(this.dtrLossMultiplier);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.deathbanMultiplier);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        result = 31 * result + (this.safezone ? 1 : 0);
        return result;
    }


}
