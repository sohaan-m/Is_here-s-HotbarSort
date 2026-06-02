package com.hotbarsort.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import java.util.UUID;

public class Preset {
    private final UUID id;
    private String name;
    private final ItemStack[] layout;
    private int quickHotkeySlot; // -1 if not assigned, 1, 2, or 3 for keybinds

    public Preset(String name, PlayerEntity player) {
        this.id = UUID.randomUUID(); // Unique ID for local JSON saving files
        this.name = name;
        this.layout = new ItemStack[9];
        this.quickHotkeySlot = -1;

        var inv = player.getInventory();
        // Deep copy the active hotbar items safely using public getter loops
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            this.layout[i] = stack.isEmpty() ? ItemStack.EMPTY : new ItemStack(stack.getItem());
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ItemStack[] getLayout() { return layout; }
    public int getQuickHotkeySlot() { return quickHotkeySlot; }
    public void setQuickHotkeySlot(int slot) { this.quickHotkeySlot = slot; }
}