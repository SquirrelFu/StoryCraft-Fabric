package io.github.paradoxicalblock.storycraft.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import io.github.paradoxicalblock.storycraft.gui.SocialScreen;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SocialVillager extends PassiveEntity {
	private String hairColor;
	private String hairRecessive;
	private String eyeColor;
	private String eyeRecessive;
	private String skinColor;
	private String skinRecessive;
	private String sexuality;
	private int hairStyle = 0;
	private int friendliness = 0;
	private int bravery = 0;
	private int generosity = 0;
	private boolean apologized = false;
	private boolean charmed = false;
	private boolean gender;
	public static TrackedData<String> hairColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
	public static TrackedData<String> eyeColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
	public static TrackedData<String> skinColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
	public static TrackedData<Integer> hairStyleUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.INTEGER);
	public static TrackedData<String> orientationUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
	public static TrackedData<String> serverUUID = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
	public static TrackedData<Boolean> genderUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.BOOLEAN);
	public String firstName;
	public String lastName;
	protected HashMap<UUID, Integer> opinions = new HashMap<UUID, Integer>();
	public SocialVillager(World world)
	{
		this(StoryCraft.SOCIAL_VILLAGER, world);
	}
	public SocialVillager(EntityType<?> type, World world) {
		super(type, world);
		Random r = new Random();
		if (hairColor == null || hairColor == "")
		{
			
			unifiedSetup();
			this.dataTracker.set(hairColorUnified,hairColor);
			this.dataTracker.set(eyeColorUnified, eyeColor);
			this.dataTracker.set(skinColorUnified, skinColor);
			this.dataTracker.set(hairStyleUnified, hairStyle);
			this.dataTracker.set(orientationUnified, sexuality);
			this.dataTracker.set(serverUUID, this.getUuidAsString());
			this.setBreedingAge(0);
			
			this.dataTracker.set(genderUnified, gender);
		}
		
		
		try {
			this.firstName = generateFirstName(r.nextBoolean());
			this.lastName = generateLastName();
			this.setCustomName(new StringTextComponent(firstName + " " + lastName));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void initDataTracker()
	{
		super.initDataTracker();
		this.dataTracker.startTracking(hairColorUnified, hairColor);
		this.dataTracker.startTracking(eyeColorUnified, eyeColor);
		this.dataTracker.startTracking(skinColorUnified, skinColor);
		this.dataTracker.startTracking(hairStyleUnified, hairStyle);
		this.dataTracker.startTracking(orientationUnified, sexuality);
		this.dataTracker.startTracking(serverUUID, this.getUuidAsString());
		this.dataTracker.startTracking(genderUnified, gender);
	}
	public String getHairColor()
	{
		return hairColor;
	}
	public void setOpinion(UUID uuid, int newValue)
	{
		this.opinions.put(uuid, newValue);
	}
	public int getOpinion(UUID uuid)
	{
		return opinions.get(uuid);
	}
	public String getEyeColor()
	{
		return eyeColor;
	}
	public boolean getApologized()
	{
		return apologized;
	}
	public void setApologized()
	{
		this.apologized = true;
	}
	public void formOpinion(Entity person)
	{
		Random rand = new Random();
		if (!opinions.containsKey(person.getUuid()))
		{
			opinions.put(person.getUuid(), rand.nextInt(50) - 25);
		}
		
	}
	@Override
	public boolean interactMob(PlayerEntity player, Hand hand)
	{
		if (!opinions.containsKey(player.getUuid()))
		{
			formOpinion(player);
		}
		MinecraftClient.getInstance().openScreen(new SocialScreen(this, player));
		return true;
		
	}
	public String getSkinColor()
	{
		return skinColor;
	}
	public int getHairStyle()
	{
		return hairStyle;
	}
	public void setupHair()
	{
		Random rand = new Random();
		String[] hairList = {"Red", "Brown", "Black", "Blonde"};
		int[] styleList = {1,2,3,4};
		this.hairStyle = styleList[rand.nextInt(4)];
		this.hairColor = hairList[rand.nextInt(4)];
		this.hairRecessive = hairColor;
	}
	public void setupEyes()
	{
		Random rand = new Random();
		String[] eyeList = {"Green", "Brown", "Blue"};
		this.eyeColor = eyeList[rand.nextInt(3)];
		this.eyeRecessive = eyeColor;
	}
	public void setupSkin()
	{
		Random rand = new Random();
		String[] skinList = {"Light", "Medium", "Dark"};
		this.skinColor = skinList[rand.nextInt(3)];
		this.skinRecessive = skinColor;
	}
	public void setupOrientation()
	{
		Random rand = new Random();
		int orientationInt = rand.nextInt(10);
		if (orientationInt == 9)
		{
			boolean orientationBool = rand.nextBoolean();
			if (orientationBool)
			{
				this.sexuality = "Bisexual";
			}
			else
			{
				this.sexuality = "Gay";
			}
		}
		else
		{
			this.sexuality = "Straight";
		}
	}
	@Override
	public void writeCustomDataToTag(CompoundTag tag)
	{
		super.writeCustomDataToTag(tag);
		tag.putString("Hair Color", hairColor);
		tag.putString("Hair Color R", hairRecessive);
		tag.putString("Eye Color", eyeColor);
		tag.putString("Eye Color R", eyeRecessive);
		tag.putString("Skin Color", skinColor);
		tag.putString("Skin Color R", skinRecessive);
		tag.putString("Sexuality", sexuality);
		tag.putInt("Hair Style", hairStyle);
		tag.putInt("Friendliness", friendliness);
		tag.putInt("Bravery", bravery);
		tag.putInt("Generosity", generosity);
		tag.putBoolean("Apologized", apologized);
		tag.putBoolean("Charmed", charmed);
		tag.putString("First Name", firstName);
		tag.putString("Last Name", lastName);
		tag.putInt("Age", this.getBreedingAge());
		if (opinions.keySet().size() > 13)
		{
			for (UUID key : opinions.keySet())
			{
				CompoundTag opinionTag = new CompoundTag();
				opinionTag.putUuid("Holder",key);
				opinionTag.putInt("Opinion", opinions.get(key));
				tag.put(key.toString(), opinionTag);
			}
		}
		
	}
	public void unifiedSetup()
	{
		this.setupEyes();
		this.setupHair();
		this.setupSkin();
		this.setupOrientation();
	}
	@Override
	public void readCustomDataFromTag(CompoundTag tag)
	{
		super.readCustomDataFromTag(tag);
		this.hairColor = tag.getString("Hair Color");
		this.hairRecessive = tag.getString("Hair Color R");
		this.eyeColor = tag.getString("Eye Color");
		this.eyeRecessive = tag.getString("Eye Color R");
		this.skinColor = tag.getString("Skin Color");
		this.skinRecessive = tag.getString("Skin Color R");
		this.sexuality = tag.getString("Sexuality");
		this.hairStyle = tag.getInt("Hair Style");
		this.friendliness = tag.getInt("Friendliness");
		this.bravery = tag.getInt("Bravery");
		this.generosity = tag.getInt("Generosity");
		this.apologized = tag.getBoolean("Apologized");
		this.charmed = tag.getBoolean("Charmed");
		this.firstName = tag.getString("First Name");
		this.lastName = tag.getString("Last Name");
		for (String key : tag.getKeys())
		{
			if (tag.hasUuid(key))
			{
				this.opinions.put(tag.getCompound(key).getUuid("Holder"),tag.getInt("Opinion"));
			}
		}
		if (hairColor == null || hairColor == "")
		{
			unifiedSetup();
		}
		this.setBreedingAge(tag.getInt("Age"));
		this.dataTracker.set(hairColorUnified,hairColor);
		this.dataTracker.set(eyeColorUnified, eyeColor);
		this.dataTracker.set(skinColorUnified, skinColor);
		this.dataTracker.set(hairStyleUnified, hairStyle);
		this.dataTracker.set(orientationUnified, sexuality);
		this.dataTracker.set(serverUUID, this.getUuidAsString());
		
	}
	public int getFriendliness()
	{
		return this.friendliness;
	}
	public void setCharmed()
	{
		this.charmed = true;
	}
	public boolean getCharmed()
	{
		return this.charmed;
	}
	@Override
	public PassiveEntity createChild(PassiveEntity var1) {
		// TODO Auto-generated method stub
		return null;
	}
	private String generateFirstName(boolean genderbool) throws IOException
	{
		String firstNameOut;
		Random rand = new Random();
		Identifier malenames = new Identifier("storycraft:names/malenames.txt");
		Identifier neutralnames = new Identifier("storycraft:names/unisexnames.txt");
		Identifier femalenames = new Identifier("storycraft:names/femalenames.txt");
		InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(malenames).getInputStream();
		InputStream stream2 = MinecraftClient.getInstance().getResourceManager().getResource(neutralnames).getInputStream();
		InputStream stream3 = MinecraftClient.getInstance().getResourceManager().getResource(femalenames).getInputStream();
		if(genderbool)
		{
			if (this.gender)
			{
				Scanner scanner = new Scanner(stream);
				StringBuilder builder = new StringBuilder();
				while (scanner.hasNextLine())
				{
					builder.append(scanner.nextLine());
					builder.append(",");
				}
				String[] strings = builder.toString().split(",");
				firstNameOut = strings[rand.nextInt(strings.length)];
				scanner.close();
			}
			
			else
			{
				Scanner scanner = new Scanner(stream3);
				StringBuilder builder = new StringBuilder();
				while (scanner.hasNextLine())
				{
					builder.append(scanner.nextLine());
					builder.append(",");
				}
				String[] strings = builder.toString().split(",");
				firstNameOut = strings[rand.nextInt(strings.length)];
				scanner.close();
			}
		}
		else
		{
			Scanner scanner = new Scanner(stream2);
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine())
			{
				builder.append(scanner.nextLine());
				builder.append(",");
			}
			String[] strings = builder.toString().split(",");
			firstNameOut = strings[rand.nextInt(strings.length)];
			scanner.close();
		}
		stream.close();
		stream2.close();
		stream3.close();
		return firstNameOut;
	}
	private String generateLastName() throws IOException
	{
		String lastNameOut;
		Random rand = new Random();
		Identifier surnames = new Identifier("storycraft:names/surnames.txt");
		InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(surnames).getInputStream();
		Scanner scanner = new Scanner(stream);
		StringBuilder builder = new StringBuilder();
		while (scanner.hasNextLine())
		{
			builder.append(scanner.nextLine());
			builder.append(",");
		}
		String[] strings = builder.toString().split(",");
		lastNameOut = strings[rand.nextInt(strings.length)];
		stream.close();
		scanner.close();
		return lastNameOut;
	}
}
