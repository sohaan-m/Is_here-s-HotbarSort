package com.hotbarsort.storage;

import com.hotbarsort.config.HotbarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class HotbarStorage {

    private static ItemStack[] activeSortingLayout = null;
    private static boolean isSortingActive = false;
    private static int currentTargetHotbarSlot = 0;
    private static int delayTimer = 0;

    public static void startSorting(MinecraftClient client, ItemStack[] targetLayout) {
        if (client.player == null || targetLayout == null) return;

        if (client.player.currentScreenHandler != client.player.playerScreenHandler) {
            return;
        }

        activeSortingLayout = targetLayout;
        isSortingActive = true;
        currentTargetHotbarSlot = 0;
        delayTimer = 0;
    }

    public static boolean isSorting() {
        return isSortingActive;
    }

    public static void cancelSorting() {
        isSortingActive = false;
        activeSortingLayout = null;
    }

    public static void tickSortingSystem() {
        if (!isSortingActive || activeSortingLayout == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) {
            cancelSorting();
            return;
        }

        if (client.player.currentScreenHandler != client.player.playerScreenHandler) {
            cancelSorting();
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
            ItemStack target = activeSortingLayout[currentTargetHotbarSlot];

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
                im.clickSlot(
                        player.playerScreenHandler.syncId,
                        sourceScreenSlot,
                        currentTargetHotbarSlot,
                        SlotActionType.SWAP,
                        player
                );

                delayTimer = HotbarConfig.INSTANCE.tickDelayRate;
                currentTargetHotbarSlot++;
                return;git add .
            }

            currentTargetHotbarSlot++;
        }

        // THE FIX: Loop finished naturally, send the completion indicator!
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§8[§fHotbarSort§8] §7Sorting complete."), false);
        }

        cancelSorting();
    }
}