# LuckyBlocks (Paper plugin)

Tiered Lucky Blocks for Paper servers. Players can receive Lucky Blocks by command and, when placed and broken, a random reward is chosen based on the tier's weighted loot table.

## Build

- Requires Java 17+
- Build with Maven:

```
mvn -q -DskipTests package
```

The built jar will be in `target/luckyblocks-1.0.0.jar`.

## Install

1. Copy the jar to your server's `plugins/` directory.
2. Start the server to generate the default `config.yml` and `placed.yml`.
3. Edit `plugins/LuckyBlocks/config.yml` to customize tiers and loot.

## Command

- `/luckyblock give <player> <tier> [amount]`
  - Permission: `luckyblocks.give` (default: OP)

Tab completion is included for player names, tier IDs, and common stack sizes.

## How it works

- The give command creates an item with a hidden tag marking it as a Lucky Block tier.
- When a player places the block, its location is recorded.
- When broken, normal drops are suppressed and a random loot entry is executed:
  - item: drops items at the block location
  - command: runs a console command (supports `{player}` placeholder)
  - lightning: strikes lightning effect at player or location
  - explosion: creates a configurable explosion

## Configuration

Edit `plugins/LuckyBlocks/config.yml` to add or modify tiers. Each tier has:

- `display`: Name shown on the item.
- `itemMaterial`: The material of the Lucky Block item.
- `glow`: Whether the item should have a glowing enchantment.
- `loot`: A list of loot entries with `type` and `weight`.

Supported loot types: `item`, `command`, `lightning`, `explosion`.

## Notes

- Placed Lucky Blocks are persisted in `plugins/LuckyBlocks/placed.yml` by location.
- Breaking a Lucky Block in Creative mode still triggers the loot (you can adjust behavior in `BlockBreakListener`).
