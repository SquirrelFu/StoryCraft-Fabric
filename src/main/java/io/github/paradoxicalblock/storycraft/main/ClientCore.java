package io.github.paradoxicalblock.storycraft.main;

import com.google.common.collect.ImmutableMap;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

import java.util.Map;

public class ClientCore implements ClientModInitializer {
    public static Map<String, FamiliarsEntityRenderer> familiarsEntityRendererMap;

    public static void addFamiliarsEntityRenderers(EntityRenderDispatcher entityRenderDispatcher) {
        familiarsEntityRendererMap = ImmutableMap.of("Male", new FamiliarsEntityRenderer(entityRenderDispatcher, false), "Female", new FamiliarsEntityRenderer(entityRenderDispatcher, true));
    }

    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(StoryCraft.SOCIAL_VILLAGER, FamiliarsEntityRenderer::new);
    }

}
