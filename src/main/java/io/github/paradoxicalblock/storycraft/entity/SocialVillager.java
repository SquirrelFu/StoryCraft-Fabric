package io.github.paradoxicalblock.storycraft.entity;

import io.github.paradoxicalblock.storycraft.entity.ai.goal.FindDiamondBlockGoal;
import io.github.paradoxicalblock.storycraft.entity.ai.goal.VillagerFarmGoal;
import io.github.paradoxicalblock.storycraft.entity.ai.goal.VillagerStareGoal;
import io.github.paradoxicalblock.storycraft.gui.SocialScreen;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import io.github.paradoxicalblock.storycraft.socialVillager.SocialVillagerData;
import io.github.paradoxicalblock.storycraft.socialVillager.VillagerAspects;
import io.github.paradoxicalblock.storycraft.socialVillager.VillagerGender;
import io.github.paradoxicalblock.storycraft.socialVillager.VillagerProfession;
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
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
    private final BasicInventory inventory = new BasicInventory(8);
    private boolean goalsSet;
    private boolean staring;

    private VillagerAspects villagerAspects = new VillagerAspects();
    private VillagerProfession villagerProfession = new VillagerProfession();
    private VillagerGender villagerGender = new VillagerGender();
    private SocialVillagerData socialVillagerData = new SocialVillagerData(villagerAspects, villagerGender, villagerProfession);

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
            this.setCustomName(new LiteralText(firstName + " " + lastName));

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

    private void setSpecificGoals() {
        if (!this.goalsSet) {
            this.goalsSet = true;
            if (this.isBaby()) {
                this.goalSelector.add(8, new VillagerStareGoal(this, 0.32D));
            } else if (this.profession.equals("Farmer")) {
                this.goalSelector.add(6, new VillagerFarmGoal(this, 0.6D));
            } else if (this.profession.equals("Guard")) {
                this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
                this.goalSelector.add(2, new GoToEntityTargetGoal(this, 0.9D, 32.0F));
                this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D));
                this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
                this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
                this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
                this.goalSelector.add(8, new LookAroundGoal(this));
                this.targetSelector.add(2, new RevengeGoal(this));
                this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity_1) ->
                        livingEntity_1 instanceof Monster && !(livingEntity_1 instanceof CreeperEntity)));

            }
        }
    }

    protected void onGrowUp() {
        if (this.profession.equals("Farmer")) {
            this.goalSelector.add(8, new VillagerFarmGoal(this, 0.6D));
        } else if (this.profession.equals("Guard")) {
            this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
            this.goalSelector.add(2, new GoToEntityTargetGoal(this, 0.9D, 32.0F));
            this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D));
            this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
            this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
            this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
            this.goalSelector.add(8, new LookAroundGoal(this));
            this.targetSelector.add(2, new RevengeGoal(this));
            this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity_1) ->
                    livingEntity_1 instanceof Monster && !(livingEntity_1 instanceof CreeperEntity)));

        }

        super.onGrowUp();
    }

    public void setStaring(boolean boolean_1) {
        this.staring = boolean_1;
    }

    public boolean isStaring() {
        return this.staring;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    public BasicInventory getInventory() {
        return this.inventory;
    }

    public boolean canBreed() {
        boolean boolean_1 = this.profession.equals("Farmer");
        if (boolean_1) {
            return !this.hasEnoughFood(5);
        } else {
            return !this.hasEnoughFood(1);
        }
    }

    private boolean hasEnoughFood(int int_1) {
        boolean boolean_1 = this.profession.equals("Farmer");

        for(int int_2 = 0; int_2 < this.getInventory().getInvSize(); ++int_2) {
            ItemStack itemStack_1 = this.getInventory().getInvStack(int_2);
            Item item_1 = itemStack_1.getItem();
            int int_3 = itemStack_1.getCount();
            if (item_1 == Items.BREAD && int_3 >= 3 * int_1 || item_1 == Items.POTATO && int_3 >= 12 * int_1 || item_1 == Items.CARROT && int_3 >= 12 * int_1 || item_1 == Items.BEETROOT && int_3 >= 12 * int_1) {
                return true;
            }

            if (boolean_1 && item_1 == Items.WHEAT && int_3 >= 9 * int_1) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSeed() {
        for(int int_1 = 0; int_1 < this.getInventory().getInvSize(); ++int_1) {
            Item item_1 = this.getInventory().getInvStack(int_1).getItem();
            if (item_1 == Items.WHEAT_SEEDS || item_1 == Items.POTATO || item_1 == Items.CARROT || item_1 == Items.BEETROOT_SEEDS) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(hairColorUnified, "Brown");
        this.dataTracker.startTracking(eyeColorUnified, "Black");
        this.dataTracker.startTracking(skinColorUnified, "Light");
        this.dataTracker.startTracking(hairStyleUnified, 1);
        this.dataTracker.startTracking(orientationUnified, "Straight");
        this.dataTracker.startTracking(serverUUID, this.getUuidAsString());
        this.dataTracker.startTracking(genderUnified, "Female");
        this.dataTracker.startTracking(professionUnified, "Nomad");
    }

    public <T> T get(TrackedData<T> key) {
        return this.dataTracker.get(key);
    }

    public <T> void set(TrackedData<T> key, T value) {
        this.dataTracker.set(key, value);
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
        this.hairStyle = villagerAspects.getHairStyle();
        this.hairColor = villagerAspects.getHairColor();
    }

    private void setupEyes() {
        this.eyeColor = villagerAspects.getEyeColor();
    }

    private void setupSkin() {
        this.skinColor = villagerAspects.getSkinColor();
    }

    private void setupGender() {
        this.gender = villagerGender.getGender();
    }

    private void setupProfession() {
        this.profession = villagerProfession.getProfession();
    }

    private void setupOrientation() {
        this.sexuality = villagerAspects.getSexuality();
    }

    public boolean canImmediatelyDespawn(double double_1) {
        return false;
    }

    public VillagerAspects getVillagerAspects() {
        return villagerAspects;
    }

    public VillagerProfession getVillagerProfession() {
        return villagerProfession;
    }

    public VillagerGender getVillagerGender() {
        return villagerGender;
    }

    public SocialVillagerData getSocialVillagerData() {
        return socialVillagerData;
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
        tag.putString("hair_color", hairColor);
        tag.putString("eye_color", eyeColor);
        tag.putString("skin_color", skinColor);
        tag.putString("sexuality", sexuality);
        tag.putInt("hair_style", hairStyle);
        tag.putInt("friendliness", friendliness);
        tag.putInt("bravery", bravery);
        tag.putInt("generosity", generosity);
        tag.putBoolean("apologized", apologized);
        tag.putBoolean("charmed", charmed);
        tag.putString("first_name", firstName);
        tag.putString("last_name", lastName);
        tag.putInt("age", this.getBreedingAge());
        tag.putString("gender", gender);
        tag.putString("profession", profession);
        if (opinions.keySet().size() > 13) {
            for (UUID key : opinions.keySet()) {
                CompoundTag opinionTag = new CompoundTag();
                opinionTag.putUuid("holder", key);
                opinionTag.putInt("opinion", opinions.get(key));
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
        this.dataTracker.set(hairColorUnified, hairColor);
        this.dataTracker.set(eyeColorUnified, eyeColor);
        this.dataTracker.set(skinColorUnified, skinColor);
        this.dataTracker.set(hairStyleUnified, hairStyle);
        this.dataTracker.set(orientationUnified, sexuality);
        this.dataTracker.set(serverUUID, this.getUuidAsString());
        this.dataTracker.set(genderUnified, gender);
        this.dataTracker.set(professionUnified, profession);
        this.friendliness = tag.getInt("friendliness");
        this.bravery = tag.getInt("bravery");
        this.generosity = tag.getInt("generosity");
        this.apologized = tag.getBoolean("apologized");
        this.charmed = tag.getBoolean("charmed");
        this.firstName = tag.getString("first_name");
        this.lastName = tag.getString("last_name");
        for (String key : tag.getKeys()) {
            if (tag.hasUuid(key)) {
                this.opinions.put(tag.getCompound(key).getUuid("holder"), tag.getInt("opinion"));
            }
        }
        if (hairColor == null || hairColor.equals("")) {
            unifiedSetup();
        }
        this.setBreedingAge(tag.getInt("age"));
        this.setCanPickUpLoot(true);
        this.setSpecificGoals();
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