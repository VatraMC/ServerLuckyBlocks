package com.cascade.luckyblocks.util;

import com.cascade.luckyblocks.model.LootEntry;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedPicker {
    public static <T extends LootEntry> T pick(List<T> entries) {
        if (entries == null || entries.isEmpty()) return null;
        int total = 0;
        for (T e : entries) total += Math.max(0, e.weight());
        if (total <= 0) return entries.get(ThreadLocalRandom.current().nextInt(entries.size()));
        int r = ThreadLocalRandom.current().nextInt(total);
        int sum = 0;
        for (T e : entries) {
            sum += Math.max(0, e.weight());
            if (r < sum) return e;
        }
        return entries.get(entries.size() - 1);
    }
}
