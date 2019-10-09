package io.github.paradoxicalblock.storycraft.inventory;

import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import net.minecraft.inventory.BasicInventory;

public class InventoryMCA extends BasicInventory {

    public InventoryMCA(FamiliarsEntity villager) {
        super(27);
    }

}