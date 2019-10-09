package io.github.paradoxicalblock.storycraft.quests;

import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.questing_api.api.QuestReward;
import io.github.paradoxicalblock.questing_api.api.QuestTask;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import io.github.paradoxicalblock.storycraft.socialVillager.Professions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TestingQuest extends Quest {

    public TestingQuest() {
        super(
            new Identifier(StoryCraft.MOD_ID, "testing_idk"),
            new ItemStack(Items.BEEF),
            new QuestTask[]{
                new QuestTask("Test 1", "Testing"),
                new QuestTask("Test 2", "Testing"),
                new QuestTask("Test 3", "Testing"),
                new QuestTask("Test 4", "Testing"),
                new QuestTask("Test 5", "Testing"),
                new QuestTask("Test 6", "Testing")
            },
            new QuestReward[]{
                new QuestReward(new ItemStack(Items.MUSIC_DISC_11, 10), 10)
            },
            Professions.ARCHITECT.name
        );
    }

}
