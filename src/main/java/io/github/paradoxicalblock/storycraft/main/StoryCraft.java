package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.questing_api.QuestDataManager;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.util.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StoryCraft implements ModInitializer {

	public static final String MOD_ID = "storycraft";

	public static EntityType<SocialVillager> SOCIAL_VILLAGER;
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final QuestDataManager QUEST_DATA_MANAGER = new QuestDataManager();

    @Override
    public void onInitialize() {
    	QUEST_DATA_MANAGER.registerReloadListener();
		SOCIAL_VILLAGER = EntityRegistryBuilder
			.<SocialVillager>createBuilder("social_villager")
			.entity((var1, var2) -> new SocialVillager(var2))
			.category(EntityCategory.CREATURE)
			.egg(5651507, 12422002)
			.size(EntityDimensions.fixed(0.5F, 1.95F))
			.tracker(64, 3, false)
			.build();
    }
}
