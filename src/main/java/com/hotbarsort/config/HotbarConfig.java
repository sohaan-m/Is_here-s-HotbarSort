package com.hotbarsort.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotbarConfig {
    public static HotbarConfig INSTANCE = new HotbarConfig();

    // Creates a dedicated JSON file in your Minecraft config folder
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("hotbarsort.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public int tickDelayRate = 2;

    // The HashMap allows for infinite profiles tied to custom text strings!
    public Map<String, List<String>> savedProfiles = new HashMap<>();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, HotbarConfig.class);
            } catch (Exception e) {
                System.err.println("[HotbarSort] Failed to load configuration!");
            }
        }
        // Safety check to ensure the map is never null on first boot
        if (INSTANCE.savedProfiles == null) {
            INSTANCE.savedProfiles = new HashMap<>();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (Exception e) {
            System.err.println("[HotbarSort] Failed to save configuration!");
        }
    }

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("HotbarSort Configuration"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General Settings"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Enable Mod"))
                                .binding(true, () -> INSTANCE.enabled, val -> INSTANCE.enabled = val)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Tick Delay Rate"))
                                .binding(2, () -> INSTANCE.tickDelayRate, val -> INSTANCE.tickDelayRate = val)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 20).step(1))
                                .build())
                        .build())
                .save(HotbarConfig::save) // Triggers disk save when UI is closed
                .build()
                .generateScreen(parent);
    }
}