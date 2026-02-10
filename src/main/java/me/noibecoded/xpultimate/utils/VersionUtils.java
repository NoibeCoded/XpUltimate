package me.noibecoded.xpultimate.utils;

import me.noibecoded.xpultimate.api.MinecraftVersion;
import org.bukkit.Bukkit;

public class VersionUtils {

    public static MinecraftVersion getCurrentVersion() {
        String version = Bukkit.getVersion();

        if (version.contains("1.21.3")) {
            return MinecraftVersion.v1_21_3;
        } else if (version.contains("1.21.2")) {
            return MinecraftVersion.v1_21_2;
        } else if (version.contains("1.21.1")) {
            return MinecraftVersion.v1_21_1;
        } else if (version.contains("1.21")) {
            return MinecraftVersion.v1_21;
        } else if (version.contains("1.20.6")) {
            return MinecraftVersion.v1_20_6;
        } else if (version.contains("1.20.5")) {
            return MinecraftVersion.v1_20_5;
        } else if (version.contains("1.20.4")) {
            return MinecraftVersion.v1_20_4;
        } else if (version.contains("1.20.3")) {
            return MinecraftVersion.v1_20_3;
        } else if (version.contains("1.20.2")) {
            return MinecraftVersion.v1_20_2;
        } else if (version.contains("1.20.1")) {
            return MinecraftVersion.v1_20_1;
        } else if (version.contains("1.20")) {
            return MinecraftVersion.v1_20;
        } else if (version.contains("1.19.4")) {
            return MinecraftVersion.v1_19_4;
        } else if (version.contains("1.19.3")) {
            return MinecraftVersion.v1_19_3;
        } else if (version.contains("1.19.2")) {
            return MinecraftVersion.v1_19_2;
        } else if (version.contains("1.19.1")) {
            return MinecraftVersion.v1_19_1;
        } else if (version.contains("1.19")) {
            return MinecraftVersion.v1_19;
        }

        return null;
    }

    public static boolean isVersionAtLeast(MinecraftVersion version) {
        MinecraftVersion current = getCurrentVersion();
        if (current == null) {
            return false;
        }
        return current.getProtocolVersion() >= version.getProtocolVersion();
    }
}
