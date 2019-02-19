package io.github.paradoxicalblock.storycraft.gui;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SocialButton extends ButtonWidget {
	public Identifier texture = new Identifier ("storycraft","textures/gui/speechbuttonsheet.png");
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
			SocialVillager target = screen.getTarget();
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
			SocialVillager target = screen.getTarget();
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
			SocialVillager target = screen.getTarget();
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
	

	public SocialButton(int id, int x, int y, int sizex, int sizey, String name) {
		super(id, x, y, sizex, sizey, name);
	}
	@Override
	public void draw(int mouseX, int mouseY, float float_1) {
		SocialScreen screen = (SocialScreen) MinecraftClient.getInstance().currentScreen;
		MinecraftClient minecraftClient_1 = MinecraftClient.getInstance();
		minecraftClient_1.getTextureManager().bindTexture(texture);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(this.isHovered())
		{
			if(this.getText() != "Socialize" && this.getText() != "Influence")
			{
				if (this.getText() != "Examine" && this.getText() != "Barter")
				{
					drawTexturedRect(x,y,122,31,width,height);
				}
				else
				{
					drawTexturedRect(x,y,59,31,width,height);
				}
				
			}
			else if(this.getText() == "Socialize")
			{
				if (!screen.positiveButtons.get(0).visible)
				{
					drawTexturedRect(x,y,0,31,width,height);
					
				}
				else
				{
					drawTexturedRect(x,y,0,62,width,height);
				}
			}
			else if(this.getText() == "Influence")
			{
				if (!screen.neutralButtons.get(0).visible)
				{
					drawTexturedRect(x,y,0,31,width,height);
				}
				else
				{
					drawTexturedRect(x,y,0,62,width,height);
				}
				
			}
			
		}
		else if(this.getText() == "Socialize")
		{
			if (screen.positiveButtons.get(0).visible)
			{
				drawTexturedRect(x,y,0,62,width,height);
			}
			else
			{
				drawTexturedRect(x,y,0,0,width,height);
			}
			
		}
		else if(this.getText() == "Influence")
		{
			if (screen.neutralButtons.get(0).visible)
			{
				drawTexturedRect(x,y,0,62,width,height);
			}
			else
			{
				drawTexturedRect(x,y,0,0,width,height);
			}
		}
		else if (this.getText() != "Examine" && this.getText() != "Barter")
		{
			drawTexturedRect(x,y,122,0,width,height);
		}
		else
		{
			drawTexturedRect(x,y,59,0,width,height);
		}
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer fr = client.textRenderer;
		drawStringCentered(fr, "§0" + this.getText(), x + width/2, y + (this.height - 16)/2, 0);
	}
	@Override
	public void drawStringCentered(TextRenderer textRenderer_1, String string_1, int int_1, int int_2, int int_3) {
	      textRenderer_1.draw(string_1, (float)(int_1 - textRenderer_1.getStringWidth(string_1) / 2), (float)int_2, int_3);
	}
}
