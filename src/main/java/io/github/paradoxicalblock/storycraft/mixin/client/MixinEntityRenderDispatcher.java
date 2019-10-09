package io.github.paradoxicalblock.storycraft.mixin.client;

import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import io.github.paradoxicalblock.storycraft.main.ClientCore;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, CallbackInfo callbackInfo) {
        ClientCore.addFamiliarsEntityRenderers((EntityRenderDispatcher) (Object) this);
    }

    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("TAIL"), cancellable = true)
    public void getRenderer(Entity entity, CallbackInfoReturnable<EntityRenderer> callbackInfoReturnable) {
        if (entity instanceof FamiliarsEntity) {
            callbackInfoReturnable.setReturnValue(ClientCore.familiarsEntityRendererMap.get(((FamiliarsEntity) entity).get(FamiliarsEntity.genderUnified)));
        }
    }

}