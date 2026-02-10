package me.noibecoded.xpultimate.data;

import me.noibecoded.xpultimate.XpUltimate;
import me.noibecoded.xpultimate.config.ConfigManager;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private final XpUltimate plugin;
    private Connection connection;
    private boolean useMySQL;

    public DatabaseManager(XpUltimate plugin) {
        this.plugin = plugin;
    }

    public void init() {
        ConfigManager config = plugin.getConfigManager();
        useMySQL = "mysql".equalsIgnoreCase(config.getDatabaseType());

        if (useMySQL) {
            initMySQL();
        } else {
            initSQLite();
        }
    }

    private void initSQLite() {
        File dbFile = new File(plugin.getDataFolder(), "xpultimate.db");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTables();
            plugin.getLogger().info("SQLite database initialized");
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to initialize SQLite: " + e.getMessage());
            useMySQL = false;
        }
    }

    private void initMySQL() {
        ConfigManager config = plugin.getConfigManager();
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true",
            config.getDbHost(), config.getDbPort(), config.getDbName());

        try {
            connection = DriverManager.getConnection(url, config.getDbUser(), config.getDbPassword());
            createTables();
            plugin.getLogger().info("MySQL database initialized");
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to initialize MySQL, falling back to SQLite: " + e.getMessage());
            useMySQL = false;
            initSQLite();
        }
    }

    private void createTables() {
        String createXpTable = """
            CREATE TABLE IF NOT EXISTS xp_data (
                uuid VARCHAR(36) PRIMARY KEY,
                xp_amount INTEGER DEFAULT 0,
                last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_logout TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createInterestHistory = """
            CREATE TABLE IF NOT EXISTS interest_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid VARCHAR(36),
                amount REAL,
                interest_rate REAL,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                was_online BOOLEAN DEFAULT 0
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createXpTable);
            stmt.execute(createInterestHistory);
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to create tables: " + e.getMessage());
        }
    }

    public int getXp(UUID uuid) {
        String query = "SELECT xp_amount FROM xp_data WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("xp_amount");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get XP: " + e.getMessage());
        }

        return 0;
    }

    public void setXp(UUID uuid, int amount) {
        String upsert = """
            INSERT INTO xp_data (uuid, xp_amount)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE xp_amount = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(upsert)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, amount);
            stmt.setInt(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to set XP: " + e.getMessage());
        }
    }

    public void addXp(UUID uuid, int amount) {
        String update = "UPDATE xp_data SET xp_amount = xp_amount + ? WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, amount);
            stmt.setString(2, uuid.toString());
            int updated = stmt.executeUpdate();

            if (updated == 0) {
                setXp(uuid, amount);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add XP: " + e.getMessage());
        }
    }

    public void removeXp(UUID uuid, int amount) {
        String update = "UPDATE xp_data SET xp_amount = GREATEST(0, xp_amount - ?) WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, amount);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to remove XP: " + e.getMessage());
        }
    }

    public void updateLoginTime(UUID uuid) {
        String update = "UPDATE xp_data SET last_login = CURRENT_TIMESTAMP WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, uuid.toString());
            int updated = stmt.executeUpdate();

            if (updated == 0) {
                setXp(uuid, 0);
                updateLoginTime(uuid);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to update login time: " + e.getMessage());
        }
    }

    public void updateLogoutTime(UUID uuid) {
        String update = "UPDATE xp_data SET last_logout = CURRENT_TIMESTAMP WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to update logout time: " + e.getMessage());
        }
    }

    public void logInterest(UUID uuid, double amount, double rate, boolean wasOnline) {
        String insert = "INSERT INTO interest_history (uuid, amount, interest_rate, was_online) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, uuid.toString());
            stmt.setDouble(2, amount);
            stmt.setDouble(3, rate);
            stmt.setBoolean(4, wasOnline);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to log interest: " + e.getMessage());
        }
    }

    public Map<UUID, Integer> getAllXpData() {
        Map<UUID, Integer> result = new HashMap<>();
        String query = "SELECT uuid, xp_amount FROM xp_data";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    int xp = rs.getInt("xp_amount");
                    result.put(uuid, xp);
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get all XP data: " + e.getMessage());
        }

        return result;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to close database connection: ");
            }
        }
    }
}
