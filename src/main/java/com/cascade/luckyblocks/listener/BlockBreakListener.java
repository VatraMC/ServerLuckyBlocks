package com.cascade.luckyblocks.listener;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import com.cascade.luckyblocks.model.LootEntry;
import com.cascade.luckyblocks.model.Tier;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final LuckyBlocksPlugin plugin;
    public BlockBreakListener(LuckyBlocksPlugin plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        var loc = e.getBlock().getLocation();
        String tierId = plugin.getStorage().get(loc);
        if (tierId == null) return;

        // it's a lucky block
        e.setDropItems(false);
        e.setExpToDrop(0);

        // remove block and storage entry
        plugin.getStorage().remove(loc);
        e.getBlock().setType(Material.AIR);

        Tier tier = plugin.getTier(tierId);
        if (tier == null) return;
        LootEntry loot = tier.pickLoot();
        if (loot != null) loot.execute(e.getPlayer(), loc);
    }
}
