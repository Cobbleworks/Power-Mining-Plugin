# Changelog

All notable changes to Power Mining Tools will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- Added a persistent Dungeon Locator recovery compass for finding the nearest mob spawner in loaded terrain.
- Added `/pm dungeonlocator [player] [radius]` with `dungeon` and `dl` aliases.
- Added player-only glowing spawner markers and a retained compass target.
- Added configurable radius limits, cooldowns, marker duration, and dedicated use/give permissions.

### Performance

- Dungeon scans capture loaded chunk snapshots on the server thread and search the immutable data asynchronously.

### Security

- The locator never generates or force-loads unexplored chunks.
- Locator radius data is clamped to the configured maximum, and an unloaded result chunk is never synchronously reloaded just to create a marker.

## [1.0.0] - 2026-04-28

Power Mining Tools v1.0.0 is the initial release, delivering eight custom mining utility items covering mounted drilling, ore detection, item magnetism, auto-smelting, structure tracking, and night vision.

### Mining Tools

- **Mounted Mining Drill**: While riding a horse and holding a pickaxe, mines a configurable cuboid ahead of the mount at a configurable interval
- **Smelter's Pickaxe**: Replaces ore and block drops with smelted outputs and supports fortune-style bonus logic
- **Miner's Goggles**: Custom leather helmet that highlights nearby ores with optional filter and radius metadata
- **Miner's Helmet**: Custom golden helmet granting recurring night vision while worn

### Utility Tools

- **Magnet Hopper**: Custom hopper item that pulls nearby dropped items toward itself on a repeating task
- **Ore Scanner Bell**: Placeable custom bell that scans an area for ores and highlights found blocks for a configurable duration with configurable cooldown
- **Escape Rope**: Sets a return point on block click and teleports back on air right-click, consuming one rope per use
- **Cave Compass**: Cycles through structure targets and updates the lodestone to the located structure

### Persistence

- **Per-Item Metadata**: Radius, filter, duration, and mode stored per item using Bukkit's persistent data container

**Note:** If you encounter any bugs or issues, please don't hesitate to open an [issue](https://github.com/Cobbleworks/Power-Mining-Tools/issues). For any questions or to start a discussion, feel free to initiate a [discussion](https://github.com/Cobbleworks/Power-Mining-Tools/discussions) on the GitHub repository.
