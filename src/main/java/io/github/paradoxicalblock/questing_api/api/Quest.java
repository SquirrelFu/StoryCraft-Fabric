package io.github.paradoxicalblock.questing_api.api;

import net.minecraft.item.ItemStack;

public class Quest {

    public String registry_name;
    public String profession;
    private ItemStack icon;
    private QuestTask task;

    public Quest(String registry_name, ItemStack icon, QuestTask task, String profession) {
        this.registry_name = registry_name;
        this.icon = icon;
        this.task = task;
        this.profession = profession;
    }

    public String getRegistryName() {
        return registry_name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public QuestTask getTask() {
        return task;
    }

    public String getProfession() {
        return profession;
    }

}