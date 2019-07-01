package io.github.paradoxicalblock.storycraft.util;

import io.github.paradoxicalblock.storycraft.entity.SocialVillager;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

@Environment(value = EnvType.CLIENT)
public class TextureAssembler {

	private String eyeColor;
	private String hairColor;
	private String skinColor;
	private Integer hairstyle;
	private String gender;
	private String profession;

	private String SOCIAL_VILLAGER_LOCATION = "textures/entity/social_villagers";
	private String PROFESSION_LOCATION = SOCIAL_VILLAGER_LOCATION + "/profession";
	private String FEMALE_LOCATION = SOCIAL_VILLAGER_LOCATION + "/female";
	private String MALE_LOCATION = SOCIAL_VILLAGER_LOCATION + "/male";

	//Outfits
	private Identifier outfit1Female = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/clothes/outfit1.png");
	private Identifier outfit2Female = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/clothes/outfit2.png");
	private Identifier outfit3Female = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/clothes/outfit3.png");

	private Identifier outfit1Male = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/clothes/outfit1.png");
	private Identifier outfit2Male = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/clothes/outfit2.png");
	private Identifier outfit3Male = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/clothes/outfit3.png");

	//Skin
	private Identifier lightSkinMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/skin/light.png");
	private Identifier medSkinMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/skin/medium.png");
	private Identifier darkSkinMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/skin/dark.png");
	private Identifier lightSkinFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/skin/light.png");
	private Identifier mediumSkinFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/skin/medium.png");
	private Identifier darkSkinFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/skin/dark.png");

	//Eyes
	private Identifier blackEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/black.png");
	private Identifier blueEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/blue.png");
	private Identifier brownEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/brown.png");
	private Identifier greenEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/green.png");
	private Identifier limeEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/lime.png");
	private Identifier pinkEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/pink.png");
	private Identifier yellowEyes = new Identifier(StoryCraft.MOD_ID, SOCIAL_VILLAGER_LOCATION + "/eyes/yellow.png");

	//Hair male
	private Identifier blackHairMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/black1.png");
	private Identifier blackHairMale2 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/black2.png");
	private Identifier blackHairMale3 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/black3.png");
	private Identifier blackHairMale4 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/black4.png");
	private Identifier brownHairMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/brown1.png");
	private Identifier brownHairMale2 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/brown2.png");
	private Identifier brownHairMale3 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/brown3.png");
	private Identifier brownHairMale4 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/brown4.png");
	private Identifier blondeHairMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/blonde1.png");
	private Identifier blondeHairMale2 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/blonde2.png");
	private Identifier blondeHairMale3 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/blonde3.png");
	private Identifier blondeHairMale4 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/blonde4.png");
	private Identifier redHairMale = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/red1.png");
	private Identifier redHairMale2 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/red2.png");
	private Identifier redHairMale3 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/red3.png");
	private Identifier redHairMale4 = new Identifier(StoryCraft.MOD_ID, MALE_LOCATION + "/hair/red4.png");

	//Hair female
	private Identifier blondeHairFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/blonde1.png");
	private Identifier blondeHairFemale2 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/blonde2.png");
	private Identifier blondeHairFemale3 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/blonde3.png");
	private Identifier blondeHairFemale4 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/blonde4.png");
	private Identifier brownHairFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/brown1.png");
	private Identifier brownHairFemale2 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/brown2.png");
	private Identifier brownHairFemale3 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/brown3.png");
	private Identifier brownHairFemale4 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/brown4.png");
	private Identifier blackHairFemale = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/black1.png");
	private Identifier blackHairFemale2 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/black2.png");
	private Identifier blackHairFemale3 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/black3.png");
	private Identifier blackHairFemale4 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/black4.png");
	private Identifier redHairFemale1 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/red1.png");
	private Identifier redHairFemale2 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/red2.png");
	private Identifier redHairFemale3 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/red3.png");
	private Identifier redHairFemale4 = new Identifier(StoryCraft.MOD_ID, FEMALE_LOCATION + "/hair/red4.png");

	private Identifier femaleBaker1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/baker1.png");
	private Identifier femaleMiner1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/miner1.png");
	private Identifier femaleMiner2 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/miner2.png");
	private Identifier femaleGuard1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/guard1.png");
	private Identifier femalePriest1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/priest1.png");
	private Identifier femaleLibrarian1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/librarian1.png");
	private Identifier femaleButcher1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/butcher1.png");
	private Identifier femaleFarmer1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/farmer1.png");
	private Identifier femaleBlackmith1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/blacksmith1.png");
	private Identifier femaleWarrior1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/female/warrior1.png");

	private Identifier maleBaker1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/baker1.png");
	private Identifier maleMiner1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/miner1.png");
	private Identifier maleMiner2 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/miner2.png");
	private Identifier maleGuard1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/guard1.png");
	private Identifier malePriest1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/priest1.png");
	private Identifier maleLibrarian1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/librarian1.png");
	private Identifier maleButcher1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/butcher1.png");
	private Identifier maleFarmer1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/farmer1.png");
	private Identifier maleBlackmith1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/blacksmith1.png");
	private Identifier maleWarrior1 = new Identifier(StoryCraft.MOD_ID, PROFESSION_LOCATION + "/male/warrior1.png");

	public TextureAssembler(SocialVillager socialVillager) {
		this.eyeColor = socialVillager.getVillagerAspects().getEyeColor();
		this.hairColor = socialVillager.getVillagerAspects().getHairColor();
		this.skinColor = socialVillager.getVillagerAspects().getSkinColor();
		this.hairstyle = socialVillager.getVillagerAspects().getHairStyle();
		this.gender = socialVillager.getVillagerGender().getGender();
		this.profession = socialVillager.getVillagerProfession().getProfession();
	}

	public BufferedImage createTexture() {
		BufferedImage totalImage;
		BufferedImage hairImage;
		BufferedImage eyeImage;
		BufferedImage skinImage;
		BufferedImage outfitImage;
		BufferedImage professionImage;

		try {
			InputStream inputstream;
			if (gender.equals("Male")) {
				inputstream = null;
				int random3 = new Random().nextInt(3);
				switch (random3) {
					case 0:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit1Male).getInputStream();
						break;
					case 1:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit2Male).getInputStream();
						break;
					case 2:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit3Male).getInputStream();
						break;
				}
				outfitImage = ImageIO.read(Objects.requireNonNull(inputstream));
				inputstream.close();
				switch (this.skinColor) {
					case "Light":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(lightSkinMale).getInputStream();
						break;
					case "Medium":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(medSkinMale).getInputStream();
						break;
					case "Dark":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(darkSkinMale).getInputStream();
						break;
				}
				skinImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (this.profession) {
					case "Baker":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleBaker1).getInputStream();
						break;
					case "Miner":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleMiner1).getInputStream();
						break;
					case "Guard":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleGuard1).getInputStream();
						break;
					case "Butcher":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleButcher1).getInputStream();
						break;
					case "Librarian":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleLibrarian1).getInputStream();
						break;
					case "Priest":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(malePriest1).getInputStream();
						break;
					case "Farmer":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleFarmer1).getInputStream();
						break;
					case "Blacksmith":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleBlackmith1).getInputStream();
						break;
					case "Warrior":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(maleWarrior1).getInputStream();
						break;
				}
				professionImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (hairColor) {
					case "Black":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairMale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairMale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairMale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairMale4).getInputStream();
						}
						break;
					case "Blonde":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairMale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairMale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairMale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairMale4).getInputStream();
						}
						break;
					case "Brown":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairMale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairMale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairMale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairMale4).getInputStream();
						}
						break;
					case "Red":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairMale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairMale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairMale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairMale4).getInputStream();
						}
						break;
				}
				hairImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (eyeColor) {
					case "Black":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackEyes).getInputStream();
						break;
					case "Blue":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blueEyes).getInputStream();
						break;
					case "Brown":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownEyes).getInputStream();
						break;
					case "Green":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(greenEyes).getInputStream();
						break;
					case "Lime":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(limeEyes).getInputStream();
						break;
					case "Pink":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(pinkEyes).getInputStream();
						break;
					case "Yellow":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(yellowEyes).getInputStream();
						break;
				}
				eyeImage = ImageIO.read(inputstream);
				inputstream.close();
			} else {
				inputstream = null;
				int random3 = new Random().nextInt(3);
				switch (random3) {
					case 0:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit1Female).getInputStream();
						break;
					case 1:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit2Female).getInputStream();
						break;
					case 2:
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(outfit3Female).getInputStream();
						break;
				}
				outfitImage = ImageIO.read(Objects.requireNonNull(inputstream));
				inputstream.close();
				switch (this.skinColor) {
					case "Light":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(lightSkinFemale).getInputStream();
						break;
					case "Medium":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(mediumSkinFemale).getInputStream();
						break;
					case "Dark":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(darkSkinFemale).getInputStream();
						break;
				}
				skinImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (this.profession) {
					case "Baker":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleBaker1).getInputStream();
						break;
					case "Miner":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleMiner1).getInputStream();
						break;
					case "Guard":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleGuard1).getInputStream();
						break;
					case "Butcher":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleButcher1).getInputStream();
						break;
					case "Librarian":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleLibrarian1).getInputStream();
						break;
					case "Priest":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femalePriest1).getInputStream();
						break;
					case "Farmer":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleFarmer1).getInputStream();
						break;
					case "Blacksmith":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleBlackmith1).getInputStream();
						break;
					case "Warrior":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(femaleWarrior1).getInputStream();
						break;
				}
				professionImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (this.hairColor) {
					case "Black":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairFemale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairFemale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairFemale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackHairFemale4).getInputStream();
						}

						break;
					case "Blonde":
						switch(this.hairstyle) {
							case 1:
								inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairFemale).getInputStream();
								break;
							case 2:
								inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairFemale2).getInputStream();
								break;
							case 3:
								inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairFemale3).getInputStream();
								break;
							case 4:
								inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blondeHairFemale4).getInputStream();
								break;
						}
						break;
					case "Brown":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairFemale).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairFemale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairFemale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownHairFemale4).getInputStream();
						}
						break;
					case "Red":
						if (hairstyle.equals(1)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairFemale1).getInputStream();
						} else if (hairstyle.equals(2)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairFemale2).getInputStream();
						} else if (hairstyle.equals(3)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairFemale3).getInputStream();
						} else if (hairstyle.equals(4)) {
							inputstream = MinecraftClient.getInstance().getResourceManager().getResource(redHairFemale4).getInputStream();
						}
						break;
				}
				hairImage = ImageIO.read(inputstream);
				inputstream.close();
				switch (eyeColor) {
					case "Black":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blackEyes).getInputStream();
						break;
					case "Blue":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(blueEyes).getInputStream();
						break;
					case "Brown":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(brownEyes).getInputStream();
						break;
					case "Green":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(greenEyes).getInputStream();
						break;
					case "Lime":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(limeEyes).getInputStream();
						break;
					case "Pink":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(pinkEyes).getInputStream();
						break;
					case "Yellow":
						inputstream = MinecraftClient.getInstance().getResourceManager().getResource(yellowEyes).getInputStream();
						break;
				}
				eyeImage = ImageIO.read(inputstream);
				inputstream.close();
			}
			totalImage = new BufferedImage(skinImage.getWidth(), skinImage.getHeight(), 2);
			Graphics2D g = totalImage.createGraphics();
			g.drawImage(skinImage, 0, 0, null);
			g.drawImage(eyeImage, 0, 0, null);
			g.drawImage(outfitImage, 0, 0, null);
			g.drawImage(hairImage, 0, 0, null);
			g.drawImage(professionImage, 0, 0, null);
			g.dispose();

			return totalImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}