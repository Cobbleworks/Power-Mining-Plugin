package de.andidoescode.powermining.managers;

import de.andidoescode.powermining.PowerMining;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Finds spawners in already loaded chunks without reading live world data off
 * the server thread. The recovery compass retains the last located room.
 */
public final class DungeonLocatorManager implements Listener {
    private final PowerMining plugin;
    private final NamespacedKey locatorKey;
    private final NamespacedKey radiusKey;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Set<UUID> scanning = new HashSet<>();
    private final Set<BlockDisplay> activeMarkers = new HashSet<>();

    public DungeonLocatorManager(PowerMining plugin) {
        this.plugin = plugin;
        locatorKey = new NamespacedKey(plugin, "dungeon_locator");
        radiusKey = new NamespacedKey(plugin, "dungeon_locator_radius");
    }

    public ItemStack createDungeonLocator(int requestedRadius) {
        int radius = Math.max(1, Math.min(requestedRadius,
                plugin.getConfig().getInt("dungeon-locator.max-radius", 48)));
        ItemStack item = new ItemStack(Material.RECOVERY_COMPASS);
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.displayName(Component.text("Dungeon Locator", NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Right-click to search loaded terrain", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Detection radius: " + radius + " blocks", NamedTextColor.AQUA)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Points to the nearest mob spawner found", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(locatorKey, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(radiusKey, PersistentDataType.INTEGER, radius);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isDungeonLocator(ItemStack item) {
        return item != null && item.getType() == Material.RECOVERY_COMPASS && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(locatorKey, PersistentDataType.BYTE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND
                || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                || !isDungeonLocator(event.getItem())) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        if (!plugin.getConfig().getBoolean("dungeon-locator.enabled", true)) {
            player.sendMessage(Component.text("Dungeon locators are disabled.", NamedTextColor.RED));
            return;
        }
        if (!player.hasPermission("powermining.use.dungeonlocator")) {
            player.sendMessage(Component.text("You do not have permission to use this item.", NamedTextColor.RED));
            return;
        }
        if (!scanning.add(player.getUniqueId())) {
            player.sendMessage(Component.text("A dungeon scan is already running.", NamedTextColor.YELLOW));
            return;
        }

        long cooldownMillis = Math.max(0L,
                plugin.getConfig().getLong("dungeon-locator.cooldown-seconds", 15)) * 1000L;
        long remaining = cooldowns.getOrDefault(player.getUniqueId(), 0L) + cooldownMillis - System.currentTimeMillis();
        if (remaining > 0) {
            scanning.remove(player.getUniqueId());
            player.sendMessage(Component.text("Dungeon locator ready in " + ((remaining + 999) / 1000) + " seconds.",
                    NamedTextColor.YELLOW));
            return;
        }

        Location origin = player.getLocation().getBlock().getLocation();
        int configuredMaximum = Math.max(1, plugin.getConfig().getInt("dungeon-locator.max-radius", 48));
        int storedRadius = event.getItem().getItemMeta().getPersistentDataContainer().getOrDefault(
                radiusKey, PersistentDataType.INTEGER,
                plugin.getConfig().getInt("dungeon-locator.default-radius", 32));
        int radius = Math.max(1, Math.min(storedRadius, configuredMaximum));
        List<ChunkSnapshot> snapshots = captureLoadedSnapshots(origin, radius);
        int centerX = origin.getBlockX();
        int centerY = origin.getBlockY();
        int centerZ = origin.getBlockZ();
        int minimumY = origin.getWorld().getMinHeight();
        int maximumY = origin.getWorld().getMaxHeight();
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Component.text("Searching loaded terrain for mob spawners…", NamedTextColor.GRAY));

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            BlockPosition nearest = findNearestSpawner(snapshots, centerX, centerY,
                    centerZ, minimumY, maximumY, radius);
            plugin.getServer().getScheduler().runTask(plugin, () -> completeScan(player.getUniqueId(), origin, nearest));
        });
    }

    private List<ChunkSnapshot> captureLoadedSnapshots(Location origin, int radius) {
        World world = origin.getWorld();
        List<ChunkSnapshot> snapshots = new ArrayList<>();
        int minChunkX = (origin.getBlockX() - radius) >> 4;
        int maxChunkX = (origin.getBlockX() + radius) >> 4;
        int minChunkZ = (origin.getBlockZ() - radius) >> 4;
        int maxChunkZ = (origin.getBlockZ() + radius) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (world.isChunkLoaded(chunkX, chunkZ)) {
                    snapshots.add(world.getChunkAt(chunkX, chunkZ).getChunkSnapshot(false, false, false));
                }
            }
        }
        return snapshots;
    }

    private static BlockPosition findNearestSpawner(List<ChunkSnapshot> snapshots, int centerX, int centerY,
                                                    int centerZ, int minimumY, int maximumY, int radius) {
        long radiusSquared = (long) radius * radius;
        List<BlockPosition> found = new ArrayList<>();

        for (ChunkSnapshot snapshot : snapshots) {
            int startX = snapshot.getX() << 4;
            int startZ = snapshot.getZ() << 4;
            for (int localX = 0; localX < 16; localX++) {
                int worldX = startX + localX;
                int deltaX = worldX - centerX;
                for (int localZ = 0; localZ < 16; localZ++) {
                    int worldZ = startZ + localZ;
                    int deltaZ = worldZ - centerZ;
                    long horizontal = (long) deltaX * deltaX + (long) deltaZ * deltaZ;
                    if (horizontal > radiusSquared) continue;
                    int minY = Math.max(minimumY, centerY - radius);
                    int maxY = Math.min(maximumY - 1, centerY + radius);
                    for (int y = minY; y <= maxY; y++) {
                        int deltaY = y - centerY;
                        if (withinRadius(deltaX, deltaY, deltaZ, radius)
                                && snapshot.getBlockType(localX, y, localZ) == Material.SPAWNER) {
                            found.add(new BlockPosition(worldX, y, worldZ));
                        }
                    }
                }
            }
        }

        return found.stream().min(Comparator.comparingLong(position -> position.distanceSquared(centerX, centerY, centerZ)))
                .orElse(null);
    }

    private void completeScan(UUID playerId, Location origin, BlockPosition nearest) {
        scanning.remove(playerId);
        Player player = plugin.getServer().getPlayer(playerId);
        if (player == null || !player.isOnline() || !player.getWorld().equals(origin.getWorld())) return;
        if (nearest == null) {
            player.sendMessage(Component.text("No mob spawner was found in loaded terrain within range.", NamedTextColor.YELLOW));
            return;
        }

        if (!player.getWorld().isChunkLoaded(nearest.x() >> 4, nearest.z() >> 4)) {
            player.sendMessage(Component.text("The detected spawner's chunk unloaded before it could be marked.",
                    NamedTextColor.YELLOW));
            return;
        }
        Location target = new Location(player.getWorld(), nearest.x(), nearest.y(), nearest.z());
        if (target.getBlock().getType() != Material.SPAWNER) {
            player.sendMessage(Component.text("The detected spawner changed before it could be marked.", NamedTextColor.YELLOW));
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (isDungeonLocator(held)) {
            CompassMeta meta = (CompassMeta) held.getItemMeta();
            meta.setLodestone(target);
            meta.setLodestoneTracked(false);
            held.setItemMeta(meta);
        }
        markForPlayer(player, target);
        int distance = (int) Math.round(origin.distance(target));
        player.sendMessage(Component.text("Nearest mob spawner marked " + distance + " blocks away at "
                + nearest.x() + ", " + nearest.y() + ", " + nearest.z() + ".", NamedTextColor.GREEN));
    }

    private void markForPlayer(Player player, Location blockLocation) {
        Location displayLocation = blockLocation.clone().add(0.5, 0.5, 0.5);
        BlockDisplay display = displayLocation.getWorld().spawn(displayLocation, BlockDisplay.class, marker -> {
            marker.setBlock(Material.SPAWNER.createBlockData());
            marker.setTransformation(new Transformation(
                    new Vector3f(-0.51f, -0.51f, -0.51f), new AxisAngle4f(),
                    new Vector3f(1.02f, 1.02f, 1.02f), new AxisAngle4f()));
            marker.setGlowing(true);
            marker.setGlowColorOverride(Color.FUCHSIA);
            marker.setBrightness(new Display.Brightness(15, 15));
            marker.setVisibleByDefault(false);
            marker.setPersistent(false);
        });
        activeMarkers.add(display);
        player.showEntity(plugin, display);
        long duration = Math.max(20L, plugin.getConfig().getLong("dungeon-locator.marker-duration-ticks", 200L));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            activeMarkers.remove(display);
            if (display.isValid()) display.remove();
        }, duration);
    }

    public void shutdown() {
        activeMarkers.forEach(marker -> {
            if (marker.isValid()) marker.remove();
        });
        activeMarkers.clear();
        scanning.clear();
    }

    static boolean withinRadius(int deltaX, int deltaY, int deltaZ, int radius) {
        return (long) deltaX * deltaX + (long) deltaY * deltaY + (long) deltaZ * deltaZ
                <= (long) radius * radius;
    }

    private record BlockPosition(int x, int y, int z) {
        private long distanceSquared(int otherX, int otherY, int otherZ) {
            long xDistance = x - otherX;
            long yDistance = y - otherY;
            long zDistance = z - otherZ;
            return xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
        }
    }
}
