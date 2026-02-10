package me.noibecoded.xpultimate.platform.handlers;

import me.noibecoded.xpultimate.platform.IPlatformHandler;

public class Handler_1_21 implements IPlatformHandler {

    @Override
    public String getPlatformName() {
        return "1.21+";
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
