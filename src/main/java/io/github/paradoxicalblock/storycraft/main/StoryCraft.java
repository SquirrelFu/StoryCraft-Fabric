package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerRenderer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;

public class StoryCraft implements ModInitializer {
	
	static FabricEntityTypeBuilder<SocialVillager> builder = FabricEntityTypeBuilder.create(EntityCategory.CREATURE,SocialVillager::new).size(0.5F, 1.95f).trackable(64, 3);
	public static final EntityType<SocialVillager> SOCIAL_VILLAGER = Registry.register(Registry.ENTITY_TYPE,"storycraft:social_villager", builder.build());
	public static final Item SOCIAL_VILLAGER_EGG = Registry.register(Registry.ITEM, "storycraft:social_villager_egg", new SpawnEggItem(SOCIAL_VILLAGER, 5636095, 170, new Item.Settings().itemGroup(ItemGroup.MISC)));
	@Override
	public void onInitialize() {
		EntityRendererRegistry.INSTANCE.register(SocialVillager.class, ((entityRenderDispatcher, context) -> new SocialVillagerRenderer(entityRenderDispatcher)));
	}
	
}
