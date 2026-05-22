package com.hotbarsort.client;

import com.hotbarsort.config.HotbarConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> HotbarConfig.createConfigScreen(parent);
    }
}