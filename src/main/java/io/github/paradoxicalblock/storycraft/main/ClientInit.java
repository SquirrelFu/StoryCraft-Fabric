package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;

public class ClientInit implements ClientModInitializer {

    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(SocialVillager.class, ((entityRenderDispatcher, context) -> new SocialVillagerRenderer(entityRenderDispatcher)));
    }

}
