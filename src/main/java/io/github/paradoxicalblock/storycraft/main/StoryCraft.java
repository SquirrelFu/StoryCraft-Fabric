package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.questing_api.QuestDataManager;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.util.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
import net.mcft.copy.wearables.api.IWearablesItemHandler;
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
		IWearablesItemHandler.register(ShieldArmorHandler.INSTANCE);
		SOCIAL_VILLAGER = EntityRegistryBuilder
			.<SocialVillager>createBuilder("social_villager")
			.entity((var1, var2) -> new SocialVillager(var2))
			.category(EntityCategory.CREATURE)
			.egg(5651507, 12422002)
			.size(EntityDimensions.fixed(0.5F, 1.95F))
			.tracker(64, 3, false)
			.build();

		/*QuestManager.registerQuests(
				new Quest(
						new Identifier(MOD_ID, "test").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						VillagerProfessions.BAKER.getProfession()
				),
				new Quest(
						new Identifier(MOD_ID, "test2").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 2", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test3").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 3", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test4").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 4", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test5").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 5", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test6").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 6", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test7").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 7", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test8").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 8", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test9").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 9", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test10").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 10", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test11").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 11", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test12").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 12", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test13").toString(),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 13", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				)
		);*/
    }
}
