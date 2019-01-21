package club.curahq.core.visualise;

import com.google.common.base.Predicate;

import club.curahq.core.Core;
import club.curahq.core.faction.claim.Claim;
import club.curahq.core.faction.type.ClaimableFaction;
import club.curahq.core.faction.type.Faction;
import club.curahq.core.faction.type.RoadFaction;
import club.curahq.core.util.cuboid.Cuboid;
import club.curahq.core.visualise.VisualBlock;
import club.curahq.core.visualise.VisualType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class WallBorderListener
implements Listener {
    @SuppressWarnings("unused")
	private static final int WALL_BORDER_HEIGHT_BELOW_DIFF = 3;
    @SuppressWarnings("unused")
	private static final int WALL_BORDER_HEIGHT_ABOVE_DIFF = 4;
    @SuppressWarnings("unused")
	private static final int WALL_BORDER_HORIZONTAL_DISTANCE = 7;
    private final boolean useTaskInstead;
    private final Map<UUID, BukkitTask> wallBorderTask = new HashMap<UUID, BukkitTask>();
    private final Core plugin;

    public WallBorderListener(Core plugin) {
        this.plugin = plugin;
        plugin.getRandom().nextBoolean();
        this.useTaskInstead = false;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!this.useTaskInstead) {
            return;
        }
        BukkitTask task = this.wallBorderTask.remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (this.useTaskInstead) {
            this.wallBorderTask.put(player.getUniqueId(), new WarpTimerRunnable(this, player).runTaskTimer((Plugin)this.plugin, 3, 3));
            return;
        }
        final Location now = player.getLocation();
        new BukkitRunnable(){

            public void run() {
                Location location = player.getLocation();
                if (now.equals((Object)location)) {
                    WallBorderListener.this.handlePositionChanged(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                }
            }
        }.runTaskLater((Plugin)this.plugin, 4);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.useTaskInstead) {
            return;
        }
        Location to = event.getTo();
        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();
        Location from = event.getFrom();
        if (from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
            this.handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove((PlayerMoveEvent)event);
    }

    @SuppressWarnings({ "unused", "rawtypes" })
	private void handlePositionChanged(Player player, final World toWorld, final int toX, final int toY, final int toZ) {
        VisualType visualType;
        Object object;
        if (this.plugin.getTimerManager().combatTimer.getRemaining(player) > 0) {
            if (toWorld.getEnvironment() == World.Environment.THE_END) {
                object = VisualType.END_BORDER;
            }
            visualType = VisualType.SPAWN_BORDER;
            object = this.plugin.getTimerManager().combatTimer;
        } else {
            if (this.plugin.getTimerManager().invincibilityTimer.getRemaining(player) <= 0) {
                return;
            }
            visualType = VisualType.CLAIM_BORDER;
            object = this.plugin.getTimerManager().invincibilityTimer;
        }
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, new Predicate<VisualBlock>(){

            public boolean apply(VisualBlock visualBlock) {
                Location other = visualBlock.getLocation();
                if (other.getWorld().equals((Object)toWorld) && (Math.abs(toX - other.getBlockX()) > 7 || Math.abs(toY - other.getBlockY()) > 4 || Math.abs(toZ - other.getBlockZ()) > 7)) {
                    return true;
                }
                return false;
            }
        });
        int minHeight = toY - 3;
        int maxHeight = toY + 4;
        int minX = toX - 7;
        int maxX = toX + 7;
        int minZ = toZ - 7;
        int maxZ = toZ + 7;
        HashSet<Claim> added = new HashSet<Claim>();
        int x = minX;
        while (x < maxX) {
            int z = minZ;
            while (z < maxZ) {
                Faction faction = this.plugin.getFactionManager().getFactionAt(toWorld, x, z);
                if (faction instanceof ClaimableFaction && !(visualType == VisualType.SPAWN_BORDER ? !faction.isSafezone() : visualType == VisualType.CLAIM_BORDER && (faction instanceof RoadFaction || faction.isSafezone()))) {
                    Set<Claim> claims = ((ClaimableFaction)faction).getClaims();
                    for (Claim claim : claims) {
                        if (!toWorld.equals((Object)claim.getWorld())) continue;
                        added.add(claim);
                    }
                }
                ++z;
            }
            ++x;
        }
        if (!added.isEmpty()) {
            int generated = 0;
            Iterator iterator = added.iterator();
            while (iterator.hasNext()) {
                Claim claim2 = (Claim)iterator.next();
                List<Vector> edges = claim2.edges();
                for (Vector edge : edges) {
                    Location location;
                    if (Math.abs(edge.getBlockX() - toX) > 7 || Math.abs(edge.getBlockZ() - toZ) > 7 || (location = edge.toLocation(toWorld)) == null) continue;
                    Location first = location.clone();
                    first.setY(minHeight);
                    Location second = location.clone();
                    second.setY(maxHeight);
                    generated += this.plugin.getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false).size();
                }
                iterator.remove();
            }
        }
    }

    private static final class WarpTimerRunnable
    extends BukkitRunnable {
        private WallBorderListener listener;
        private Player player;
        private double lastX = Double.MAX_VALUE;
        private double lastY = Double.MAX_VALUE;
        private double lastZ = Double.MAX_VALUE;

        public WarpTimerRunnable(WallBorderListener listener, Player player) {
            this.listener = listener;
            this.player = player;
        }

        public void run() {
            Location location = this.player.getLocation();
            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();
            if (this.lastX == x && this.lastY == y && this.lastZ == z) {
                return;
            }
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.listener.handlePositionChanged(this.player, this.player.getWorld(), (int)x, (int)y, (int)z);
        }

        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.listener = null;
            this.player = null;
        }
    }

}