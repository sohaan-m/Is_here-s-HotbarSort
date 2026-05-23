package com.hotbarsort.storage;

import com.hotbarsort.config.HotbarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.screen.slot.SlotActionType;

public class HotbarStorage {

    private static final ItemStack[] saved = new ItemStack[9];

    private static int delayTimer = 0;
    private static boolean isSortingActive = false;
    private static int currentTargetHotbarSlot = 0;

    private static void sendMinimalChatMessage(MinecraftClient client, String message) {
        if (client.player != null) {
            // Prints a local, non-server gray chat message that fades naturally
            client.player.sendMessage(Text.literal("§8[HotbarSort] §7" + message), false);
        }
    }

    public static void saveHotbar(MinecraftClient client) {
        if (client.player == null) return;

        var inv = client.player.getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            saved[i] = stack.isEmpty() ? ItemStack.EMPTY : new ItemStack(stack.getItem());
        }

        sendMinimalChatMessage(client, "Hotbar layout saved.");
    }

    public static void startSorting(MinecraftClient client) {
        if (client.player == null) return;

        if (saved[0] == null) {
            sendMinimalChatMessage(client, "No configuration saved.");
            return;
        }

        if (client.player.currentScreenHandler != client.player.playerScreenHandler) {
            sendMinimalChatMessage(client, "Close inventory before starting.");
            return;
        }

        isSortingActive = true;
        currentTargetHotbarSlot = 0;
        delayTimer = 0;
        sendMinimalChatMessage(client, "Sorting started...");
    }

    public static void tickSortingSystem() {
        if (!isSortingActive) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) {
            isSortingActive = false;
            return;
        }

        if (client.player.currentScreenHandler != client.player.playerScreenHandler) {
            isSortingActive = false;
            sendMinimalChatMessage(client, "Sorting canceled: Menu opened.");
            return;
        }

        if (delayTimer > 0) {
            delayTimer--;
            return;
        }

        var player = client.player;
        var im = client.interactionManager;
        var inv = player.getInventory();

        while (currentTargetHotbarSlot < 9) {
            ItemStack target = saved[currentTargetHotbarSlot];
            int targetScreenSlot = 36 + currentTargetHotbarSlot;

            if (target == null || target.isEmpty() || inv.getStack(currentTargetHotbarSlot).getItem() == target.getItem()) {
                currentTargetHotbarSlot++;
                continue;
            }

            boolean found = false;
            int sourceScreenSlot = -1;

            for (int s = 9; s <= 44; s++) {
                int invIndex = (s >= 36) ? (s - 36) : s;
                ItemStack current = inv.getStack(invIndex);

                if (!current.isEmpty() && current.getItem() == target.getItem()) {
                    sourceScreenSlot = s;
                    found = true;
                    break;
                }
            }

            if (!found) {
                ItemStack offhand = player.getOffHandStack();
                if (!offhand.isEmpty() && offhand.getItem() == target.getItem()) {
                    sourceScreenSlot = 45;
                    found = true;
                }
            }

            if (found) {
                im.clickSlot(player.playerScreenHandler.syncId, sourceScreenSlot, 0, SlotActionType.PICKUP, player);
                im.clickSlot(player.playerScreenHandler.syncId, targetScreenSlot, 0, SlotActionType.PICKUP, player);
                if (!player.currentScreenHandler.getCursorStack().isEmpty()) {
                    im.clickSlot(player.playerScreenHandler.syncId, sourceScreenSlot, 0, SlotActionType.PICKUP, player);
                }

                delayTimer = HotbarConfig.getTickDelayRate();
                currentTargetHotbarSlot++;
                return;
            }

            currentTargetHotbarSlot++;
        }

        isSortingActive = false;
        sendMinimalChatMessage(client, "Hotbar sorted successfully.");
    }
}