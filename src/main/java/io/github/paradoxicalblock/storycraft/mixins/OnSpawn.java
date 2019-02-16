package io.github.paradoxicalblock.storycraft.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.paradoxicalblock.storycraft.entity.SocialVillagerBase;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerFemale;
import io.github.paradoxicalblock.storycraft.entity.SocialVillagerMale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(EntityType.class)
public class OnSpawn {

	@Inject(at=@At("TAIL"),method="spawn")
	private void spawnReact(World world_1, CompoundTag compoundTag_1, TextComponent textComponent_1, 
			PlayerEntity playerEntity_1, BlockPos blockPos_1, SpawnType spawnType_1, boolean boolean_1, boolean boolean_2,
			CallbackInfo callback, Entity entity_1)
	{
		if (entity_1 instanceof SocialVillagerMale || entity_1 instanceof SocialVillagerFemale)
		{
			SocialVillagerBase base = (SocialVillagerBase) entity_1;
			base.unifiedSetup();
		}
		
	}
}
