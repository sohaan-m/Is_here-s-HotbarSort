package com.hotbarsort.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder; // Imported for the ON/OFF button
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HotbarConfig {
    public static class ConfigData {
        @SerialEntry
        public int tickDelayRate = 2; // Default delay between each item

        @SerialEntry
        public boolean enabled = true; // Your enabled variable is perfectly declared here!
    }

    public static final ConfigClassHandler<ConfigData> HANDLER = ConfigClassHandler.createBuilder(ConfigData.class)
            .id(net.minecraft.util.Identifier.of("hotbarsort", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("hotbarsort.json"))
                    .build())
            .build();

    // Added a quick helper method so you can easily check if the mod is on elsewhere in your code
    public static boolean isEnabled() {
        return HANDLER.instance().enabled;
    }

    public static int getTickDelayRate() {
        return HANDLER.instance().tickDelayRate;
    }

    public static Screen createConfigScreen(Screen parentScreen) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Hotbar Sort Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General Settings"))

                        // 1. Cleaned up and fixed your Boolean Toggle Option
                        .option(Option.<Boolean>createBuilder() // Capital 'B' Boolean wrapper
                                .name(Text.literal("Enable Or Disable The Mod"))
                                .description(OptionDescription.of(Text.literal("Turns the mod off or on.")))
                                .binding(
                                        true, // Default state
                                        () -> HANDLER.instance().enabled, // Read method
                                        newVal -> HANDLER.instance().enabled = newVal // Write method
                                )
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .valueFormatter(val -> val ? Text.literal("ON") : Text.literal("OFF"))
                                        .coloured(true) // Makes ON green and OFF red!
                                )
                                .build()) // Nicely closed out the boolean option block

                        // 2. Separated and cleaned up your Delay Slider Option
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Delay Between Each Item"))
                                .description(OptionDescription.of(Text.literal("Game ticks to wait between moving each individual item. 0 = instant, 20 = 1 second per item (9 seconds total).")))
                                .binding(
                                        2, // Default
                                        () -> HANDLER.instance().tickDelayRate, // Read method
                                        newVal -> HANDLER.instance().tickDelayRate = newVal // Write method
                                )
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 20)
                                        .step(1)
                                )
                                .build())
                        .build())
                .save(HANDLER::save)
                .build()
                .generateScreen(parentScreen);
    }
}