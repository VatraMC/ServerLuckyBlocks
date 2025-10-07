package com.cascade.luckyblocks.listener;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class BlockDamageListener implements Listener {
    private final LuckyBlocksPlugin plugin;
    public BlockDamageListener(LuckyBlocksPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDamage(BlockDamageEvent e) {
        if (plugin.getStorage().contains(e.getBlock().getLocation())) {
            // Allow breaking instantly, even by hand
            e.setInstaBreak(true);
        }
    }
}
