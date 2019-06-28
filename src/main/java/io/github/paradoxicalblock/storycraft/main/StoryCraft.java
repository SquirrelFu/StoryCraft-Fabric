package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.questing_api.api.QuestReward;
import io.github.paradoxicalblock.questing_api.api.QuestTask;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.socialVillager.VillagerProfessions;
import io.github.paradoxicalblock.storycraft.util.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
import net.mcft.copy.wearables.api.IWearablesItemHandler;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StoryCraft implements ModInitializer {

	public static final String MOD_ID = "storycraft";

	public static EntityType<SocialVillager> SOCIAL_VILLAGER;
	public static final Logger LOGGER = LogManager.getLogger("[StoryCraft]");

    @Override
    public void onInitialize() {
		IWearablesItemHandler.register(ShieldArmorHandler.INSTANCE);
		SOCIAL_VILLAGER = EntityRegistryBuilder
			.<SocialVillager>createBuilder("social_villager")
			.entity((var1, var2) -> new SocialVillager(var2))
			.category(EntityCategory.CREATURE)
			.egg(5651507, 12422002)
			.size(EntityDimensions.fixed(0.5F, 1.95F))
			.tracker(64, 3, false)
			.build();

		QuestManager.registerQuests(
				new Quest(
						new Identifier(MOD_ID, "test"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						VillagerProfessions.BAKER.getProfession()
				),
				new Quest(
						new Identifier(MOD_ID, "test2"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 2", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test3"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 3", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test4"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 4", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test5"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 5", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test6"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 6", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test7"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 7", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test8"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 8", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test9"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 9", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test10"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 10", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test11"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 11", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test12"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 12", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				),
				new Quest(
						new Identifier(MOD_ID, "test13"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing 13", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				)
		);
    }
}
