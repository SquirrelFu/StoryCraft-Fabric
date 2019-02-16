package io.github.paradoxicalblock.storycraft.world;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.client.MinecraftClient;

public class DataManager {

	
	protected String fileName = "customData.dat";
	protected File directory;
	protected DataOutputStream out;
	private boolean changed;
	public DataManager()
	{
		directory = new File(MinecraftClient.getInstance().getLevelStorage().toString());
		String filePath = directory.toString() + fileName;
		try {
		new File(filePath).createNewFile();
		out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
