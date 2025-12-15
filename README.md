# Power Mining

![Power Mining Icon](placeholder-icon.png)

A Minecraft Paper plugin that enhances the mining experience with powerful features: mounted mining, magnet hoppers, ore scanner bells, miner's helmet, escape ropes, cave compass, auto-smelting pickaxe, and miner's goggles.

## Features

### 🐴 Mounted Mining (Drill)

Automatically mine blocks while riding horses, donkeys, mules, and other mounts!

![Mounted Mining Screenshot](screenshots/mounted-mining.png)

- **How it works:** Simply ride any horse-like mount while holding a pickaxe
- **Mining area:** 3x3x3 blocks in front of your mount
- **Smart offset:** Never mines below your mount's hooves (prevents falling into holes)
- **Durability:** Respects pickaxe durability and enchantments
- **Configurable:** Adjust mining radius, interval, and Y-offset in config

### 🧲 Magnet Hopper

A special hopper that attracts nearby dropped items like a magnet!

![Magnet Hopper Screenshot](screenshots/magnet-hopper.png)

- **Particle effects:** Soul fire flame particles indicate active magnet hoppers
- **Customizable radius:** Set the attraction radius when giving the item
- **Smooth attraction:** Items are smoothly pulled towards the hopper
- **Preserves data:** Breaking the hopper drops the special item with its settings

**Command:** `/pm magnethopper [player] [radius]`

### 🔔 Ore Scanner Bell

A placeable bell that reveals all ores in a customizable radius when activated!

![Ore Scanner Bell Screenshot](screenshots/ore-scanner-bell.png)

- **Right-click activation:** Place the bell and right-click to scan
- **Glowing outlines:** Ores are highlighted with color-coded glowing outlines
- **Filter option:** Scan for specific ore types only
- **Custom duration:** Control how long highlights last
- **Cooldown:** Configurable cooldown between scans

**Command:** `/pm orebell [player] [radius] [--filter ORE_TYPE] [--duration TICKS]`

### ⛑️ Miner's Helmet

A special gold helmet that grants night vision when worn!

- **Automatic effect:** Night vision is applied automatically when wearing the helmet
- **No potions needed:** Perfect for mining in dark caves without carrying potions
- **Persistent effect:** Effect stays active as long as the helmet is worn

**Command:** `/pm helmet [player]`

### 🪢 Escape Rope

A magical lead item that remembers a location and teleports you back!

- **Set return point:** Right-click on a block to remember that location
- **Visual feedback:** Item name and lore update to show the saved coordinates
- **Teleport back:** Right-click in the air to teleport back to the saved location
- **Single use:** The escape rope is consumed when you teleport
- **Particle effects:** Beautiful portal particles on teleportation

**Command:** `/pm escaperope [player]`

### 🧭 Cave Compass

A mystical compass that points to nearby structures like dungeons, mineshafts, and strongholds!

- **Structure tracking:** Points to the nearest selected structure
- **Cycleable targets:** Shift+Right-click to cycle forward, Shift+Left-click to cycle backward
- **Available targets:\*\***
  - Mineshaft
  - Stronghold
  - Nether Fortress
  - Bastion Remnant
  - Ancient City
  - Trail Ruins
  - Trial Chambers
- **Distance display:** Shows how far away the structure is

**Command:** `/pm cavecompass [player]`

### 🔥 Smelter's Pickaxe

A special gold pickaxe that auto-smelts ores when mining!

- **Instant smelting:** Iron, Gold, and Copper ores drop ingots instead of raw ore
- **Fortune compatible:** Fortune enchantment multiplies smelted drops
- **Pre-enchanted:** Comes with Efficiency III and Unbreaking II
- **Additional smelts:** Also works on cobblestone, sand, clay, and more!

**Command:** `/pm smelterpick [player]`

### 👓 Miner's Goggles

A special leather helmet that reveals nearby ores with a glowing outline!

- **Ore vision:** Ores within range are highlighted with a glowing effect
- **Configurable radius:** Set reveal radius from 5-20 blocks
- **Filter support:** Create goggles that only reveal specific ore types
- **Real-time tracking:** Highlights update as you move through caves
- **No potion effects:** Works independently without affecting other gameplay

**Command:** `/pm goggles [player] [radius] [--filter ORE_TYPE]`

## Commands

| Command                                            | Aliases           | Description              |
| -------------------------------------------------- | ----------------- | ------------------------ |
| `/powermining magnethopper [player] [radius]`      | `/pm mh`          | Give a Magnet Hopper     |
| `/powermining orebell [player] [radius] [options]` | `/pm ob`          | Give an Ore Scanner Bell |
| `/powermining helmet [player]`                     | `/pm helmet`      | Give a Miner's Helmet    |
| `/powermining escaperope [player]`                 | `/pm rope, er`    | Give an Escape Rope      |
| `/powermining cavecompass [player]`                | `/pm compass, cc` | Give a Cave Compass      |
| `/powermining smelterpick [player]`                | `/pm smelter, sp` | Give a Smelter's Pickaxe |
| `/powermining goggles [player] [radius] [options]` | `/pm goggles, mg` | Give Miner's Goggles     |
| `/powermining drill`                               | `/pm drill`       | Show mounted mining help |
| `/powermining help`                                | `/pm help`        | Show all commands        |

### Ore Bell Options

- `--filter <ORE_TYPE>` - Only scan for specific ore type (e.g., `DIAMOND_ORE`)
- `--duration <TICKS>` - Highlight duration in ticks (20 ticks = 1 second)

### Miner's Goggles Options

- `--filter <ORE_TYPE>` - Only reveal specific ore type (e.g., `DIAMOND_ORE`)

## Permissions

| Permission                           | Description                   | Default |
| ------------------------------------ | ----------------------------- | ------- |
| `powermining.*`                      | Full access to all features   | op      |
| `powermining.use.*`                  | Use all Power Mining features | true    |
| `powermining.use.mountedmining`      | Use mounted mining            | true    |
| `powermining.use.magnethopper`       | Use magnet hoppers            | true    |
| `powermining.use.orescannerbell`     | Use ore scanner bells         | true    |
| `powermining.use.minershelmet`       | Use miner's helmets           | true    |
| `powermining.use.escaperope`         | Use escape ropes              | true    |
| `powermining.use.cavecompass`        | Use cave compasses            | true    |
| `powermining.use.autosmelterpickaxe` | Use auto-smelter pickaxes     | true    |
| `powermining.use.minersgoggles`      | Use miner's goggles           | true    |
| `powermining.give.*`                 | Give Power Mining items       | op      |
| `powermining.give.magnethopper`      | Give magnet hoppers           | op      |
| `powermining.give.orescannerbell`    | Give ore scanner bells        | op      |
| `powermining.give.minershelmet`      | Give miner's helmets          | op      |
| `powermining.give.escaperope`        | Give escape ropes             | op      |
| `powermining.give.cavecompass`       | Give cave compasses           | op      |
| `powermining.give.smelterpickaxe`    | Give smelter's pickaxes       | op      |
| `powermining.give.minersgoggles`     | Give miner's goggles          | op      |

## Configuration

```yaml
# Mounted Mining Settings
mounted-mining:
  enabled: true
  radius-x: 1 # Mining area width
  radius-y: 1 # Mining area height
  radius-z: 1 # Mining area depth
  mining-interval: 5 # Ticks between mining
  y-offset: 1 # Height offset from mount

# Magnet Hopper Settings
magnet-hopper:
  enabled: true
  default-radius: 8 # Default attraction radius
  max-radius: 32 # Maximum allowed radius
  attraction-interval: 5
  attraction-speed: 0.5
  particles:
    enabled: true
    type: SOUL_FIRE_FLAME
    count: 3
    interval: 10

# Ore Scanner Bell Settings
ore-scanner-bell:
  enabled: true
  default-radius: 16
  max-radius: 64
  default-duration: 60 # Ticks (60 = 3 seconds)
  cooldown: 30 # Seconds
  ore-types: # Customizable ore list
    - DIAMOND_ORE
    - IRON_ORE
    # ... and more

# Miner's Helmet Settings
miners-helmet:
  enabled: true

# Escape Rope Settings
escape-rope:
  enabled: true

# Cave Compass Settings
cave-compass:
  enabled: true
  search-radius: 5000 # Max distance to search structures

# Auto-Smelter Pickaxe Settings
auto-smelter-pickaxe:
  enabled: true

# Miner's Goggles Settings
miners-goggles:
  enabled: true
  default-radius: 10 # Default reveal radius
  min-radius: 5 # Minimum radius
  max-radius: 20 # Maximum radius
  highlight-interval: 20 # Ticks between updates
  highlight-color: GOLD # Glow color
  ore-types: # Detectable ore list
    - DIAMOND_ORE
    - IRON_ORE
    # ... and more
```

## Installation

1. Download the latest release from [Releases](releases/)
2. Place `power-mining-1.0.0.jar` in your server's `plugins` folder
3. Restart your server
4. (Optional) Configure `plugins/PowerMining/config.yml`

## Requirements

- **Minecraft:** 1.21.4+
- **Server:** Paper or forks (Purpur, etc.)
- **Java:** 21+

## Building from Source

```bash
git clone https://github.com/yourusername/power-mining.git
cd power-mining
mvn clean package
```

The compiled JAR will be in `target/power-mining-1.0.0.jar`

## Ore Color Guide

The ore scanner bell highlights different ores with distinct colors:

| Ore            | Glow Color   |
| -------------- | ------------ |
| Diamond        | Cyan         |
| Emerald        | Bright Green |
| Gold           | Gold         |
| Iron           | Tan          |
| Copper         | Orange-Brown |
| Redstone       | Red          |
| Lapis          | Blue         |
| Coal           | Dark Gray    |
| Quartz         | White        |
| Ancient Debris | Brown        |

## Support

If you encounter any issues or have suggestions, please [open an issue](issues/).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ for the Minecraft community
