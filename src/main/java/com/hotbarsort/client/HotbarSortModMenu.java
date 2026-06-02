package com.hotbarsort.client;

import com.hotbarsort.config.HotbarConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class HotbarSortModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HotbarConfig::createScreen;
    }
}