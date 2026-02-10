package me.noibecoded.xpultimate.api;

import org.bukkit.Bukkit;

public enum ServerType {
    SPIGOT("Spigot"),
    PAPER("Paper"),
    FORK("Fork"),
    UNKNOWN("Unknown");

    private final String name;

    ServerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ServerType detect() {
        String serverName = Bukkit.getName();

        if (serverName.equalsIgnoreCase("Paper")) {
            return PAPER;
        } else if (serverName.equalsIgnoreCase("Spigot")) {
            return SPIGOT;
        } else if (serverName.contains("Paper") || serverName.contains("Spigot")) {
            return FORK;
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return PAPER;
        } catch (ClassNotFoundException e) {
        }

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return SPIGOT;
        } catch (ClassNotFoundException e) {
        }

        return UNKNOWN;
    }
}
