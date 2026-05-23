package com.hotbarsort.client;

import com.hotbarsort.config.HotbarConfig;
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
        HotbarConfig.HANDLER.load();

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
            if (!HotbarConfig.isEnabled()) {
                saveKey.setPressed(false);
                sortKey.setPressed(false);
                return;
            }

            if (saveKey.wasPressed()) {
                HotbarStorage.saveHotbar(client);
                while (saveKey.wasPressed()) {}
            }

            if (sortKey.wasPressed()) {
                HotbarStorage.startSorting(client);
                while (sortKey.wasPressed()) {}
            }

            HotbarStorage.tickSortingSystem();
        });
    }
}