package me.noibecoded.xpultimate.api;

public enum MinecraftVersion {
    v1_19("1.19", 759),
    v1_19_1("1.19.1", 760),
    v1_19_2("1.19.2", 761),
    v1_19_3("1.19.3", 762),
    v1_19_4("1.19.4", 763),
    v1_20("1.20", 764),
    v1_20_1("1.20.1", 765),
    v1_20_2("1.20.2", 766),
    v1_20_3("1.20.3", 767),
    v1_20_4("1.20.4", 768),
    v1_20_5("1.20.5", 769),
    v1_20_6("1.20.6", 770),
    v1_21("1.21", 771),
    v1_21_1("1.21.1", 772),
    v1_21_2("1.21.2", 773),
    v1_21_3("1.21.3", 774);

    private final String version;
    private final int protocolVersion;

    MinecraftVersion(String version, int protocolVersion) {
        this.version = version;
        this.protocolVersion = protocolVersion;
    }

    public String getVersion() {
        return version;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getDisplayVersion() {
        return "Minecraft " + version;
    }
}
