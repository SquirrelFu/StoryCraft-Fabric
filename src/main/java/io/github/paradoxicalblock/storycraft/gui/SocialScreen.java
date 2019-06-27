package io.github.paradoxicalblock.storycraft.gui;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
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

		String name = String.format("%s %s", this.target.firstName, this.target.lastName);
		String namePlusProfession = String.format("%s - %s", name, this.target.getVillagerProfession().getProfession());
		this.font.draw(namePlusProfession, 211, 51, 4210752);

		String profession = this.target.getVillagerProfession().getProfession();

		ElementListWidget elementListWidget = new ElementListWidget(this.minecraft, 89, 100, 70, 170, 20){};

		QuestManager.getQuests().forEach(quests -> {
			for(Quest quest : quests) {
				if(quest.profession.equals(profession)) {
					int i = 0;
					SocialVillagerQuestButton socialVillagerQuestButton = new SocialVillagerQuestButton(110, 60 + i, quest.name.getPath());
					socialVillagerQuestButton.render(10, 10, 10);
//					this.font.draw(quest.name.getPath(), 110 + this.font.getStringWidth(quest.name.getPath()), 70, 4210752);
					i += 15;
				}
			}
		});

		elementListWidget.render(10, 10, 10);
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

	public static class SocialVillagerQuestButton extends ButtonWidget {

		public SocialVillagerQuestButton(int x, int y, String text) {
			super(x, y, 89, 20, text, ButtonWidget::onPress);
		}

		@Override
		public void onPress() {
			super.onPress();
		}

	}

}