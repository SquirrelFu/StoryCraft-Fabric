package io.github.paradoxicalblock.storycraft.gui;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");

	private SocialVillager target;
	private PlayerEntity talker;

	public SocialScreen(SocialVillager entity, PlayerEntity player) {
		super(new TranslatableComponent("narrator.screen.title"));
		this.target = entity;
		this.talker = player;
	}

	@Override
	public void render(int int_1, int int_2, float float_1) {
		this.renderBackground();
		super.render(int_1, int_2, float_1);
		Objects.requireNonNull(this.minecraft).getTextureManager().bindTexture(TEXTURE);
		int int_3 = (this.width - 276) / 2;
		int int_4 = (this.height - 166) / 2;
		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(int_3, int_4, this.blitOffset, 0.0F, 0.0F, 275, 166, 256, 512);

		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(107, 62, this.blitOffset, 275.0F, 0.0F, 88, 80, 256, 512);

		String quest = "Quests";
		this.font.draw(quest, 100 + this.font.getStringWidth(quest), 51, 4210752);

		String questName = "Quest: Carrot Collector";
		this.font.draw(questName, 110 + this.font.getStringWidth(questName), 51, 4210752);

		String newLine = "\n";

		String desc = " help me!" + newLine + " My rabbits are hungry," + newLine + " But I don't have any carrots!" + newLine + " Can you help me?" + newLine + newLine + " Collect 25 carrots." + newLine + " Reward:";
		drawWrappedString(desc, 210, 71, 153, 4210752);
	}

	public void drawWrappedString(String text, int x, int y, int entryWidth, int color) {
		List<String> strings = font.wrapStringToWidthAsList(text, entryWidth);
		for (String string : strings) {
			font.draw(string, x, y, color);
			y += font.fontHeight + 3;
		}
	}

	public SocialVillager getTarget() {
		return this.target;
	}

}