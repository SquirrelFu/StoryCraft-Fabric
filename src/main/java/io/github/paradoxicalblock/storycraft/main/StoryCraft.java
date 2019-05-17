package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.util.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;

public class StoryCraft implements ModInitializer {

	public static final String MOD_ID = "storycraft";

	public static EntityType<SocialVillager> SOCIAL_VILLAGER;

    @Override
    public void onInitialize() {
		SOCIAL_VILLAGER = EntityRegistryBuilder
			.<SocialVillager>createBuilder("social_villager")
			.entity((var1, var2) -> new SocialVillager(var2))
			.category(EntityCategory.CREATURE)
			.egg(5651507, 12422002)
			.size(EntitySize.constant(0.5F, 1.95F))
			.tracker(64, 3, false)
			.build();
    }
}
