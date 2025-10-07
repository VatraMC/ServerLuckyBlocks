package com.cascade.luckyblocks.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlacedStorage {
    private final Plugin plugin;
    private final File file;
    private final Map<String, String> byKey = new HashMap<>(); // key -> tierId

    public PlacedStorage(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "placed.yml");
    }

    public void load() {
        byKey.clear();
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getKeys(false)) {
            byKey.put(key, cfg.getString(key));
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, String> e : byKey.entrySet()) cfg.set(e.getKey(), e.getValue());
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save placed.yml: " + e.getMessage());
        }
    }

    public int size() { return byKey.size(); }

    public static String toKey(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    public void add(Location loc, String tierId) {
        byKey.put(toKey(loc), tierId);
    }

    public String remove(Location loc) {
        return byKey.remove(toKey(loc));
    }

    public String get(Location loc) {
        return byKey.get(toKey(loc));
    }

    public boolean contains(Location loc) { return byKey.containsKey(toKey(loc)); }
}
