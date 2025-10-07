package com.cascade.luckyblocks;

import com.cascade.luckyblocks.command.LuckyBlockCommand;
import com.cascade.luckyblocks.listener.BlockBreakListener;
import com.cascade.luckyblocks.listener.BlockPlaceListener;
import com.cascade.luckyblocks.listener.BlockDamageListener;
import com.cascade.luckyblocks.model.LootEntry;
import com.cascade.luckyblocks.model.Tier;
import com.cascade.luckyblocks.storage.PlacedStorage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class LuckyBlocksPlugin extends JavaPlugin {

    private static LuckyBlocksPlugin instance;

    public static LuckyBlocksPlugin getInstance() { return instance; }

    private NamespacedKey tierKey;
    private final Map<String, Tier> tiers = new LinkedHashMap<>();
    private PlacedStorage storage;

    @Override
    public void onEnable() {
        instance = this;
        this.tierKey = new NamespacedKey(this, "luckyblock-tier");

        saveDefaultConfig();
        reloadTiers();

        this.storage = new PlacedStorage(this);
        storage.load();

        // Command
        LuckyBlockCommand cmd = new LuckyBlockCommand(this);
        Objects.requireNonNull(getCommand("luckyblock")).setExecutor(cmd);
        Objects.requireNonNull(getCommand("luckyblock")).setTabCompleter(cmd);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockDamageListener(this), this);

        getLogger().info("Loaded " + tiers.size() + " tiers, and " + storage.size() + " placed lucky blocks.");
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.save();
    }

    public void reloadTiers() {
        tiers.clear();
        FileConfiguration cfg = getConfig();
        ConfigurationSection tiersSec = cfg.getConfigurationSection("tiers");
        if (tiersSec == null) return;
        for (String id : tiersSec.getKeys(false)) {
            try {
                Tier tier = Tier.fromConfig(id, tiersSec.getConfigurationSection(id));
                tiers.put(id.toLowerCase(Locale.ROOT), tier);
            } catch (Exception ex) {
                getLogger().warning("Failed to load tier '" + id + "': " + ex.getMessage());
            }
        }
    }

    public NamespacedKey getTierKey() {
        return tierKey;
    }

    public Map<String, Tier> getTiers() {
        return Collections.unmodifiableMap(tiers);
    }

    public Tier getTier(String id) {
        if (id == null) return null;
        return tiers.get(id.toLowerCase(Locale.ROOT));
    }

    public PlacedStorage getStorage() {
        return storage;
    }
}
