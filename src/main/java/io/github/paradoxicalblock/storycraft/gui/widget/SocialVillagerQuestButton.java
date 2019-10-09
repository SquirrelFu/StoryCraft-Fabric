package io.github.paradoxicalblock.storycraft.gui.widget;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.paradoxicalblock.questing_api.api.Quest;
import net.minecraft.text.LiteralText;

public class SocialVillagerQuestButton extends WButton {

    private Quest quest;

    public SocialVillagerQuestButton(Quest quest) {
        super(new LiteralText(quest != null ? quest.getTask().getName() : ""));
        this.quest = quest;
        setOnClick(() -> System.out.println(quest.getTask().getName()));
    }

    @Override
    public void onClick(int x, int y, int button) {
        System.out.println(quest.getTask().getName());
    }

    public Quest getQuest() {
        return quest;
    }

}