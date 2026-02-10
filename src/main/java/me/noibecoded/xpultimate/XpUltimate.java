package me.noibecoded.xpultimate;

import me.noibecoded.xpultimate.api.MinecraftVersion;
import me.noibecoded.xpultimate.api.ServerType;
import me.noibecoded.xpultimate.annotations.SupportedVersions;
import me.noibecoded.xpultimate.commands.XpBottleCommand;
import me.noibecoded.xpultimate.commands.XpBankCommand;
import me.noibecoded.xpultimate.config.ConfigManager;
import me.noibecoded.xpultimate.data.DatabaseManager;
import me.noibecoded.xpultimate.data.XpDataManager;
import me.noibecoded.xpultimate.listeners.BottleUseListener;
import me.noibecoded.xpultimate.listeners.PlayerJoinListener;
import me.noibecoded.xpultimate.recipes.XpBottleCraftingListener;
import me.noibecoded.xpultimate.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SupportedVersions(versions = {
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
    "1.21", "1.21.1", "1.21.2", "1.21.3"
}, serverType = "BOTH")
public final class XpUltimate extends JavaPlugin {

    private static XpUltimate instance;
    private MinecraftVersion currentVersion;
    private ServerType serverType;
    private boolean isSupported = false;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private XpDataManager xpDataManager;

    @Override
    public void onLoad() {
        instance = this;
        detectServer();
        loadConfig();
        initDatabase();
    }

    @Override
    public void onEnable() {
        if (!isSupported) {
            getLogger().warning("This server version is not fully supported.");
        }

        xpDataManager.init(this);
        registerCommands();
        registerListeners();
        registerRecipes();

        getLogger().info("XpUltimate v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (xpDataManager != null) {
            xpDataManager.saveData();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("XpUltimate disabled!");
    }

    private void detectServer() {
        serverType = ServerType.detect();
        getLogger().info("Detected server type: " + serverType.getName());
    }

    private void loadConfig() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        getLogger().info("Configuration loaded. Database type: " + configManager.getDatabaseType());
        getLogger().info("Interest rates - Online: " + configManager.getOnlineInterest() + "%, Offline: " + configManager.getOfflineInterest() + "%");
    }

    private void initDatabase() {
        databaseManager = new DatabaseManager(this);
        databaseManager.init();
    }

    private void registerCommands() {
        getCommand("xpbottles").setExecutor(new XpBottleCommand());
        getCommand("xpbank").setExecutor(new XpBankCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BottleUseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new XpBottleCraftingListener(this), this);
    }

    private void registerRecipes() {
        XpBottleCraftingListener.registerRecipes(this);
    }

    public static XpUltimate getInstance() {
        return instance;
    }

    public MinecraftVersion getCurrentVersion() {
        return currentVersion;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public boolean isPaper() {
        return serverType == ServerType.PAPER;
    }

    public boolean isSpigot() {
        return serverType == ServerType.SPIGOT;
    }

    public boolean isFork() {
        return serverType == ServerType.FORK;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public XpDataManager getXpDataManager() {
        return xpDataManager;
    }
}
