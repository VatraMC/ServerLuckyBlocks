package com.cascade.luckyblocks.listener;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener implements Listener {
    private final LuckyBlocksPlugin plugin;
    public BlockPlaceListener(LuckyBlocksPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack hand = e.getItemInHand();
        if (hand == null) return;
        ItemMeta meta = hand.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String tierId = pdc.get(plugin.getTierKey(), PersistentDataType.STRING);
        if (tierId == null) return;

        // Register placement
        plugin.getStorage().add(e.getBlockPlaced().getLocation(), tierId);
    }
}
