package io.github.paradoxicalblock.storycraft.util;

import io.github.paradoxicalblock.storycraft.socialVillager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.UUID;

public class References {

    public static final UUID ZERO_UUID = new UUID(0, 0);

    public static ItemStack getDefaultHeldItem(VillagerProfession profession) {
        switch (profession.getProfession()) {
            case "Guard":
                return ItemStackCache.get(Items.DIAMOND_SWORD);
            case "Archer":
                return ItemStackCache.get(Items.BOW);
            case "Farmer":
                return ItemStackCache.get(Items.IRON_HOE);
            case "Miner":
                return ItemStackCache.get(Items.IRON_PICKAXE);
            case "Lumberjack":
                return ItemStackCache.get(Items.IRON_AXE);
            case "Butcher":
                return ItemStackCache.get(Items.IRON_SWORD);
        }
        return ItemStack.EMPTY;
    }

}
