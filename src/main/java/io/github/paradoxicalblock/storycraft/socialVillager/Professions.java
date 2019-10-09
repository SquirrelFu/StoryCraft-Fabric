package io.github.paradoxicalblock.storycraft.socialVillager;

import net.minecraft.util.StringIdentifiable;

public enum Professions implements StringIdentifiable {

    LUMBERJACK("Lumberjack"),
    FARMER("Farmer"),
    ARCHITECT("Architect"),
    BLACKSMITH("Blacksmith"),
    ENCHANTER("Enchanter"),
    DRUID("Druid"),
    BUTCHER("Butcher"),
    LIBRARIAN("Librarian"),
    NOMAD("Nomad"),
    BAKER("Baker"),
    PRIEST("Priest"),
    MINER("Miner"),
    GUARD("Guard"),
    ARCHER("Archer");

    public String name;

    Professions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String asString() {
        return name;
    }

}