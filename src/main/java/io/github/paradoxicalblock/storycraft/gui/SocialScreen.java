package io.github.paradoxicalblock.storycraft.gui;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");

	private SocialVillager target;
	private PlayerEntity talker;

	public SocialScreen(SocialVillager entity, PlayerEntity player) {
		super(new TranslatableText("narrator.screen.title"));
		this.target = entity;
		this.talker = player;
	}

	@Override
	public void render(int int_1, int int_2, float float_1) {
		this.renderBackground();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(102, 44, this.blitOffset, 0.0F, 0.0F, 275, 166, 256, 512);

		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(107, 62, this.blitOffset, 275.0F, 0.0F, 88, 80, 256, 512);

		/*String quest = "Quests";
		this.font.draw(quest, 100 + this.font.getStringWidth(quest), 51, 4210752);

		String questName = "Quest: Carrot Collector";
		this.font.draw(questName, 110 + this.font.getStringWidth(questName), 51, 4210752);

		String newLine = "\n";

		String desc = " help me!" + newLine + " My rabbits are hungry," + newLine + " But I don't have any carrots!" + newLine + " Can you help me?" + newLine + newLine + " Collect 25 carrots." + newLine + " Reward:";
		drawWrappedString(desc, 210, 71, 153, 4210752);*/

		String name = String.format("%s %s", this.target.firstName, this.target.lastName);
		String profession = String.format("%s - %s", name, this.target.getVillagerProfession().getProfession());
		this.font.draw(profession, 211, 51, 4210752);

		QuestManager.getQuests().forEach(quests -> {
			for(Quest quest : quests) {
				if(this.target.getVillagerProfession().getProfession().equals(quest.getProfession())) {
					this.font.draw(quest.name.getPath(), 110 + this.font.getStringWidth(quest.name.getPath()), 70, 4210752);
				}
			}
		});

		/*String name = String.format("%s %s", this.target.firstName, this.target.lastName);
		TextFieldWidget nameText = new TextFieldWidget(this.font, 100, 50, this.font.getStringWidth(name) + 10, 15, name);
		nameText.setText(name);
		nameText.setIsEditable(false);
		nameText.setUneditableColor(0xFFFFFF);
		nameText.setEditableColor(0xFFFFFF);
		nameText.render(int_1, int_2, float_1);*/
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