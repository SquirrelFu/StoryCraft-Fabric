package io.github.paradoxicalblock.storycraft.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.paradoxicalblock.storycraft.socialVillager.SocialVillagerData;
import io.github.paradoxicalblock.storycraft.util.TextureAssembler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Environment(value=EnvType.CLIENT)
public class SocialVillagerRenderer extends MobEntityRenderer<SocialVillager, PlayerEntityModel<SocialVillager>> {
	public SocialVillagerRenderer(EntityRenderDispatcher dispatcher) {
		this(dispatcher, true);
	}

	public SocialVillagerRenderer(EntityRenderDispatcher dispatcher, boolean thinArms) {
		super(dispatcher, new PlayerEntityModel<>(0.0F, thinArms), 0.5F);
	}

	@Override
	protected Identifier getTexture(SocialVillager entity) {
		if (this.getRenderManager().textureManager.getTexture(new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(SocialVillager.serverUUID) + "_1")) != null) {
			return new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(SocialVillager.serverUUID) + "_1");
		}
		SocialVillagerData socialVillagerData = entity.getSocialVillagerData();
		BufferedImage imageBase = new TextureAssembler(socialVillagerData.getVillagerAspects(), socialVillagerData.getVillagerGender(), socialVillagerData.getVillagerProfession())
				.createTexture();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ImageIO.write(imageBase, "png", stream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStream is = new ByteArrayInputStream(stream.toByteArray());
		NativeImage base = null;
		try {
			base = NativeImage.fromInputStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NativeImageBackedTexture texture = new NativeImageBackedTexture(base);
		return this.getRenderManager().textureManager.registerDynamicTexture(entity.getDataTracker().get(SocialVillager.serverUUID), texture);
	}

	@Override
	protected void scale(SocialVillager livingEntity_1, float float_1) {
		float float_2 = 0.9375F;
		if (livingEntity_1.isBaby()) {
			float_2 = (float) ((double) float_2 * 0.5D);
			this.field_4673 = 0.25F;
		} else {
			this.field_4673 = 0.5F;
		}

		GlStateManager.scalef(float_2, float_2, float_2);
	}

}
