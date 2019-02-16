package io.github.paradoxicalblock.storycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import io.github.paradoxicalblock.storycraft.world.DataManager;
import net.minecraft.resource.ResourceReloadListener;

@Mixin(ResourceReloadListener.class)
public class OnWorldLoad {

	@Inject(at=@At("HEAD"),method="apply")
	public static void worldLoadIntegrator()
	{
		new DataManager();
	}
}
