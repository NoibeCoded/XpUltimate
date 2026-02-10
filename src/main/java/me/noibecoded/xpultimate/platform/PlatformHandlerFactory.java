package me.noibecoded.xpultimate.platform;

import me.noibecoded.xpultimate.XpUltimate;
import org.bukkit.Bukkit;

public class PlatformHandlerFactory {

    private static IPlatformHandler instance;

    public static IPlatformHandler getHandler(XpUltimate plugin) {
        if (instance != null) {
            return instance;
        }

        String serverName = Bukkit.getName();

        if (serverName.equalsIgnoreCase("Paper")) {
            instance = new PaperPlatformHandler();
        } else if (serverName.equalsIgnoreCase("Spigot")) {
            instance = new SpigotPlatformHandler();
        } else if (serverName.contains("Paper") || serverName.contains("Spigot")) {
            instance = new ForkPlatformHandler();
        } else {
            instance = new GenericPlatformHandler();
        }

        return instance;
    }

    private static class PaperPlatformHandler implements IPlatformHandler {
        @Override
        public String getPlatformName() {
            return "Paper";
        }

        @Override
        public boolean supportsAdventure() {
            return true;
        }

        @Override
        public boolean supportsPaper() {
            return true;
        }
    }

    private static class SpigotPlatformHandler implements IPlatformHandler {
        @Override
        public String getPlatformName() {
            return "Spigot";
        }

        @Override
        public boolean supportsAdventure() {
            return false;
        }

        @Override
        public boolean supportsPaper() {
            return false;
        }
    }

    private static class ForkPlatformHandler implements IPlatformHandler {
        @Override
        public String getPlatformName() {
            return "Fork";
        }

        @Override
        public boolean supportsAdventure() {
            try {
                Class.forName("net.kyori.adventure.text.Component");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        @Override
        public boolean supportsPaper() {
            try {
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}
