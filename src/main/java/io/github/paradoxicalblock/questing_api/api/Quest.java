package io.github.paradoxicalblock.questing_api.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Quest {

    public Identifier name;
    public ItemStack icon;
    public QuestTask task;
    public String profession;

    public Quest(Identifier name, ItemStack icon, QuestTask task, String profession) {
        this.name = name;
        this.icon = icon;
        this.task = task;
        this.profession = profession;
    }

    public Identifier getName() {
        return name;
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