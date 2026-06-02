package com.hotbarsort.client;

import com.hotbarsort.config.HotbarConfig;
import com.hotbarsort.storage.HotbarStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class HotbarSortClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // BOOT SEQUENCE: Load saved JSON data from disk into memory!
        HotbarConfig.load();

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            HotbarCommand.register(dispatcher);
        });

        // Run background tick checks
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || !HotbarConfig.INSTANCE.enabled) {
                return;
            }
            HotbarStorage.tickSortingSystem();
        });
    }
}