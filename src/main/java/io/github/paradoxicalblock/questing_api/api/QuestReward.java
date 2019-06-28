package io.github.paradoxicalblock.questing_api.api;

import net.minecraft.item.ItemStack;

public class QuestReward {

    private ItemStack reward;
    private int xpReward;

    public QuestReward(ItemStack reward, int xpReward) {
        this.reward = reward;
        this.xpReward = xpReward;
    }

    public ItemStack getItemReward() {
        return reward;
    }

    public int getXPReward() {
        return xpReward;
    }

}