package io.github.paradoxicalblock.storycraft.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.paradoxicalblock.storycraft.util.TextureAssembler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Environment(value = EnvType.CLIENT)
public class FamiliarsEntityRenderer extends MobEntityRenderer<FamiliarsEntity, PlayerEntityModel<FamiliarsEntity>> {
    public FamiliarsEntityRenderer(EntityRenderDispatcher dispatcher, boolean thinArms) {
        super(dispatcher, new PlayerEntityModel<>(0.0F, thinArms), 0.5F);
    }

    @Override
    public Identifier getTexture(FamiliarsEntity entity) {
        if (this.getRenderManager().textureManager.getTexture(new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(FamiliarsEntity.serverUUID) + "_1")) != null) {
            return new Identifier("minecraft:dynamic/" + entity.getDataTracker().get(FamiliarsEntity.serverUUID) + "_1");
        }
        TextureAssembler assembler = new TextureAssembler(entity);
        BufferedImage imageBase = assembler.createTexture();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(imageBase, "png", stream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        NativeImage base = null;
        try {
            base = NativeImage.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NativeImageBackedTexture texture = new NativeImageBackedTexture(base);
        assembler.save();
        return this.getRenderManager().textureManager.registerDynamicTexture(entity.getDataTracker().get(FamiliarsEntity.serverUUID), texture);
    }

    @Override
    protected void scale(FamiliarsEntity entity, MatrixStack matrices, float tickDelta) {
        float float_2 = 0.9375F;
        if (entity.isBaby()) {
            float_2 = (float) ((double) float_2 * 0.5D);
            this.shadowSize = 0.25F;
        } else {
            this.shadowSize = 0.5F;
        }

        GlStateManager.scalef(float_2, float_2, float_2);
    }

}
