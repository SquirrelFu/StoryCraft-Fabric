package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.questing_api.api.QuestReward;
import io.github.paradoxicalblock.questing_api.api.QuestTask;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.socialVillager.VillagerProfessions;
import io.github.paradoxicalblock.storycraft.util.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
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
				)
		);

		QuestManager.registerQuests(
				new Quest(
						new Identifier(MOD_ID, "test"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				)
		);

		QuestManager.registerQuests(
				new Quest(
						new Identifier(MOD_ID, "test"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				)
		);

		QuestManager.registerQuests(
				new Quest(
						new Identifier(MOD_ID, "test"),
						new ItemStack(Items.CAKE),
						new QuestTask("Testing", "This is a test",
								new QuestReward(new ItemStack(Items.CAKE, 4), 10)
						),
						"Baker"
				)
		);
    }
}
