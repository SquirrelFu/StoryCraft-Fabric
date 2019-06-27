package io.github.paradoxicalblock.storycraft.inventory;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import net.minecraft.inventory.BasicInventory;

public class InventoryMCA extends BasicInventory {
    private SocialVillager villager;

    public InventoryMCA(SocialVillager villager) {
        super(27);
        this.villager = villager;
    }

}