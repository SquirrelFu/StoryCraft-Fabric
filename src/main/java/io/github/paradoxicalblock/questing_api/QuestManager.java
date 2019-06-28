package io.github.paradoxicalblock.questing_api;

import io.github.paradoxicalblock.questing_api.api.Quest;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QuestManager {

    private static Collection<Quest> quests = new ConcurrentLinkedQueue<>();

    public static void registerQuests(Quest... quests) {
        QuestManager.quests.addAll(Arrays.asList(quests));
    }

    public static Collection<Quest> getQuests() {
        return quests;
    }

}