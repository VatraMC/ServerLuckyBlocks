package com.cascade.luckyblocks.util;

import com.cascade.luckyblocks.LuckyBlocksPlugin;
import com.cascade.luckyblocks.model.Tier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemBuilder {
    private static final Pattern TEXTURE_URL_PATTERN = Pattern.compile("https?://textures\\.minecraft\\.net/texture/[-a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);

    public static ItemStack luckyBlockItem(LuckyBlocksPlugin plugin, Tier tier, int amount) {
        String headTexture = tier.getHeadTexture();
        ItemStack item = (headTexture != null && !headTexture.isEmpty())
                ? new ItemStack(Material.PLAYER_HEAD)
                : new ItemStack(tier.getItemMaterial());

        item.setAmount(Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(tier.getDisplayName());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Place this block and break it to get",
                    ChatColor.GRAY + "a random reward based on its tier."
            ));
            if (tier.isGlow()) {
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.getPersistentDataContainer().set(plugin.getTierKey(), PersistentDataType.STRING, tier.getId());

            // Apply skull texture if provided
            if (meta instanceof SkullMeta && headTexture != null && !headTexture.isEmpty()) {
                applyHeadTexture((SkullMeta) meta, headTexture);
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Applies a custom texture (base64 payload from minecraft-heads) to a SkullMeta using Paper's PlayerProfile API.
     */
    private static void applyHeadTexture(SkullMeta skullMeta, String base64Texture) {
        try {
            String decoded = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
            Matcher m = TEXTURE_URL_PATTERN.matcher(decoded);
            if (!m.find()) {
                return; // couldn't find a URL, skip
            }
            URL url = new URL(m.group());
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(url);
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
        } catch (IllegalArgumentException | MalformedURLException ignored) {
            // Invalid base64 or URL; silently ignore and leave default appearance
        }
    }
}
