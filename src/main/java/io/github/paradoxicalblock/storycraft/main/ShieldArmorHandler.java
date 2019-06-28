package io.github.paradoxicalblock.storycraft.main;

import net.mcft.copy.wearables.api.IWearablesItemHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;

import java.util.Collection;
import java.util.Collections;

public class ShieldArmorHandler implements IWearablesItemHandler {

    public static final ShieldArmorHandler INSTANCE = new ShieldArmorHandler();

    @Override
    public Collection<String> getHandledSpecialItems() {
        return Collections.singleton("ItemShield");
    }

    @Override
    public String getSpecialItems(Item item) {
        return (item instanceof ShieldItem) ? "ItemShield" : null;
    }

}