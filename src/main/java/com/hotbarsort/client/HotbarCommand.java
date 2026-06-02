package com.hotbarsort.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.hotbarsort.config.HotbarConfig;
import com.hotbarsort.storage.HotbarStorage;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HotbarCommand {

    private static final SuggestionProvider<FabricClientCommandSource> PROFILE_SUGGESTIONS = (context, builder) -> {
        String remaining = builder.getRemaining().toLowerCase();
        for (String profile : HotbarConfig.INSTANCE.savedProfiles.keySet()) {
            if (profile.toLowerCase().startsWith(remaining)) {
                builder.suggest(profile);
            }
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        LiteralArgumentBuilder<FabricClientCommandSource> mainCommand = ClientCommandManager.literal("hotbarsort")
                .then(ClientCommandManager.literal("save")
                        .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                .executes(context -> saveHotbar(StringArgumentType.getString(context, "name")))
                        )
                )
                .then(ClientCommandManager.literal("delete")
                        .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                .suggests(PROFILE_SUGGESTIONS)
                                .executes(context -> deleteHotbar(StringArgumentType.getString(context, "name")))
                        )
                )
                .then(ClientCommandManager.literal("list")
                        .executes(context -> listHotbars())
                )
                .then(ClientCommandManager.argument("profilename", StringArgumentType.word())
                        .suggests(PROFILE_SUGGESTIONS)
                        .executes(context -> loadHotbar(StringArgumentType.getString(context, "profilename")))
                );

        dispatcher.register(mainCommand);
        dispatcher.register(ClientCommandManager.literal("hbsort").redirect(mainCommand.build()));
    }

    // THE FIX: Cleaner, two-tone prefix with proper capitalization
    private static void sendFeedback(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§8[§fHotbarSort§8] §7" + text), false);
        }
    }

    private static boolean isModDisabled() {
        if (!HotbarConfig.INSTANCE.enabled) {
            sendFeedback("Mod is currently disabled in settings.");
            return true;
        }
        return false;
    }

    private static int saveHotbar(String profileName) {
        if (isModDisabled()) return 0;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return 0;

        profileName = profileName.toLowerCase();
        List<String> itemIds = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.isEmpty()) {
                itemIds.add("minecraft:air");
            } else {
                itemIds.add(Registries.ITEM.getId(stack.getItem()).toString());
            }
        }

        HotbarConfig.INSTANCE.savedProfiles.put(profileName, itemIds);
        HotbarConfig.save();

        sendFeedback("Hotbar saved as '" + profileName + "'.");
        return 1;
    }

    private static int loadHotbar(String profileName) {
        if (isModDisabled()) return 0;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return 0;

        profileName = profileName.toLowerCase();
        List<String> savedIds = HotbarConfig.INSTANCE.savedProfiles.get(profileName);

        if (savedIds == null || savedIds.isEmpty()) {
            sendFeedback("Profile not found.");
            return 0;
        }

        ItemStack[] targets = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            String rawId = savedIds.get(i);
            if (rawId.equals("minecraft:air")) {
                targets[i] = ItemStack.EMPTY;
            } else {
                targets[i] = new ItemStack(Registries.ITEM.get(Identifier.of(rawId)));
            }
        }

        HotbarStorage.startSorting(client, targets);
        sendFeedback("Loading layout...");
        return 1;
    }

    private static int deleteHotbar(String profileName) {
        if (isModDisabled()) return 0;

        profileName = profileName.toLowerCase();
        if (HotbarConfig.INSTANCE.savedProfiles.remove(profileName) != null) {
            HotbarConfig.save();
            sendFeedback("Profile deleted.");
        } else {
            sendFeedback("Profile not found.");
        }
        return 1;
    }

    private static int listHotbars() {
        if (isModDisabled()) return 0;

        if (HotbarConfig.INSTANCE.savedProfiles.isEmpty()) {
            sendFeedback("No profiles saved.");
            return 0;
        }
        String list = String.join(", ", HotbarConfig.INSTANCE.savedProfiles.keySet());
        sendFeedback("Profiles: " + list);
        return 1;
    }
}