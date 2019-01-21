package club.curahq.core.visualise;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import club.curahq.core.Core;
import club.curahq.core.faction.struct.Relation;
import club.curahq.core.faction.type.Faction;

public enum VisualType {
    END_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @SuppressWarnings("deprecation")
				@Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.LIME.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    SPAWN_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @SuppressWarnings("deprecation")
				@Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CLAIM_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @SuppressWarnings("deprecation")
				@Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.SILVER.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CLAIM_MAP {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                private final Material[] types = {Material.NETHERRACK, Material.SANDSTONE, Material.BOOKSHELF, Material.SNOW_BLOCK, Material.LOG, Material.LAPIS_BLOCK, Material.NETHER_BRICK, Material.DIAMOND_ORE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE};
                private int materialCounter = 0;

                @SuppressWarnings("deprecation")
				@Override
                VisualBlockData generate(final Player player, final Location location) {
                    final int y = location.getBlockY();
                    Material material = null;
                    if(y == 0 || y % 3 == 0){
                        material = this.types[materialCounter];
                    }
                    if(material != null) {
                        return new VisualBlockData(material);
                    }
                    final Faction faction = Core.getPlugin().getFactionManager().getFactionAt(location);
                    DyeColor color;
                    if(faction != null){
                        color = faction.getRelation(player).toDyeColour();
                    }else{
                        color = Relation.ENEMY.toDyeColour();
                    }
                    return new VisualBlockData(Material.STAINED_GLASS, color.getData());
                }

                @Override
                ArrayList<VisualBlockData> bulkGenerate(final Player player, final Iterable<Location> locations) {
                    final ArrayList<VisualBlockData> result = super.bulkGenerate(player, locations);
                    if(++this.materialCounter == this.types.length) {
                        this.materialCounter = 0;
                    }
                    return result;
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CREATE_CLAIM_SELECTION {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData((location.getBlockY() % 3 != 0) ? Material.GLASS : Material.DIAMOND_BLOCK);
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    };

    abstract BlockFiller blockFiller();
}
