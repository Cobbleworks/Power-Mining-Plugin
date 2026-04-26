<p align="center">
  <img src="images/plugin-logo.png" alt="Power Mining Tools" width="128" />
</p>
<h1 align="center">Power Mining Tools</h1>
<p align="center">
  <b>Mining enhancements for Minecraft servers.</b><br>
  <b>Mounted mining, magnet hoppers, ore scanner, escape ropes, and auto-smelting.</b>
</p>
<p align="center">
  <a href="https://github.com/Cobbleworks/Power-Mining-Tools/releases"><img src="https://img.shields.io/github/v/release/Cobbleworks/Power-Mining-Tools?include_prereleases&style=flat-square&color=4CAF50" alt="Latest Release"></a>&nbsp;&nbsp;<a href="https://github.com/Cobbleworks/Power-Mining-Tools/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License"></a>&nbsp;&nbsp;<img src="https://img.shields.io/badge/Java-17+-orange?style=flat-square" alt="Java Version">&nbsp;&nbsp;<img src="https://img.shields.io/badge/Minecraft-1.21+-green?style=flat-square" alt="Minecraft Version">&nbsp;&nbsp;<img src="https://img.shields.io/badge/Platform-Spigot%2FPaper-yellow?style=flat-square" alt="Platform">&nbsp;&nbsp;<img src="https://img.shields.io/badge/Status-Active-brightgreen?style=flat-square" alt="Status">
</p>

Power Mining Tools is an open-source Minecraft plugin that provides a comprehensive set of mining enhancements for Spigot and Paper servers. From riding a horse while automatically excavating a 3×3×3 tunnel, to magnet hoppers that pull in nearby drops, to leather goggles that highlight ores through walls — the plugin transforms every aspect of cave exploration and resource gathering. All tools are given via commands and are fully configurable per-item at runtime.

### **Core Features**

- **Mounted Mining (Drill):** Automatically mine a 3×3×3 area in front of your mount while riding horses, donkeys, or mules — respects enchantments and durability
- **Magnet Hopper:** Special hoppers that attract nearby dropped items with configurable attraction radius and smooth pull animation using soul fire flame particles
- **Ore Scanner Bell:** Placeable bells that reveal all ores within a configurable radius using color-coded glowing outlines when activated by right-click
- **Miner's Helmet:** Gold helmet that permanently grants night vision as long as it is worn — no potions required
- **Escape Rope:** Magical lead item that remembers a set location and teleports the player back on use — consumed on teleport
- **Cave Compass:** Mystical compass that points toward the nearest selectable structure (mineshafts, strongholds, nether fortresses, ancient cities, and more)
- **Smelter's Pickaxe:** Special gold pickaxe that auto-smelts ores on break — Fortune compatible, also works on cobblestone, sand, clay, and more
- **Miner's Goggles:** Leather helmet that reveals nearby ores in real time with a configurable glowing radius and ore filter support

### **Supported Platforms**

- **Server Software:** `Spigot`, `Paper`, `Purpur`, `CraftBukkit`
- **Minecraft Versions:** `1.21.5`, `1.21.6`, `1.21.7`, `1.21.8`, `1.21.9`, `1.21.10` and higher
- **Java Requirements:** `Java 17+`

### **Installation**

1. Download the latest `.jar` from the [Releases](https://github.com/Cobbleworks/Power-Mining-Tools/releases) page
2. Stop your Minecraft server
3. Copy the `.jar` into your server's `plugins` folder
4. Start your server — a default configuration folder is generated at `plugins/PowerMining/`

### **Player Commands**

| Command | Description |
|---------|-------------|
| `/pm magnethopper [player] [radius]` | Give a Magnet Hopper |
| `/pm orebell [player] [radius] [--filter ORE_TYPE] [--duration TICKS]` | Give an Ore Scanner Bell |
| `/pm helmet [player]` | Give a Miner's Helmet |
| `/pm escaperope [player]` | Give an Escape Rope |
| `/pm cavecompass [player]` | Give a Cave Compass |
| `/pm smelterpick [player]` | Give a Smelter's Pickaxe |
| `/pm goggles [player] [radius] [--filter ORE_TYPE]` | Give Miner's Goggles |
| `/pm drill` | Show mounted mining help and usage |
| `/pm help` | Show all available commands |

**Aliases:** `/powermining`, `/pm`

### **License**

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

## **Screenshots**

The screenshots below demonstrate the core features of the Power Mining Tools plugin, including mounted mining, magnet hoppers, ore scanner bells, and miner's goggles.

<table>
  <tr>
    <th>Power Mining - Mounted Mining</th>
    <th>Power Mining - Magnet Hopper</th>
  </tr>
  <tr>
    <td><a href="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-drill.png" target="_blank" rel="noopener noreferrer"><img src="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-drill.png" alt="Mounted Mining" width="450"></a></td>
    <td><a href="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-hopper.png" target="_blank" rel="noopener noreferrer"><img src="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-hopper.png" alt="Magnet Hopper" width="450"></a></td>
  </tr>
  <tr>
    <th>Power Mining - Ore Scanner Bell</th>
    <th>Power Mining - Miner's Goggles</th>
  </tr>
  <tr>
    <td><a href="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-scanner.png" target="_blank" rel="noopener noreferrer"><img src="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-scanner.png" alt="Ore Scanner Bell" width="450"></a></td>
    <td><a href="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-goggles.png" target="_blank" rel="noopener noreferrer"><img src="https://github.com/Cobbleworks/Power-Mining-Tools/raw/main/images/screenshot-goggles.png" alt="Miner's Goggles" width="450"></a></td>
  </tr>
</table>
