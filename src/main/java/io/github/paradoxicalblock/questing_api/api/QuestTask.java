package io.github.paradoxicalblock.questing_api.api;

public class QuestTask {

    private String name;
    private String description;
    private QuestReward reward;

    public QuestTask(String name, String description, QuestReward reward) {
        this.name = name;
        this.description = description;
        this.reward = reward;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestReward getReward() {
        return reward;
    }

}