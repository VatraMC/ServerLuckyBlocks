package com.cascade.luckyblocks.model;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public interface LootEntry {
    int weight();
    void execute(Player player, Location location);

    static LootEntry fromMap(Map<String, Object> map) {
        String type = String.valueOf(map.getOrDefault("type", "item")).toLowerCase(Locale.ROOT);
        int weight = (int) (map.getOrDefault("weight", 1) instanceof Number ? ((Number) map.get("weight")).intValue() : 1);
        switch (type) {
            case "item": {
                String material = String.valueOf(map.getOrDefault("material", "STONE"));
                String amountStr = String.valueOf(map.getOrDefault("amount", "1"));
                int min, max;
                if (amountStr.contains("-")) {
                    String[] parts = amountStr.split("-");
                    min = Integer.parseInt(parts[0]);
                    max = Integer.parseInt(parts[1]);
                } else {
                    min = max = Integer.parseInt(amountStr);
                }
                return new LootItem(weight, material, min, max);
            }
            case "command": {
                String command = String.valueOf(map.getOrDefault("command", "say {player} got loot"));
                return new LootCommand(weight, command);
            }
            case "lightning": {
                String target = String.valueOf(map.getOrDefault("target", "location"));
                return new LootLightning(weight, target);
            }
            case "explosion": {
                double power = map.get("power") instanceof Number ? ((Number) map.get("power")).doubleValue() : 2.0;
                boolean fire = Boolean.parseBoolean(String.valueOf(map.getOrDefault("fire", false)));
                boolean breakBlocks = Boolean.parseBoolean(String.valueOf(map.getOrDefault("breakBlocks", true)));
                return new LootExplosion(weight, power, fire, breakBlocks);
            }
            default:
                LuckyBlocksPlugin.getInstance().getLogger().warning("Unknown loot type: " + type);
                return null;
        }
    }

    class LootItem implements LootEntry {
        private final int weight;
        private final Material material;
        private final int min;
        private final int max;
        public LootItem(int weight, String material, int min, int max) {
            this.weight = weight;
            this.material = Material.matchMaterial(material);
            if (this.material == null) throw new IllegalArgumentException("Invalid material: " + material);
            this.min = min;
            this.max = max;
        }
        @Override public int weight() { return weight; }
        @Override public void execute(Player player, Location location) {
            int amount = (min == max) ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
            World world = location.getWorld();
            if (world != null) world.dropItemNaturally(location, new org.bukkit.inventory.ItemStack(material, Math.max(1, amount)));
        }
    }

    class LootCommand implements LootEntry {
        private final int weight;
        private final String command;
        public LootCommand(int weight, String command) {
            this.weight = weight;
            this.command = command;
        }
        @Override public int weight() { return weight; }
        @Override public void execute(Player player, Location location) {
            String cmd = command.replace("{player}", player.getName());
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), cmd);
        }
    }

    class LootLightning implements LootEntry {
        private final int weight;
        private final String target; // player or location
        public LootLightning(int weight, String target) { this.weight = weight; this.target = target; }
        @Override public int weight() { return weight; }
        @Override public void execute(Player player, Location location) {
            Location strikeAt = ("player".equalsIgnoreCase(target)) ? player.getLocation() : location;
            if (strikeAt.getWorld() != null) strikeAt.getWorld().strikeLightningEffect(strikeAt);
        }
    }

    class LootExplosion implements LootEntry {
        private final int weight;
        private final double power;
        private final boolean fire;
        private final boolean breakBlocks;
        public LootExplosion(int weight, double power, boolean fire, boolean breakBlocks) {
            this.weight = weight; this.power = power; this.fire = fire; this.breakBlocks = breakBlocks;
        }
        @Override public int weight() { return weight; }
        @Override public void execute(Player player, Location location) {
            if (location.getWorld() != null) location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float) power, fire, breakBlocks);
        }
    }
}
