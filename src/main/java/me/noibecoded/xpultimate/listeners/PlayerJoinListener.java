package me.noibecoded.xpultimate.listeners;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.config.ConfigManager;
import me.noibecoded.xpultimate.data.DatabaseManager;
import me.noibecoded.xpultimate.data.XpDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final XpUltimate plugin;

    public PlayerJoinListener(XpUltimate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ConfigManager config = plugin.getConfigManager();

        if (config.isInterestEnabled()) {
            processInterest(player, uuid);
        }

        XpDataManager.recordLogin(player);

        int currentXp = XpDataManager.getTotalXp(player);
        if (currentXp > 0) {
            player.sendMessage("§aWelcome back! Your bank balance: §e" + currentXp + " XP");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        XpDataManager.recordLogout(player);
    }

    private void processInterest(Player player, UUID uuid) {
        ConfigManager config = plugin.getConfigManager();
        DatabaseManager dbManager = plugin.getDatabaseManager();

        int currentXp = XpDataManager.getTotalXp(player);
        if (currentXp <= 0) {
            return;
        }

        double onlineRate = config.getOnlineInterest();
        double offlineRate = config.getOfflineInterest();

        long lastLogin = System.currentTimeMillis();

        double interestAmount = (currentXp * offlineRate) / 100.0;

        dbManager.logInterest(uuid, interestAmount, offlineRate, false);

        if (interestAmount >= 1) {
            int intAmount = (int) Math.floor(interestAmount);
            XpDataManager.addXp(player, intAmount);
            player.sendMessage("§aInterest applied! §e+" + intAmount + " XP §7(offline rate: " + offlineRate + "%)");
        }
    }
}
