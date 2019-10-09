package io.github.paradoxicalblock.storycraft.structures;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum VillageStructureType {

    STORAGE, BLACKSMITH, MINESHAFT, TOWNHALL, COW_PEN, SHEEP_PEN, PIG_PEN, CHICKEN_COOP, BUTCHER, TAVERN, LIBRARY, SCHOOL, HOME2, HOME4, HOME6, GUARD_POST, MERCHANT_STALL, BARRACKS, KITCHEN;


    public static final String TOWN_HALL_NAME = "Town Hall";
    public final ItemStack itemStack;
    public final int tilesPerVillager;

    VillageStructureType(ItemStack itemStack, int tilesPerVillager) {
        this.itemStack = itemStack;
        this.tilesPerVillager = tilesPerVillager;
    }

    public boolean isItemEqual(ItemStack i) {
        return this.itemStack.isItemEqual(i);
    }

    public boolean isHome() {
        return (this == HOME2 || this == HOME4 || this == HOME6);
    }

    public abstract VillageStructure create(World paramWorld, Village paramVillage, ItemFrameEntity paramEntityItemFrame);

}
