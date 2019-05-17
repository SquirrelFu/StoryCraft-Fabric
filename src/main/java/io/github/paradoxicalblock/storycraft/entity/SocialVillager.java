package io.github.paradoxicalblock.storycraft.entity;

import io.github.paradoxicalblock.storycraft.entity.ai.goal.FindDiamondBlockGoal;
import io.github.paradoxicalblock.storycraft.gui.SocialScreen;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class SocialVillager extends PassiveEntity {
    public static TrackedData<String> hairColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> eyeColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> skinColorUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<Integer> hairStyleUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.INTEGER);
    public static TrackedData<String> serverUUID = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> genderUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public static TrackedData<String> professionUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    private static TrackedData<String> orientationUnified = DataTracker.registerData(SocialVillager.class, TrackedDataHandlerRegistry.STRING);
    public String firstName;
    public String lastName;
    private HashMap<UUID, Integer> opinions = new HashMap<>();
    private String hairColor;
    private String eyeColor;
    private String skinColor;
    private String sexuality;
    private String gender;
    private String profession;
    private int hairStyle = 0;
    private int friendliness = 0;
    private int bravery = 0;
    private int generosity = 0;
    private boolean apologized = false;
    private boolean charmed = false;

    public SocialVillager(World world) {
        this(StoryCraft.SOCIAL_VILLAGER, world);
    }

    private SocialVillager(EntityType<? extends PassiveEntity> type, World world) {
        super(type, world);
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);
        this.setCanPickUpLoot(true);
        if (hairColor == null || hairColor.equals("")) {
            unifiedSetup();
            this.dataTracker.set(hairColorUnified, hairColor);
            this.dataTracker.set(eyeColorUnified, eyeColor);
            this.dataTracker.set(skinColorUnified, skinColor);
            this.dataTracker.set(hairStyleUnified, hairStyle);
            this.dataTracker.set(orientationUnified, sexuality);
            this.dataTracker.set(serverUUID, this.getUuidAsString());
            this.dataTracker.set(genderUnified, gender);
            this.dataTracker.set(professionUnified, profession);
        }

        try {
            this.firstName = generateFirstName(this.gender);
            this.lastName = generateLastName();
            this.setCustomName(new TextComponent(firstName + " " + lastName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, ZombieEntity.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, EvokerEntity.class, 12.0F, 0.8D, 0.8D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VindicatorEntity.class, 8.0F, 0.8D, 0.8D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, VexEntity.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PillagerEntity.class, 15.0F, 0.6D, 0.6D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, IllusionerEntity.class, 12.0F, 0.6D, 0.6D));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 0.6D));
        this.goalSelector.add(9, new GoToEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(9, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.add(5, new FindDiamondBlockGoal(this, 1.0D));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(hairColorUnified, hairColor);
        this.dataTracker.startTracking(eyeColorUnified, eyeColor);
        this.dataTracker.startTracking(skinColorUnified, skinColor);
        this.dataTracker.startTracking(hairStyleUnified, hairStyle);
        this.dataTracker.startTracking(orientationUnified, sexuality);
        this.dataTracker.startTracking(serverUUID, this.getUuidAsString());
        this.dataTracker.startTracking(genderUnified, gender);
        this.dataTracker.startTracking(professionUnified, profession);
    }

    public void setOpinion(UUID uuid, int newValue) {
        this.opinions.put(uuid, newValue);
    }

    public int getOpinion(UUID uuid) {
        return opinions.get(uuid);
    }

    public boolean getApologized() {
        return apologized;
    }

    public void setApologized() {
        this.apologized = true;
    }

    private void formOpinion(Entity person) {
        if (!opinions.containsKey(person.getUuid())) {
            opinions.put(person.getUuid(), getRand().nextInt(50) - 25);
        }
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        if (!opinions.containsKey(player.getUuid())) {
            formOpinion(player);
        }
        MinecraftClient.getInstance().openScreen(new SocialScreen(this, player));
        return true;

    }

    private void setupHair() {
        String[] hairList = {"Red", "Brown", "Black", "Blonde"};
        int[] styleList = {1, 2, 3, 4};
        this.hairStyle = styleList[getRand().nextInt(styleList.length)];
        this.hairColor = hairList[getRand().nextInt(hairList.length)];
    }

    private void setupEyes() {
        String[] eyeList = {"Black", "Blue", "Brown", "Green", "Lime", "Pink", "Yellow"};
        this.eyeColor = eyeList[getRand().nextInt(eyeList.length)];
    }

    private void setupSkin() {
        String[] skinList = {"Light", "Medium", "Dark"};
        this.skinColor = skinList[getRand().nextInt(skinList.length)];
    }

    private void setupGender() {
        String[] genderList = {"Male", "Female"};
        this.gender = genderList[getRand().nextInt(genderList.length)];
    }

    private void setupProfession() {
        String[] professionList = {"Lumberjack", "Farmer", "Architect", "Tradesman", "Merchant", "Blacksmith", "Enchanter", "Druid", "Butcher",
                "Librarian", "Nomad", "Baker", "Priest", "Miner", "Guard"};
        if(random.nextInt(100) == 0)
            this.profession = "Mayor";
        this.profession = professionList[getRand().nextInt(professionList.length)];
    }

    private void setupOrientation() {
        int orientationInt = getRand().nextInt(10);
        if (orientationInt == 9) {
            boolean orientationBool = getRand().nextBoolean();
            if (orientationBool) {
                this.sexuality = "Bisexual";
            } else {
                this.sexuality = "Gay";
            }
        } else {
            this.sexuality = "Straight";
        }
    }

    public boolean canImmediatelyDespawn(double double_1) {
        return false;
    }

    public String getGender() {
        return gender;
    }

    public String getProfession() {
        return profession;
    }

    @Override
    public void sleep(BlockPos blockPos_1) {
        super.sleep(blockPos_1);
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte byte_1) {
        if (byte_1 == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (byte_1 == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (byte_1 == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (byte_1 == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(byte_1);
        }
    }

    @Environment(EnvType.CLIENT)
    private void produceParticles(ParticleEffect particleParameters_1) {
        for (int int_1 = 0; int_1 < 5; ++int_1) {
            double double_1 = this.random.nextGaussian() * 0.02D;
            double double_2 = this.random.nextGaussian() * 0.02D;
            double double_3 = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(particleParameters_1, this.x + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.y + 1.0D + (double) (this.random.nextFloat() * this.getHeight()), this.z + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), double_1, double_2, double_3);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putString("Hair Color", hairColor);
        tag.putString("Eye Color", eyeColor);
        tag.putString("Skin Color", skinColor);
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
        tag.putString("Gender", gender);
        tag.putString("Profession", profession);
        if (opinions.keySet().size() > 13) {
            for (UUID key : opinions.keySet()) {
                CompoundTag opinionTag = new CompoundTag();
                opinionTag.putUuid("Holder", key);
                opinionTag.putInt("Opinion", opinions.get(key));
                tag.put(key.toString(), opinionTag);
            }
        }

    }

    private void unifiedSetup() {
        this.setupEyes();
        this.setupHair();
        this.setupSkin();
        this.setupGender();
        this.setupOrientation();
        this.setupProfession();
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.hairColor = tag.getString("Hair Color");
        this.eyeColor = tag.getString("Eye Color");
        this.skinColor = tag.getString("Skin Color");
        this.sexuality = tag.getString("Sexuality");
        this.hairStyle = tag.getInt("Hair Style");
        this.friendliness = tag.getInt("Friendliness");
        this.bravery = tag.getInt("Bravery");
        this.generosity = tag.getInt("Generosity");
        this.apologized = tag.getBoolean("Apologized");
        this.charmed = tag.getBoolean("Charmed");
        this.firstName = tag.getString("First Name");
        this.lastName = tag.getString("Last Name");
        this.gender = tag.getString("Gender");
        this.profession = tag.getString("Profession");
        for (String key : tag.getKeys()) {
            if (tag.hasUuid(key)) {
                this.opinions.put(tag.getCompound(key).getUuid("Holder"), tag.getInt("Opinion"));
            }
        }
        if (hairColor == null || hairColor.equals("")) {
            unifiedSetup();
        }
        this.setBreedingAge(tag.getInt("Age"));
        this.dataTracker.set(hairColorUnified, hairColor);
        this.dataTracker.set(eyeColorUnified, eyeColor);
        this.dataTracker.set(skinColorUnified, skinColor);
        this.dataTracker.set(hairStyleUnified, hairStyle);
        this.dataTracker.set(orientationUnified, sexuality);
        this.dataTracker.set(serverUUID, this.getUuidAsString());
        this.dataTracker.set(genderUnified, gender);
        this.dataTracker.set(professionUnified, profession);
    }

    public int getFriendliness() {
        return this.friendliness;
    }

    public void setCharmed() {
        this.charmed = true;
    }

    public boolean getCharmed() {
        return this.charmed;
    }

    @Override
    public PassiveEntity createChild(PassiveEntity var1) {
        return new SocialVillager(StoryCraft.SOCIAL_VILLAGER, this.world);
    }

    private String generateFirstName(String gender) throws IOException {
        String firstNameOut;
        Random rand = new Random();
        Identifier male = new Identifier(StoryCraft.MOD_ID, "names/male.txt");
        Identifier female = new Identifier(StoryCraft.MOD_ID, "names/female.txt");
        InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(male).getInputStream();
        InputStream stream3 = MinecraftClient.getInstance().getResourceManager().getResource(female).getInputStream();
        if (gender.equals("Male")) {
            Scanner scanner = new Scanner(stream);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append(",");
            }
            String[] strings = builder.toString().split(",");
            firstNameOut = strings[rand.nextInt(strings.length)];
            scanner.close();
            stream.close();
        } else {
            Scanner scanner2 = new Scanner(stream3);
            StringBuilder builder2 = new StringBuilder();
            while (scanner2.hasNextLine()) {
                builder2.append(scanner2.nextLine());
                builder2.append(",");
            }
            String[] strings2 = builder2.toString().split(",");
            firstNameOut = strings2[rand.nextInt(strings2.length)];
            scanner2.close();
            stream3.close();
        }
        return firstNameOut;
    }

    private String generateLastName() throws IOException {
        String lastNameOut;
        Random rand = new Random();
        Identifier surnames = new Identifier(StoryCraft.MOD_ID, "names/surnames.txt");
        InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(surnames).getInputStream();
        Scanner scanner = new Scanner(stream);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
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