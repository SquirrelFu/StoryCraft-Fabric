package io.github.paradoxicalblock.storycraft.gui;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");

	private SocialVillager target;
	private PlayerEntity talker;

	private SocialVillagerQuestButton[] questButtons;
	private Quest[] quests;

	public SocialScreen(SocialVillager entity, PlayerEntity player) {
		super(new TranslatableText("narrator.screen.title"));
		this.target = entity;
		this.talker = player;

		quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getVillagerProfession().getProfession())).toArray(Quest[]::new);
		questButtons = new SocialVillagerQuestButton[7];

		for(int i = 0; i < 7; i++) {
			questButtons[i] = new SocialVillagerQuestButton(107, 62 + i * 20, i < quests.length ? quests[i] : null);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(102, 44, this.blitOffset, 0.0F, 0.0F, 275, 166, 256, 512);

		//blit(x, y, z, u, v, width, height, texHeight, texWidth)
		blit(107, 62, this.blitOffset, 275.0F, 0.0F, 88, 80, 256, 512);

		String name = String.format("%s %s", this.target.firstName, this.target.lastName);
		String namePlusProfession = String.format("%s - %s", name, this.target.getVillagerProfession().getProfession());
		this.font.draw(namePlusProfession, 211, 51, 4210752);

		String questTitle = "Quests";
		this.font.draw(questTitle, 100 + this.font.getStringWidth(questTitle), 51, 4210752);

		for(SocialVillagerQuestButton questButton : questButtons) {
			questButton.render(mouseX, mouseY, delta);

			if (questButton.quest != null) {
				String questName = String.format("Quest: %s", questButton.quest.getTask().getName());

				this.font.draw(questName, 130 + this.font.getStringWidth(questName), 61, 4210752);

				drawWrappedString(questButton.quest.getTask().getDescription(), 210, 71, 153, 4210752);
			} else if (questButton.quest == null) {
				String noQuests = "This villager has no quests";
				this.font.draw(noQuests, 205, 101,4210752 );
			} else {

			}
		}

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
		private Quest quest;

		SocialVillagerQuestButton(int x, int y, Quest quest) {
			super(x, y, 89, 20, quest != null ? quest.name.getPath() : "", ButtonWidget::onPress);
            setQuest(quest);
		}

		void setQuest(Quest quest) {
			this.quest = quest;
			visible = quest != null;
			setMessage(quest != null ? quest.name.getPath() : "");
		}

		@Override
		public void onPress() {
			super.onPress();
			System.out.println(quest.name);
		}

	}

}