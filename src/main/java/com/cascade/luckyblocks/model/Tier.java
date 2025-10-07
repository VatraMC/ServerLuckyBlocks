package com.cascade.luckyblocks.model;

import com.cascade.luckyblocks.util.WeightedPicker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Tier {
    private final String id;
    private final String displayName;
    private final Material itemMaterial;
    private final boolean glow;
    private final List<LootEntry> lootEntries;
    private final String headTexture; // Base64 texture for custom player head, optional

    public Tier(String id, String displayName, Material itemMaterial, boolean glow, List<LootEntry> lootEntries, String headTexture) {
        this.id = id;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.itemMaterial = itemMaterial;
        this.glow = glow;
        this.lootEntries = lootEntries;
        this.headTexture = headTexture;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getItemMaterial() { return itemMaterial; }
    public boolean isGlow() { return glow; }
    public List<LootEntry> getLootEntries() { return lootEntries; }
    public String getHeadTexture() { return headTexture; }

    public LootEntry pickLoot() {
        return WeightedPicker.pick(lootEntries);
    }

    public static Tier fromConfig(String id, ConfigurationSection sec) {
        if (sec == null) throw new IllegalArgumentException("Missing config for tier: " + id);
        String display = sec.getString("display", id);
        String matStr = sec.getString("itemMaterial", "SPONGE");
        boolean glow = sec.getBoolean("glow", false);
        Material mat = Material.matchMaterial(matStr);
        if (mat == null) throw new IllegalArgumentException("Invalid material: " + matStr);
        String headTexture = sec.getString("headTexture", null);
        List<LootEntry> loot = new ArrayList<>();
        for (Object obj : sec.getList("loot", new ArrayList<>())) {
            if (!(obj instanceof java.util.Map)) continue;
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
            LootEntry entry = LootEntry.fromMap(map);
            if (entry != null) loot.add(entry);
        }
        if (loot.isEmpty()) throw new IllegalArgumentException("Tier '" + id + "' has no loot entries");
        return new Tier(id, display, mat, glow, loot, headTexture);
    }
}
