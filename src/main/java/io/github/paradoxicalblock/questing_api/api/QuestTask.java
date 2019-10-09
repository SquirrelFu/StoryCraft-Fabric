package io.github.paradoxicalblock.questing_api.api;

public class QuestTask {

    private String name;
    private String description;
    private int currentProgress, maxProgress;

    public QuestTask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

}