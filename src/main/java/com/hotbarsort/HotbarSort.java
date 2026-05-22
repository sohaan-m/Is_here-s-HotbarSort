package com.hotbarsort;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarSort implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarsort");

    @Override
    public void onInitialize() {
        LOGGER.info("HotbarSort loaded!");
    }
}