package io.github.paradoxicalblock.storycraft.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SocialVillagerFemale extends SocialVillagerBase {

	public SocialVillagerFemale(World world)
	{
		super(StoryCraft.SOCIAL_VILLAGER_FEMALE, world);
		Random rand = new Random();
		try {
			firstName = generateFirstName(rand.nextBoolean());
			lastName = generateLastName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setCustomName(new StringTextComponent(firstName + " " + lastName));
	}
	public SocialVillagerFemale(EntityType<?> type, World world) {
		super(type, world);
	}
	@Override
	public PassiveEntity createChild(PassiveEntity arg0) {
		return null;
	}
	private String generateFirstName(boolean genderbool) throws IOException
	{
		String firstNameOut;
		Random rand = new Random();
		Identifier femalenames = new Identifier("storycraft:names/femalenames.txt");
		Identifier neutralnames = new Identifier("storycraft:names/unisexnames.txt");
		InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(femalenames).getInputStream();
		InputStream stream2 = MinecraftClient.getInstance().getResourceManager().getResource(neutralnames).getInputStream();
		if(genderbool)
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
