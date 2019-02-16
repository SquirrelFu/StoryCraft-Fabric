package io.github.paradoxicalblock.storycraft.gui;

import java.util.List;
import java.util.Random;

import io.github.paradoxicalblock.storycraft.entity.SocialVillagerBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class SocialButton extends ButtonWidget {

	public SocialButton(int int_1, int int_2, int int_3, int int_4, int int_5, String string_1) {
		super(int_1, int_2, int_3, int_4, int_5, string_1);
	}
	public void onPressed(double d1, double d2)
	{
		SocialScreen screen = (SocialScreen) MinecraftClient.getInstance().currentScreen;
		if (this.getText() == "Socialize")
		{
			
			List<SocialButton> buttons = screen.positiveButtons;
			List<SocialButton> otherButtons = screen.neutralButtons;
			if (buttons.get(0).visible == true)
			{
				for (SocialButton button : buttons)
				{
					button.visible = false;
				}
			}
			else
			{
				for (SocialButton button : buttons)
				{
					button.visible = true;
				}
			}
			for (SocialButton button : otherButtons)
			{
				button.visible = false;
			}
		}
		else if (this.getText() == "Influence")
		{
			List<SocialButton> buttons = screen.neutralButtons;
			List<SocialButton> otherButtons = screen.positiveButtons;
			if (buttons.get(0).visible == true)
			{
				for (SocialButton button : buttons)
				{
					button.visible = false;
				}
			}
			else
			{
				for (SocialButton button : buttons)
				{
					button.visible = true;
				}
			}
			for (SocialButton button : otherButtons)
			{
				button.visible = false;
			}
		}
		else if (this.getText() == "Charm")
		{
			SocialVillagerBase target = screen.getTarget();
			PlayerEntity talker = screen.getTalker();
			if (target.getCharmed())
			{
				talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.charmrepeat")),false);
				return;
			}
			
			int friendliness = target.getFriendliness();
			float threshold = 0.66F + (float) (friendliness/400);
			Random rand = new Random();
			if (rand.nextFloat() < threshold)
			{
				talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.charmsuccess")), false);
			}
			else
			{
				talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.charmfailure")),false);
			}
			target.setCharmed();
			int opinion = target.getOpinion(talker.getUuid());
			target.setOpinion(talker.getUuid(), opinion+5);
			return;
		}
		else if (this.getText() == "Apologize")
		{
			SocialVillagerBase target = screen.getTarget();
			PlayerEntity talker = screen.getTalker();
			int opinion = target.getOpinion(talker.getUuid());
			if (opinion >= 0)
			{
				talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyunnecessary")),false);
				return;
			}
			if (target.getApologized())
			{
				
				if (opinion < 0 && opinion > -25)
				{
					talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyrepeat1")),false);
				}
				else if(opinion <= -25 && opinion > -50)
				{
					talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyrepeat2")),false);
				}
				else if(opinion <= -50 && opinion > -75)
				{
					talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyrepeat3")),false);
				}
				else if(opinion <= -75 && opinion > -100)
				{
					talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyrepeat4")),false);
				}
				else if(opinion == -100)
				{
					talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyrepeat5")),false);
				}
				return;
			}
			else
			{
				Random rand = new Random();
				float threshold = 0.66F + (float) opinion/400;
				if (rand.nextFloat() <= threshold)
				{
					if (opinion < 0 && opinion > -25)
					{
						talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyaccepted1")),false);
					}
					else if(opinion <= -25 && opinion > -50)
					{
						talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyaccepted2")),false);
					}
					else if(opinion <= -50 && opinion > -75)
					{
						talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyaccepted3")),false);
					}
					else if(opinion <= -75 && opinion > -100)
					{
						talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyaccepted4")),false);
					}
					else if(opinion == -100)
					{
						talker.addChatMessage(new StringTextComponent("<" + target.firstName + " " + target.lastName + ">: ").append(new TranslatableTextComponent("text.social.apologyaccepted5")),false);
					}
					target.setOpinion(talker.getUuid(),opinion+10);
					target.setApologized();
				}
			}
		}
		else if(this.getText() == "Examine")
		{
			SocialVillagerBase target = screen.getTarget();
			PlayerEntity talker = screen.getTalker();
			int opinion = target.getOpinion(talker.getUuid());
			if (opinion >= 0 && opinion < 25)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine1")),false);
			}
			else if (opinion >= 25 && opinion < 50)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine2")),false);
			}
			else if (opinion >= 50 && opinion < 75)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine3")),false);
			}
			else if (opinion >= 75 && opinion <= 100)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine4")),false);
			}
			else if (opinion < 0 && opinion > -25)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine5")),false);
			}
			else if (opinion <= -25 && opinion > -50)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine6")),false);
			}
			else if (opinion <= -50 && opinion > -75)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine7")),false);
			}
			else if (opinion <= -75 && opinion >= -100)
			{
				talker.addChatMessage(new StringTextComponent(target.firstName + " " + target.lastName).append(new TranslatableTextComponent("text.social.examine8")),false);
			}
		}
	}

}
