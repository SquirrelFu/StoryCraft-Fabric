package io.github.paradoxicalblock.storycraft.gui;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import io.github.paradoxicalblock.storycraft.gui.widget.SocialVillagerQuestButton;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class FamiliarsScreen extends LightweightGuiDescription {

    private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");
    private PlayerEntity talker;
    private FamiliarsEntity target;

    public WPlainPanel panel = new WPlainPanel();

    public FamiliarsScreen(FamiliarsEntity entity, PlayerEntity player) {
        this.talker = player;
        this.target = entity;

        this.setRootPanel(panel);
        panel.setSize(275, 166);

        WLabel questsTitle = new WLabel("Quests");
        questsTitle.setColor(0x000000, 0xFFFFFF);
        panel.add(questsTitle, 1 + questsTitle.getWidth(), 6);

        String name = String.format("%s %s", this.target.firstName, this.target.lastName);
        WLabel namePlusProfessionWidget = new WLabel(String.format("%s - %s", name, this.target.getFamiliarsProfession().getProfession()));
        namePlusProfessionWidget.setColor(0x000000, 0xFFFFFF);
        panel.add(namePlusProfessionWidget, 101 + namePlusProfessionWidget.getWidth(), 7);

        Quest[] quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getFamiliarsProfession().getProfession())).toArray(Quest[]::new);
        for (Quest quest : quests) {
            SocialVillagerQuestButton questButton = new SocialVillagerQuestButton(quest);
            panel.add(questButton, 10 + 2, 10);
        }

        /*WLabel tasksTitle = new WLabel("Tasks");
        tasksTitle.setColor(0x000000, 0xFFFFFF);
        panel.add(tasksTitle, 1 + tasksTitle.getWidth(), 6);*/
    }

    @Override
    public void addPainters() {
        panel.setBackgroundPainter((left, top, panel) -> {
            if (LibGuiClient.config.darkMode) {
                ScreenDrawing.rect(new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager_dark.png"), 102, 44, 275, 166,
                        0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);
            } else {
                ScreenDrawing.rect(new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png"), 102, 44, 275, 166,
                        0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);
            }
        });
//        panel.setBackgroundPainter(BackgroundPainter.VANILLA);
    }

}