package io.github.paradoxicalblock.storycraft.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.paradoxicalblock.storycraft.entity.SocialVillagerFemale;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerFemaleRenderer;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerMale;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerMaleRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ReloadableResourceManager;

@Mixin(EntityRenderDispatcher.class)
public abstract class AddRenderers {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void renderMixin(TextureManager textureManager_1, ItemRenderer itemRenderer_1, ReloadableResourceManager reloadableResourceManager_1, CallbackInfo info) {

		this.method_17145(SocialVillagerMale.class, new SocialVillagerMaleRenderer((EntityRenderDispatcher)(Object)this));
		this.method_17145(SocialVillagerFemale.class, new SocialVillagerFemaleRenderer((EntityRenderDispatcher)(Object)this));
	}
	@Shadow(aliases = "this$renderers") @Final
	private Map renderers;
	
	@Shadow(aliases= {"this$method_17145"})
	private <T extends Entity> void method_17145(Class<T> class_1, EntityRenderer<? super T> entityRenderer_1) {
		EntityRenderDispatcher thing = (EntityRenderDispatcher)(Object) this;
        this.renderers.put(class_1, entityRenderer_1);
    }
	
}