package com.hotbarsort.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HotbarConfig {

    // 1. Define the internal class that acts as our JSON structural data map
    public static class ConfigData {
        @SerialEntry
        public int tickDelayRate = 2; // Default delay between each item
    }

    // 2. Set up the File Handler pointing to config/hotbarsort.json
    public static final ConfigClassHandler<ConfigData> HANDLER = ConfigClassHandler.createBuilder(ConfigData.class)
            .id(net.minecraft.util.Identifier.of("hotbarsort", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("hotbarsort.json"))
                    .build())
            .build();

    // Shortcut getter so your HotbarStorage.java code can still read it easily
    public static int getTickDelayRate() {
        return HANDLER.instance().tickDelayRate;
    }

    // 3. Build the GUI and link the slider directly to the file data
    public static Screen createConfigScreen(Screen parentScreen) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Hotbar Sort Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General Settings"))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Delay Between Each Item"))
                                .description(OptionDescription.of(Text.literal("Game ticks to wait between moving each individual item. 0 = instant, 20 = 1 second per item (9 seconds total).")))
                                .binding(
                                        2, // Default
                                        () -> HANDLER.instance().tickDelayRate, // Read from file instance
                                        newVal -> HANDLER.instance().tickDelayRate = newVal // Write to file instance
                                )
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 20)
                                        .step(1)
                                )
                                .build())
                        .build())
                .save(HANDLER::save) // CRITICAL: This line saves the hotbarsort.json file when the user clicks save!
                .build()
                .generateScreen(parentScreen);
    }
}