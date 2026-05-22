package com.hotbarsort.client;

import com.hotbarsort.storage.HotbarStorage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HotbarSortClient implements ClientModInitializer {

    private static KeyBinding saveKey;
    private static KeyBinding sortKey;

    @Override
    public void onInitializeClient() {
        com.hotbarsort.config.HotbarConfig.HANDLER.load();

        System.out.println("HotbarSort loaded!");

        KeyBinding.Category category = KeyBinding.Category.MISC;

        saveKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.hotbarsort.save",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_O,
                        category
                )
        );

        sortKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.hotbarsort.sort",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_P,
                        category
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (saveKey.wasPressed()) {
                HotbarStorage.saveHotbar();
            }

            while (sortKey.wasPressed()) {
                // Instantiates the sorting safety parameters and tracks position
                HotbarStorage.startSorting();
            }

            // CRITICAL: This method must execute every single client tick
            // to count down your slider delay timers and process inventory moves!
            HotbarStorage.tickSortingSystem();
        });
    }
}