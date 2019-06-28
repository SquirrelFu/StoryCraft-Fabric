package io.github.paradoxicalblock.storycraft.main;

import com.google.common.collect.ImmutableMap;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

import java.util.Map;

public class ClientCore implements ClientModInitializer {
	public static Map<String, SocialVillagerRenderer> socialVillagerRendererMap;

    public void onInitializeClient() {

    }

    public static void addSocialVillagerRenderers(EntityRenderDispatcher entityRenderDispatcher) {
    	socialVillagerRendererMap = ImmutableMap.of("Male", new SocialVillagerRenderer(entityRenderDispatcher, false), "Female", new SocialVillagerRenderer(entityRenderDispatcher, true));
	}

}
