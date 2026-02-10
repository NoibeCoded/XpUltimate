package me.noibecoded.xpultimate.data;

import me.noibecoded.xpultimate.XpUltimate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XpDataManager {

    private static final Map<UUID, Integer> playerXp = new HashMap<>();
    private static final Map<UUID, Long> lastLoginTime = new HashMap<>();
    private static File dataFile;
    private static File permissionsFile;
    private static XpUltimate plugin;
    private static DatabaseManager databaseManager;

    private static final Pattern XP_PATTERN = Pattern.compile("Contains: §e(\\d+) XP");

    public static void init(XpUltimate instance) {
        plugin = instance;
        databaseManager = plugin.getDatabaseManager();
        loadData();
        loadDefaultPermissions();
    }

    private static void loadDefaultPermissions() {
        permissionsFile = new File(plugin.getDataFolder(), "permissions.yml");
        if (!permissionsFile.exists()) {
            try (InputStream is = plugin.getClass().getResourceAsStream("/permissions.yml")) {
                if (is != null) {
                    Files.copy(is, permissionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("Created default permissions.yml");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create permissions.yml: " + e.getMessage());
            }
        }
    }

    public static void loadData() {
        playerXp.clear();
        playerXp.putAll(databaseManager.getAllXpData());
    }

    public static void saveData() {
        for (Map.Entry<UUID, Integer> entry : playerXp.entrySet()) {
            databaseManager.setXp(entry.getKey(), entry.getValue());
        }
    }

    public static int getTotalXp(Player player) {
        return playerXp.getOrDefault(player.getUniqueId(), 0);
    }

    public static void addXp(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = playerXp.getOrDefault(uuid, 0);
        int newXp = current + amount;
        playerXp.put(uuid, newXp);
        databaseManager.addXp(uuid, amount);
    }

    public static void removeXp(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = playerXp.getOrDefault(uuid, 0);
        int newXp = Math.max(0, current - amount);
        playerXp.put(uuid, newXp);
        databaseManager.removeXp(uuid, amount);
    }

    public static void setXp(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        playerXp.put(uuid, Math.max(0, amount));
        databaseManager.setXp(uuid, amount);
    }

    public static void recordLogin(Player player) {
        UUID uuid = player.getUniqueId();
        lastLoginTime.put(uuid, System.currentTimeMillis());
        databaseManager.updateLoginTime(uuid);
    }

    public static void recordLogout(Player player) {
        UUID uuid = player.getUniqueId();
        Long loginTime = lastLoginTime.remove(uuid);
        if (loginTime != null) {
            databaseManager.updateLogoutTime(uuid);
        }
    }

    public static void giveXpBottle(Player player, int amount) {
        ItemStack bottle = createXpBottle(amount);
        player.getInventory().addItem(bottle);
    }

    public static ItemStack createXpBottle(int amount) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);

        ItemMeta meta = bottle.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6XP Bottle");
            List<String> lore = new ArrayList<>();
            lore.add("§7Contains: §e" + amount + " XP");
            meta.setLore(lore);
            bottle.setItemMeta(meta);
        }

        return bottle;
    }

    public static int extractXpFromBottle(ItemStack item) {
        if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().equals("§6XP Bottle")) {
            return 0;
        }

        if (!meta.hasLore() || meta.getLore().isEmpty()) {
            return 0;
        }

        String lore = meta.getLore().get(0);
        Matcher matcher = XP_PATTERN.matcher(lore);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }

    public static void giveFilledBottle(Player player, int amount) {
        ItemStack bottle = createFilledXpBottle(amount);
        player.getInventory().addItem(bottle);
    }

    public static ItemStack createFilledXpBottle(int amount) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);

        ItemMeta meta = bottle.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6XP Bottle (Filled)");
            List<String> lore = new ArrayList<>();
            lore.add("§7Contains: §e" + amount + " XP");
            lore.add("§8Right-click to open crafting interface");
            meta.setLore(lore);
            bottle.setItemMeta(meta);
        }

        return bottle;
    }

    public static ItemStack createEmptyBottle() {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);

        ItemMeta meta = bottle.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§7Empty XP Bottle");
            List<String> lore = new ArrayList<>();
            lore.add("§8Place in crafting interface to fill");
            meta.setLore(lore);
            bottle.setItemMeta(meta);
        }

        return bottle;
    }
}
