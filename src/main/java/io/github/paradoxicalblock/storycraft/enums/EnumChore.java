package io.github.paradoxicalblock.storycraft.enums;

public enum EnumChore {
    NONE(0, "none", null),
    PROSPECT(1, "gui.label.prospecting", "Pickaxe"),
    HARVEST(2, "gui.label.harvesting", "Hoe"),
    CHOP(3, "gui.label.chopping", "Axe"),
    HUNT(4, "gui.label.hunting", "Sword"),
    FISH(5, "gui.label.fishing", "Fishing_Rod");

    int id;
    String friendlyName;
    String toolType;

    EnumChore(int id, String friendlyName, String toolType) {
        this.id = id;
        this.friendlyName = friendlyName;
        this.toolType = toolType;
    }

    public static EnumChore byId(int id) {
        for (EnumChore chore : values()) {
            if (chore.id == id) {
                return chore;
            }
        }
        return NONE;
    }

    public int getId() {
        return id;
    }

    public String getToolType() {
        return toolType;
    }

}