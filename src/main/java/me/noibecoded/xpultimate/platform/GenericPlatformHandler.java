package me.noibecoded.xpultimate.platform;

public class GenericPlatformHandler implements IPlatformHandler {

    @Override
    public String getPlatformName() {
        return "Unknown";
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
