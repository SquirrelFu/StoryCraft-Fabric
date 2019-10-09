package io.github.paradoxicalblock.storycraft;

public enum ItemTagType {
    VILLAGER("villager", "Â§2"),
    STRUCTURE("struct", "");

    public final String tag;
    public final String colorPrefix;

    ItemTagType(String tag, String colorPrefix) {
        this.tag = tag;
        this.colorPrefix = colorPrefix;
    }
}
