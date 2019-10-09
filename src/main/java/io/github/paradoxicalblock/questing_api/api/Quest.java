package io.github.paradoxicalblock.questing_api.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Quest {

    public String registry_name;
    public String profession;
    private ItemStack icon;
    private QuestTask[] task;
    private QuestReward[] reward;

    public Quest(Identifier registry_name, ItemStack icon, QuestTask[] task, QuestReward[] reward, String profession) {
        this.registry_name = registry_name.toString();
        this.icon = icon;
        this.task = task;
        this.reward = reward;
        this.profession = profession;
    }

    public Quest(String registry_name, ItemStack icon, QuestTask[] task, QuestReward[] reward, String profession) {
        this.registry_name = registry_name;
        this.icon = icon;
        this.task = task;
        this.reward = reward;
        this.profession = profession;
    }

    public String getRegistryName() {
        return registry_name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public QuestTask[] getTasks() {
        return task;
    }

    public QuestTask getTask() {
        for (QuestTask task : this.getTasks()) {
            return task;
        }
        return getTasks()[0];
    }

    public QuestReward[] getRewards() {
        return reward;
    }

    public QuestReward getReward() {
        for (QuestReward task : this.getRewards()) {
            return task;
        }
        return getRewards()[0];
    }

    public String getProfession() {
        return profession;
    }

}