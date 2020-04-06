package io.github.paradoxicalblock.storycraft.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.questing_api.api.QuestTask;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");
    private static PlayerEntity talker;
    private FamiliarsEntity target;
    private SocialVillagerQuestButton[] questButtons;
    private ButtonWidget getRewardButton;

    private Map<HoverChecker, String> hoverChecks = new HashMap<>();

    public SocialScreen(FamiliarsEntity entity, PlayerEntity player) {
        super(new TranslatableText("narrator.screen.title"));
        this.target = entity;
        talker = player;

        Quest[] quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getFamiliarsProfession().getProfession())).toArray(Quest[]::new);
        questButtons = new SocialVillagerQuestButton[7];

        for (int i = 0; i < 7; i++) {
            questButtons[i] = new SocialVillagerQuestButton(107, 62 + i * 20, i < quests.length ? quests[i] : null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        super.init(minecraftClient_1, int_1, int_2);
        for (SocialVillagerQuestButton questButton : questButtons) {
            this.addButton(questButton);

            getRewardButton = new ButtonWidget(300, 130, 70, 20, "Get Reward", var1 -> {
                System.out.println(Registry.ITEM.getId(questButton.quest.getReward().getItemReward().getItem()));
                talker.dropItem(questButton.quest.getReward().getItemReward().getItem(), 10);
                talker.addChatMessage(new LiteralText("Testing"), false);
            });
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        ScreenDrawing.texturedRect(102, 44, 275, 166, TEXTURE, 0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);

        //blit(x, y, z, u, v, width, height, texHeight, texWidth)
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(107, 62, this.getBlitOffset(), 275.0F, 0.0F, 88, 80, 256, 512);

        String name = String.format("%s %s", this.target.firstName, this.target.lastName);
        String namePlusProfession = String.format("%s - %s", name, this.target.getFamiliarsProfession().getProfession());
        this.font.draw(namePlusProfession, 211, 51, 4210752);

        String questTitle = "Quests";
        this.font.draw(questTitle, 100 + this.font.getStringWidth(questTitle), 51, 4210752);

        for (SocialVillagerQuestButton questButton : questButtons) {
            questButton.render(mouseX, mouseY, delta);
            if (questButton.getQuest() != null) {
                for(QuestTask task : questButton.getQuest().getTasks()) {
                    for (int i = 0; i < questButton.getQuest().getTasks().length; i++) {
                        String questName = String.format("Quest: %s", task.getName());
                        this.font.draw(questName, 140 + this.font.getStringWidth(questName), 65 + i, 4210752);

                        drawWrappedString(task.getDescription(), 210, 80 + i, 153, 4210752);

                        i += 50;
                    }
                }
                getRewardButton.render(mouseX, mouseY, delta);
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

    public FamiliarsEntity getTarget() {
        return this.target;
    }

    public static class SocialVillagerQuestButton extends ButtonWidget {
        private Quest quest;

        SocialVillagerQuestButton(int x, int y, Quest quest) {
            super(x, y, 89, 20, quest != null ? quest.getTask().getName() : "", ButtonWidget::onPress);
            setQuest(quest);
        }

        @Override
        public void onPress() {
            System.out.println(quest.getTask().getName());


        }

        public Quest getQuest() {
            return quest;
        }

        void setQuest(Quest quest) {
            this.quest = quest;
            visible = quest != null;
            setMessage(quest != null ? quest.getTask().getName() : "");
        }

    }

}