package club.curahq.core.faction.struct;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import club.curahq.core.util.BukkitUtils;
import club.curahq.core.util.core.ConfigUtil;

public enum Relation {
    MEMBER(3),
    ALLY(2),
    ENEMY(1);

    private final int value;

    private Relation(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isAtLeast(final Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(final Relation relation) {
        return this.value <= relation.value;
    }

    public boolean isMember() {
        return this == Relation.MEMBER;
    }

    public boolean isAlly() {
        return this == Relation.ALLY;
    }

    public boolean isEnemy() {
        return this == Relation.ENEMY;
    }

    public String getDisplayName() {
        switch(this) {
            case ALLY: {
                return this.toChatColour() + "alliance";
            }
            default: {
                return this.toChatColour() + this.name().toLowerCase();
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
	public ChatColor toChatColour()
    {
      switch (this)
      {
      case ALLY: 
        return ConfigUtil.ALLY_COLOUR;
      case ENEMY: 
        return ConfigUtil.ENEMY_COLOUR;
      }
      return ConfigUtil.TEAMMATE_COLOUR;
    }

    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(this.toChatColour());
    }
}
