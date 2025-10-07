package com.cascade.luckyblocks.command;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import com.cascade.luckyblocks.model.Tier;
import com.cascade.luckyblocks.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class LuckyBlockCommand implements CommandExecutor, TabCompleter {
    private final LuckyBlocksPlugin plugin;
    public LuckyBlockCommand(LuckyBlocksPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("luckyblocks.give")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        if (args.length < 1 || !args[0].equalsIgnoreCase("give") || args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " give <player> <tier> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return true;
        }
        String tierId = args[2].toLowerCase(Locale.ROOT);
        Tier tier = plugin.getTier(tierId);
        if (tier == null) {
            sender.sendMessage(ChatColor.RED + "Unknown tier: " + tierId);
            sender.sendMessage(ChatColor.GRAY + "Available: " + String.join(", ", plugin.getTiers().keySet()));
            return true;
        }
        int amount = 1;
        if (args.length >= 4) {
            try { amount = Math.max(1, Integer.parseInt(args[3])); } catch (Exception ignored) {}
        }

        var item = ItemBuilder.luckyBlockItem(plugin, tier, amount);
        var inv = target.getInventory();
        var leftover = inv.addItem(item);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(i -> target.getWorld().dropItemNaturally(target.getLocation(), i));
        }
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " x " + ChatColor.RESET + tier.getDisplayName() + ChatColor.GREEN + " to " + target.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            if ("give".startsWith(args[0].toLowerCase(Locale.ROOT))) out.add("give");
        } else if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) if (p.getName().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) out.add(p.getName());
        } else if (args.length == 3) {
            for (String id : plugin.getTiers().keySet()) if (id.startsWith(args[2].toLowerCase(Locale.ROOT))) out.add(id);
        } else if (args.length == 4) {
            for (String n : new String[]{"1","2","4","8","16","32","64"}) if (n.startsWith(args[3])) out.add(n);
        }
        return out;
    }
}
