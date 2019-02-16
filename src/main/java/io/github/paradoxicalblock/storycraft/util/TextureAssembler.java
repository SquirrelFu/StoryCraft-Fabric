package io.github.paradoxicalblock.storycraft.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TextureAssembler {
	private String eyecolor;
	private String haircolor;
	private String skincolor;
	private Integer hairstyle;
	private boolean gender;
	private BufferedImage totalImage;
	//Outfits
	private Identifier outfit1f = new Identifier("storycraft","textures/entity/skincomponents/female/outfit1.png");
	private Identifier outfit1m = new Identifier("storycraft","textures/entity/skincomponents/male/outfit1.png");
	//Skin
	private Identifier lightSkinm = new Identifier("storycraft","textures/entity/skincomponents/male/lightskinbase.png");
	private Identifier medSkinm = new Identifier("storycraft","textures/entity/skincomponents/male/medskinbase.png");
	private Identifier darkSkinm = new Identifier("storycraft","textures/entity/skincomponents/male/darkskinbase.png");
	private Identifier lightSkinf = new Identifier("storycraft","textures/entity/skincomponents/female/lightskinbase.png");
	private Identifier mediumSkinf = new Identifier("storycraft","textures/entity/skincomponents/female/medskinbase.png");
	private Identifier darkSkinf = new Identifier("storycraft","textures/entity/skincomponents/female/darkskinbase.png");
	//Eyes
	private Identifier blueeyesf = new Identifier("storycraft","textures/entity/skincomponents/female/eyesblue.png");
	private Identifier greeneyesf = new Identifier("storycraft","textures/entity/skincomponents/female/eyesgreen.png");
	private Identifier browneyesf = new Identifier("storycraft","textures/entity/skincomponents/female/eyesbrown.png");
	private Identifier blueeyesm = new Identifier("storycraft","textures/entity/skincomponents/male/eyesblue.png");
	private Identifier browneyesm = new Identifier("storycraft","textures/entity/skincomponents/male/eyesbrown.png");
	private Identifier greeneyesm = new Identifier("storycraft","textures/entity/skincomponents/male/eyesgreen.png");
	//Hair male
	private Identifier blackhairm = new Identifier("storycraft","textures/entity/skincomponents/male/hairblack.png");
	private Identifier blackhairm2 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblack2.png");
	private Identifier blackhairm3 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblack3.png");
	private Identifier blackhairm4 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblack4.png");
	private Identifier brownhairm = new Identifier("storycraft","textures/entity/skincomponents/male/hairbrown.png");
	private Identifier brownhairm2 = new Identifier("storycraft","textures/entity/skincomponents/male/hairbrown2.png");
	private Identifier brownhairm3 = new Identifier("storycraft","textures/entity/skincomponents/male/hairbrown3.png");
	private Identifier brownhairm4 = new Identifier("storycraft","textures/entity/skincomponents/male/hairbrown4.png");
	private Identifier blondehairm = new Identifier("storycraft","textures/entity/skincomponents/male/hairblonde.png");
	private Identifier blondehairm2 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblonde2.png");
	private Identifier blondehairm3 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblonde3.png");
	private Identifier blondehairm4 = new Identifier("storycraft","textures/entity/skincomponents/male/hairblonde4.png");
	private Identifier redhairm = new Identifier("storycraft","textures/entity/skincomponents/male/hairred.png");
	private Identifier redhairm2 = new Identifier("storycraft","textures/entity/skincomponents/male/hairred2.png");
	private Identifier redhairm3 = new Identifier("storycraft","textures/entity/skincomponents/male/hairred3.png");
	private Identifier redhairm4 = new Identifier("storycraft","textures/entity/skincomponents/male/hairred4.png");
	//Hair female
	private Identifier blondehairf = new Identifier("storycraft","textures/entity/skincomponents/female/hairblonde1.png");
	private Identifier blondehairf2 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblonde2.png");
	private Identifier blondehairf3 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblonde3.png");
	private Identifier blondehairf4 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblonde4.png");
	private Identifier brownhairf = new Identifier("storycraft","textures/entity/skincomponents/female/hairbrown1.png");
	private Identifier brownhairf2 = new Identifier("storycraft","textures/entity/skincomponents/female/hairbrown2.png");
	private Identifier brownhairf3 = new Identifier("storycraft","textures/entity/skincomponents/female/hairbrown3.png");
	private Identifier brownhairf4 = new Identifier("storycraft","textures/entity/skincomponents/female/hairbrown4.png");
	private Identifier blackhairf = new Identifier("storycraft","textures/entity/skincomponents/female/hairblack1.png");
	private Identifier blackhairf2 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblack2.png");
	private Identifier blackhairf3 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblack3.png");
	private Identifier blackhairf4 = new Identifier("storycraft","textures/entity/skincomponents/female/hairblack4.png");
	private Identifier redhairf1 = new Identifier("storycraft","textures/entity/skincomponents/female/hairred1.png");
	private Identifier redhairf2 = new Identifier("storycraft","textures/entity/skincomponents/female/hairred2.png");
	private Identifier redhairf3 = new Identifier("storycraft","textures/entity/skincomponents/female/hairred3.png");
	private Identifier redhairf4 = new Identifier("storycraft","textures/entity/skincomponents/female/hairred4.png");
	public TextureAssembler(String eyecolor, String haircolor, String skincolor, Integer hairstyle, boolean gender)
    {
        this.eyecolor = eyecolor;
        this.haircolor = haircolor;
        this.skincolor = skincolor;
        this.hairstyle = hairstyle;
        //True is male, false is female.
        this.gender = gender;
    }

    public BufferedImage createTexture()
    {
    	totalImage = null;
        BufferedImage hairimage = null;
        BufferedImage eyeimage = null;
        BufferedImage skinimage = null;
        BufferedImage outfitimage = null;
        InputStream inputstream = null;
        
        try
        {
        	if (gender)
        	{
        		inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit1m).getInputStream();
        		outfitimage = ImageIO.read(inputstream);
        		inputstream.close();
        		if (this.skincolor.equals("Light"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(lightSkinm).getInputStream();
        		}
        		else if(this.skincolor.equals("Medium"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(medSkinm).getInputStream();
        		}
        		else if(this.skincolor.equals("Dark"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(darkSkinm).getInputStream();
        		}
        		skinimage = ImageIO.read(inputstream);
        		inputstream.close();
        		if (haircolor.equals("Black"))
        		{
        			if(hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairm).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairm2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairm3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairm4).getInputStream();
        			}
        		}
        		else if(haircolor.equals("Blonde"))
        		{
        			if(hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairm).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairm2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairm3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairm4).getInputStream();
        			}
        		}
        		else if(haircolor.equals("Brown"))
        		{
        			if(hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairm).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairm2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairm3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairm4).getInputStream();
        			}
        		}
        		else if(haircolor.equals("Red"))
        		{
        			if(hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairm).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairm2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairm3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairm4).getInputStream();
        			}
        		}
        		hairimage = ImageIO.read(inputstream);
        		inputstream.close();
        		if (eyecolor.equals("Blue"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blueeyesm).getInputStream();
        		}
        		else if(eyecolor.equals("Brown"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(browneyesm).getInputStream();
        		}
        		else if(eyecolor.equals("Green"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(greeneyesm).getInputStream();
        		}
        		eyeimage = ImageIO.read(inputstream);
        		inputstream.close();
        	}
        	else
        	{
        		inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit1f).getInputStream();
        		outfitimage = ImageIO.read(inputstream);
        		if (this.skincolor.equals("Light"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(lightSkinf).getInputStream();
        		}
        		else if (this.skincolor.equals("Medium"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(mediumSkinf).getInputStream();
        		}
        		else if (this.skincolor.equals("Dark"))
        		{
        			inputstream = MinecraftClient.getInstance().getResourceManager().getResource(darkSkinf).getInputStream();
        		}
        		skinimage = ImageIO.read(inputstream);
        		inputstream.close();
        		if (this.haircolor.equals("Black"))
        		{
        			if (hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairf).getInputStream();
        			}
        			else if (hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairf2).getInputStream();
        			}
        			else if (hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairf3).getInputStream();
        			}
        			else if (hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackhairf4).getInputStream();
        			}
        				
        		}
        		else if (this.haircolor.equals("Blonde"))
        		{
        			if (hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairf).getInputStream();
        			}
        			else if (hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairf2).getInputStream();
        			}
        			else if (hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairf3).getInputStream();
        			}
        			else if (hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondehairf4).getInputStream();
        			}
        			
        		}
        		else if (this.haircolor.equals("Brown"))
        		{
        			if (hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairf).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairf2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairf3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownhairf4).getInputStream();
        			}
        		}
        		else if(this.haircolor.equals("Red"))
        		{
        			if(hairstyle.equals(0))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairf1).getInputStream();
        			}
        			else if(hairstyle.equals(1))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairf2).getInputStream();
        			}
        			else if(hairstyle.equals(2))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairf3).getInputStream();
        			}
        			else if(hairstyle.equals(3))
        			{
        				inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redhairf4).getInputStream();
        			}
        		}
        		hairimage = ImageIO.read(inputstream);
        		inputstream.close();
    	        if (this.eyecolor.equals("Blue"))
    	        {
    	        	inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blueeyesf).getInputStream();
    	        }
    	        if (this.eyecolor.equals("Brown"))
    	        {
    	        	inputstream = MinecraftClient.getInstance().getResourceManager().getResource(browneyesf).getInputStream();
    	        }
    	        if (this.eyecolor.equals("Green"))
    	        {
    	        	inputstream = MinecraftClient.getInstance().getResourceManager().getResource(greeneyesf).getInputStream();
    	        }
    	        eyeimage = ImageIO.read(inputstream);
    	        inputstream.close();
        	}
        	totalImage = new BufferedImage(skinimage.getWidth(), skinimage.getHeight(), 2);
        	Graphics2D g = totalImage.createGraphics();	
        	g.drawImage(skinimage, 0, 0, null);
        	g.drawImage(eyeimage, 0, 0, null);
        	g.drawImage(outfitimage, 0, 0, null);
        	g.drawImage(hairimage, 0, 0, null);
        	g.dispose();
        	
        	return totalImage;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	}
        return null;
    }
    
}