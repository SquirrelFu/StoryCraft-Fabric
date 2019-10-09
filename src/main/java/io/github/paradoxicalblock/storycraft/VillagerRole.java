package io.github.paradoxicalblock.storycraft;

public enum VillagerRole {
    VILLAGER(1),
    DEFENDER(2),
    VENDOR(4),
    ENEMY(8),
    VISITOR(16);

    public final int value;


    VillagerRole(int v) {
        this.value = v;
    }
}
