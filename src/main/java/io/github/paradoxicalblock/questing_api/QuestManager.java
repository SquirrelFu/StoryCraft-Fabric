package io.github.paradoxicalblock.questing_api;

import io.github.paradoxicalblock.questing_api.api.Quest;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {

    private static List<Quest> QUESTS = new ArrayList<>();

    public static void registerQuest(Quest quest) {
        if(!QUESTS.contains(quest)) {
            QuestManager.QUESTS.add(quest);
        }
    }

    public static List<Quest> getQuests() {
        return QUESTS;
    }

}