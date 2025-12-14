package de.andidoescode.powermining.managers;

import de.andidoescode.powermining.PowerMining;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class OreScannerBellManager implements Listener {

    private final PowerMining plugin;
    private final NamespacedKey oreScannerKey;
    private final NamespacedKey radiusKey;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Set<Material> oreTypes = new HashSet<>();
    private final List<BlockDisplay> activeDisplays = new ArrayList<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public OreScannerBellManager(PowerMining plugin) {
        this.plugin = plugin;
        this.oreScannerKey = new NamespacedKey(plugin, "ore_scanner_bell");
        this.radiusKey = new NamespacedKey(plugin, "scanner_radius");
        
        loadOreTypes();
    }

    public void shutdown() {
        for (BlockDisplay display : activeDisplays) {
            if (display != null && display.isValid()) {
                display.remove();
            }
        }
        activeDisplays.clear();
    }

    private void loadOreTypes() {
        List<String> oreTypeNames = plugin.getConfig().getStringList("ore-scanner-bell.ore-types");
        
        for (String name : oreTypeNames) {
            try {
                Material material = Material.valueOf(name.toUpperCase());
                oreTypes.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid ore type in config: " + name);
            }
        }
        
        if (oreTypes.isEmpty()) {
            oreTypes.addAll(getDefaultOreTypes());
        }
    }

    private Set<Material> getDefaultOreTypes() {
        return Set.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("ore-scanner-bell.enabled", true)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        if (!isOreScannerBell(item)) {
            return;
        }

        Player player = event.getPlayer();
        
        if (!player.hasPermission("powermining.use.orescannerbell")) {
            String noPermMsg = plugin.getConfig().getString("messages.no-permission", "<red>You don't have permission to do that!</red>");
            player.sendMessage(miniMessage.deserialize(noPermMsg));
            return;
        }

        int cooldownSeconds = plugin.getConfig().getInt("ore-scanner-bell.cooldown", 30);
        long cooldownMillis = cooldownSeconds * 1000L;
        
        Long lastUse = cooldowns.get(player.getUniqueId());
        if (lastUse != null) {
            long remaining = (lastUse + cooldownMillis - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                String cooldownMsg = plugin.getConfig().getString("messages.ore-scanner-cooldown", 
                    "<red>Ore Scanner is on cooldown! <yellow>{seconds}</yellow> seconds remaining.</red>");
                player.sendMessage(miniMessage.deserialize(cooldownMsg.replace("{seconds}", String.valueOf(remaining))));
                return;
            }
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        int radius = getRadius(item);
        scanForOres(player, radius);
        
        event.setCancelled(true);
    }

    private void scanForOres(Player player, int radius) {
        Location center = player.getLocation();
        World world = center.getWorld();
        
        if (world == null) {
            return;
        }

        List<Location> foundOres = new ArrayList<>();
        int highlightDuration = plugin.getConfig().getInt("ore-scanner-bell.highlight-duration", 10);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    
                    if (oreTypes.contains(block.getType())) {
                        foundOres.add(loc);
                    }
                }
            }
        }

        if (foundOres.isEmpty()) {
            String noneFoundMsg = plugin.getConfig().getString("messages.ore-scanner-none-found", 
                "<yellow>No ores found in range.</yellow>");
            player.sendMessage(miniMessage.deserialize(noneFoundMsg));
            return;
        }

        String foundMsg = plugin.getConfig().getString("messages.ore-scanner-found", 
            "<green>Found <yellow>{count}</yellow> ores nearby!</green>");
        player.sendMessage(miniMessage.deserialize(foundMsg.replace("{count}", String.valueOf(foundOres.size()))));

        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 1.0f, 1.2f);

        highlightOres(player, foundOres, highlightDuration);
    }

    private void highlightOres(Player player, List<Location> oreLocations, int durationSeconds) {
        List<BlockDisplay> displays = new ArrayList<>();
        
        for (Location loc : oreLocations) {
            BlockDisplay display = createGlowingOutline(loc, player);
            if (display != null) {
                displays.add(display);
                activeDisplays.add(display);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BlockDisplay display : displays) {
                    if (display != null && display.isValid()) {
                        display.remove();
                    }
                    activeDisplays.remove(display);
                }
            }
        }.runTaskLater(plugin, durationSeconds * 20L);
    }

    private BlockDisplay createGlowingOutline(Location blockLoc, Player player) {
        World world = blockLoc.getWorld();
        if (world == null) {
            return null;
        }

        Block block = blockLoc.getBlock();
        Material oreType = block.getType();
        
        Location spawnLoc = blockLoc.clone().add(0.5, 0.5, 0.5);
        
        BlockDisplay display = (BlockDisplay) world.spawnEntity(spawnLoc, EntityType.BLOCK_DISPLAY);
        
        display.setBlock(oreType.createBlockData());
        
        float scale = 1.02f;
        display.setTransformation(new Transformation(
            new Vector3f(-scale/2, -scale/2, -scale/2),
            new AxisAngle4f(0, 0, 0, 1),
            new Vector3f(scale, scale, scale),
            new AxisAngle4f(0, 0, 0, 1)
        ));
        
        display.setGlowing(true);
        display.setGlowColorOverride(getOreGlowColor(oreType));
        display.setBrightness(new Display.Brightness(15, 15));
        
        return display;
    }

    private org.bukkit.Color getOreGlowColor(Material ore) {
        return switch (ore) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> org.bukkit.Color.fromRGB(50, 50, 50);
            case IRON_ORE, DEEPSLATE_IRON_ORE -> org.bukkit.Color.fromRGB(210, 180, 160);
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> org.bukkit.Color.fromRGB(180, 100, 70);
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> org.bukkit.Color.fromRGB(255, 215, 0);
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> org.bukkit.Color.fromRGB(255, 0, 0);
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> org.bukkit.Color.fromRGB(0, 255, 100);
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> org.bukkit.Color.fromRGB(30, 80, 200);
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> org.bukkit.Color.fromRGB(100, 220, 255);
            case NETHER_QUARTZ_ORE -> org.bukkit.Color.fromRGB(255, 255, 255);
            case ANCIENT_DEBRIS -> org.bukkit.Color.fromRGB(100, 60, 50);
            default -> org.bukkit.Color.fromRGB(255, 255, 0);
        };
    }

    public ItemStack createOreScannerBell(int radius) {
        int maxRadius = plugin.getConfig().getInt("ore-scanner-bell.max-radius", 64);
        radius = Math.min(radius, maxRadius);
        
        ItemStack bell = new ItemStack(Material.BELL);
        ItemMeta meta = bell.getItemMeta();
        
        if (meta != null) {
            meta.displayName(Component.text("Ore Scanner Bell")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));
            
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("✦ Right-click to scan for ores")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("✦ Radius: ")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(radius + " blocks")
                    .color(NamedTextColor.AQUA)));
            lore.add(Component.text("✦ Highlights ores temporarily")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
            lore.add(Component.text("Ring the bell to reveal!")
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, true));
            
            meta.lore(lore);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(oreScannerKey, PersistentDataType.BOOLEAN, true);
            container.set(radiusKey, PersistentDataType.INTEGER, radius);
            
            bell.setItemMeta(meta);
        }
        
        return bell;
    }

    public boolean isOreScannerBell(ItemStack item) {
        if (item == null || item.getType() != Material.BELL) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        return meta.getPersistentDataContainer().has(oreScannerKey, PersistentDataType.BOOLEAN);
    }

    private int getRadius(ItemStack item) {
        if (item == null) {
            return plugin.getConfig().getInt("ore-scanner-bell.default-radius", 16);
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return plugin.getConfig().getInt("ore-scanner-bell.default-radius", 16);
        }
        
        return meta.getPersistentDataContainer().getOrDefault(
            radiusKey, 
            PersistentDataType.INTEGER, 
            plugin.getConfig().getInt("ore-scanner-bell.default-radius", 16)
        );
    }
}
