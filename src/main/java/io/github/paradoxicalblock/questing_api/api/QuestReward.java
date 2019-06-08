package io.github.paradoxicalblock.questing_api.api;

import net.minecraft.item.ItemStack;

public class QuestReward {

    public ItemStack reward;
    public int xpReward;

    public QuestReward(ItemStack reward, int xpReward) {
        this.reward = reward;
        this.xpReward = xpReward;
    }

    public ItemStack getReward() {
        return reward;
    }

    public int getXpReward() {
        return xpReward;
    }

}