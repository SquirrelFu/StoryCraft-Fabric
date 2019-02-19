package io.github.paradoxicalblock.storycraft.gui;

import java.util.ArrayList;
import java.util.List;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {

	private SocialVillager target;
	private PlayerEntity talker;
	private SocialButton charmButton;
	private SocialButton apologyButton;
	private SocialButton examineButton;
	private SocialButton recruitButton;
	private SocialButton favorButton;
	private SocialButton tradeButton;
	public List<SocialButton> positiveButtons = new ArrayList<SocialButton>();
	public List<SocialButton> neutralButtons = new ArrayList<SocialButton>();
	public SocialScreen(SocialVillager entity, PlayerEntity player)
	{
		super();
		this.target = entity;
		this.talker = player;
		
	}
	public void initialize(MinecraftClient client, int int1, int int2)
	{
		super.initialize(client, int1, int2);
		this.addButton(new SocialButton(1, (width - 60), (height - 90), 60, 30, "Socialize"));
		this.addButton(new SocialButton(3, (width - 60), (height - 60), 60, 30, "Influence"));
		this.addButton(charmButton = new SocialButton(4, (width - 120), (height - 90), 60, 30, "Charm"));
		charmButton.visible = false;
		positiveButtons.add(charmButton);
		this.addButton(apologyButton = new SocialButton(5, (width - 180), (height - 90), 60, 30, "Apologize"));
		apologyButton.visible = false;
		positiveButtons.add(apologyButton);
		this.addButton(examineButton = new SocialButton(6, (width - 240), (height - 90), 60, 30, "Examine"));
		examineButton.visible = false;
		positiveButtons.add(examineButton);
		this.addButton(recruitButton = new SocialButton(7, (width - 120), (height - 60), 60, 30, "Recruit"));
		recruitButton.visible = false;
		neutralButtons.add(recruitButton);
		this.addButton(favorButton = new SocialButton(8, (width - 180), (height - 60), 60, 30, "Favor"));
		favorButton.visible = false;
		neutralButtons.add(favorButton);
		this.addButton(tradeButton = new SocialButton(9, (width - 240), (height - 60), 60, 30, "Barter"));
		tradeButton.visible = false;
		neutralButtons.add(tradeButton);
		
	}
	public SocialVillager getTarget()
	{
		return this.target;
	}
	public PlayerEntity getTalker()
	{
		return this.talker;
	}
}
