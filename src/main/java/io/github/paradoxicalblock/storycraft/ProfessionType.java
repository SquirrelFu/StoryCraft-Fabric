package io.github.paradoxicalblock.storycraft;

public enum ProfessionType {
    BARD("bard", true),
    BLACKSMITH("blacksmith", true),
    BUTCHER("butcher", true),
    CHEF("chef", true),
    CLERIC("cleric", true),
    DRUID("druid", true),
    ENCHANTER("enchanter", true),
    FARMER("farmer", true),
    GUARD("guard", true),
    CAPTAIN("captain", false),
    LUMBERJACK("lumberjack", true),
    MINER("miner", true),
    RANCHER("rancher", true),
    TEACHER("teacher", true),
    CHILD("child", false),
    NITWIT("nitwit", false),
    NOMAD("nomad", false);

    public final String name;
    public final boolean canCopy;

    ProfessionType(String name, boolean copy) {
        this.name = name;
        this.canCopy = copy;
    }
}
