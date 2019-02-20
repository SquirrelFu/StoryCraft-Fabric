package io.github.paradoxicalblock.storycraft.entity;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import io.github.paradoxicalblock.storycraft.util.TextureAssembler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry.Factory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SocialVillagerRenderer extends MobEntityRenderer {
	public static final RenderFactory factory = new RenderFactory();
	public SocialVillagerRenderer(EntityRenderDispatcher dispatcher)
	{
		super(dispatcher, new PlayerEntityModel(0.0F, false), 0.5F);
	}
	public SocialVillagerRenderer(EntityRenderDispatcher entityRenderDispatcher_1, EntityModel entityModel_1,
			float float_1) {
		super(entityRenderDispatcher_1, entityModel_1, float_1);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Identifier getTexture(Entity entity) {
		if (this.getRenderManager().textureManager.getTexture(new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(SocialVillager.serverUUID) + "_1")) != null)
		{
			return new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(SocialVillager.serverUUID) + "_1");
		}
		SocialVillager entityIn = (SocialVillager) entity;
		boolean genderVar = entityIn.getDataTracker().get(SocialVillager.genderUnified);
		this.model = new PlayerEntityModel(0.0F, !genderVar);
		String hairColor = entityIn.getDataTracker().get(SocialVillager.hairColorUnified);
		String eyeColor = entityIn.getDataTracker().get(SocialVillager.eyeColorUnified);
		String skinColor = entityIn.getDataTracker().get(SocialVillager.skinColorUnified);
		int hairStyle = entityIn.getDataTracker().get(SocialVillager.hairStyleUnified);
		BufferedImage imageBase = new TextureAssembler(eyeColor,hairColor,skinColor, hairStyle, genderVar).createTexture();
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
	public static class RenderFactory implements Factory {
		
			@Override
			public EntityRenderer<? extends Entity> create(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
				return new SocialVillagerRenderer(manager);
			}

	}



}
