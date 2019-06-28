package io.github.paradoxicalblock.storycraft.main;

import net.mcft.copy.wearables.api.IWearablesItemHandler;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.Collections;

public class BannerItemHandler implements IWearablesItemHandler {

    public static final BannerItemHandler INSTANCE = new BannerItemHandler();

    @Override
    public Collection<String> getHandledSpecialItems() {
        return Collections.singleton("ItemShield");
    }

    @Override
    public String getSpecialItems(Item item) {
        return "ItemShield";
    }

}