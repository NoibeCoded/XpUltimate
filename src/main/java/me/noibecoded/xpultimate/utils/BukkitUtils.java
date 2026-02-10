package me.noibecoded.xpultimate.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.List;

public class BukkitUtils {

    public static void sendMessage(Object player, String message) {
        try {
            Method sendMessageMethod = player.getClass().getMethod("sendMessage", String.class);
            sendMessageMethod.invoke(player, message);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to send message: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getLore(ItemStack item) {
        if (item.hasItemMeta()) {
            return item.getItemMeta().getLore();
        }
        return null;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
}
