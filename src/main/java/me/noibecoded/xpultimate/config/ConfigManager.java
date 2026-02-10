package me.noibecoded.xpultimate.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    private final JavaPlugin plugin;
    private File configFile;

    private String databaseType;
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;

    private double onlineInterest;
    private double offlineInterest;

    private boolean interestEnabled;
    private long interestInterval;

    private int maxXpPerBottle;
    private boolean enableCraftingTable;
    private String craftingTableName;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            createDefaultConfig();
        }

        reloadConfig();
    }

    public void reloadConfig() {
        org.bukkit.configuration.file.YamlConfiguration config =
            org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);

        databaseType = config.getString("database.type", "sqlite").toLowerCase();
        dbHost = config.getString("database.host", "localhost");
        dbPort = config.getInt("database.port", 3306);
        dbName = config.getString("database.name", "xpultimate");
        dbUser = config.getString("database.user", "root");
        dbPassword = config.getString("database.password", "");

        onlineInterest = config.getDouble("bank.interest.online", 0.2);
        offlineInterest = config.getDouble("bank.interest.offline", 0.05);
        interestEnabled = config.getBoolean("bank.interest.enabled", true);
        interestInterval = config.getLong("bank.interest.interval", 3600);

        maxXpPerBottle = config.getInt("bottles.max-xp-per-bottle", 10000);
        enableCraftingTable = config.getBoolean("crafting.enabled", true);
        craftingTableName = config.getString("crafting.table-name", "&6XP Bottle Crafting Table");
    }

    private void createDefaultConfig() {
        try (InputStream is = plugin.getClass().getResourceAsStream("/config.yml")) {
            if (is != null) {
                Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("Created default config.yml");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create config.yml: " + e.getMessage());
        }
    }

    public void saveConfig() {
        org.bukkit.configuration.file.YamlConfiguration config =
            new org.bukkit.configuration.file.YamlConfiguration();

        config.set("database.type", databaseType);
        config.set("database.host", dbHost);
        config.set("database.port", dbPort);
        config.set("database.name", dbName);
        config.set("database.user", dbUser);
        config.set("database.password", dbPassword);

        config.set("bank.interest.online", onlineInterest);
        config.set("bank.interest.offline", offlineInterest);
        config.set("bank.interest.enabled", interestEnabled);
        config.set("bank.interest.interval", interestInterval);

        config.set("bottles.max-xp-per-bottle", maxXpPerBottle);
        config.set("crafting.enabled", enableCraftingTable);
        config.set("crafting.table-name", craftingTableName);

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config: " + e.getMessage());
        }
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
        saveConfig();
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
        saveConfig();
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
        saveConfig();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        saveConfig();
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
        saveConfig();
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        saveConfig();
    }

    public double getOnlineInterest() {
        return onlineInterest;
    }

    public void setOnlineInterest(double onlineInterest) {
        this.onlineInterest = Math.max(0, Math.min(100, onlineInterest));
        saveConfig();
    }

    public double getOfflineInterest() {
        return offlineInterest;
    }

    public void setOfflineInterest(double offlineInterest) {
        this.offlineInterest = Math.max(0, Math.min(100, offlineInterest));
        saveConfig();
    }

    public boolean isInterestEnabled() {
        return interestEnabled;
    }

    public void setInterestEnabled(boolean interestEnabled) {
        this.interestEnabled = interestEnabled;
        saveConfig();
    }

    public long getInterestInterval() {
        return interestInterval;
    }

    public void setInterestInterval(long interestInterval) {
        this.interestInterval = interestInterval;
        saveConfig();
    }

    public int getMaxXpPerBottle() {
        return maxXpPerBottle;
    }

    public void setMaxXpPerBottle(int maxXpPerBottle) {
        this.maxXpPerBottle = maxXpPerBottle;
        saveConfig();
    }

    public boolean isCraftingEnabled() {
        return enableCraftingTable;
    }

    public void setCraftingEnabled(boolean enableCraftingTable) {
        this.enableCraftingTable = enableCraftingTable;
        saveConfig();
    }

    public String getCraftingTableName() {
        return craftingTableName;
    }

    public void setCraftingTableName(String craftingTableName) {
        this.craftingTableName = craftingTableName;
        saveConfig();
    }
}
