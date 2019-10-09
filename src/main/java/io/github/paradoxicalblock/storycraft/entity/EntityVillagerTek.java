/*
package io.github.paradoxicalblock.storycraft.entity;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import io.github.paradoxicalblock.storycraft.ProfessionType;
import io.github.paradoxicalblock.storycraft.Village;
import io.github.paradoxicalblock.storycraft.VillagerRole;
import io.github.paradoxicalblock.storycraft.structures.VillageStructure;
import io.github.paradoxicalblock.storycraft.structures.VillageStructureType;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.*;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.client.ParticleThought;
import net.tangotek.tektopia.entities.ai.EntityAIEatFood;
import net.tangotek.tektopia.items.ItemProfessionToken;
import net.tangotek.tektopia.network.PacketVillagerThought;
import net.tangotek.tektopia.storage.ItemDesireSet;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructureHome;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class EntityVillagerTek extends EntityVillageNavigator {
    public static final EntityAttribute MAX_HUNGER = (new ClampedEntityAttribute(null, "generic.hunger", 100.0D, 0.0D, 100.0D)).setName("Hunger").setTracked(true);
    public static final EntityAttribute MAX_HAPPY = (new ClampedEntityAttribute(null, "generic.happy", 100.0D, 0.0D, 100.0D)).setName("Happy").setTracked(true);
    public static final EntityAttribute MAX_INTELLIGENCE = (new ClampedEntityAttribute(null, "generic.intelligence", 100.0D, 0.0D, 100.0D)).setName("Intelligence").setTracked(true);
    protected static final int DEFAULT_JOB_PRIORITY = 50;
    private static final TrackedData<Integer> HUNGER = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> HAPPY = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INTELLIGENCE = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> FORCE_AXIS = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> ACTION_ITEM = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Byte> MOVEMENT_MODE = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> BLESSED = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> VISIT_TAVERN = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> READ_BOOK = DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final boolean ALL_MALES = false;
    private static final boolean ALL_FEMALES = false;
    private static final Map<ProfessionType, TrackedData<Integer>> SKILLS = new EnumMap<>(ProfessionType.class);
    public static int SLEEP_DURATION = 8000;
    public static int SLEEP_START_TIME = 16000;
    public static int SLEEP_END_TIME = SLEEP_START_TIME + SLEEP_DURATION;
    public static int WORK_START_TIME = 500;
    public static int WORK_END_TIME = 11500;
    private static int[] recentEatPenalties = {2, 0, -3, -7, -12, -18};

    static {
        for (ProfessionType pt : ProfessionType.values()) {
            SKILLS.put(pt, DataTracker.registerData(EntityVillagerTek.class, TrackedDataHandlerRegistry.INTEGER));
        }
    }

    private final ProfessionType professionType;
    protected ItemDesireSet desireSet;
    protected VillagerInventory villagerInventory;
    protected BlockPos bedPos = null;
    protected BlockPos homeFrame = null;

    protected EntityAnimal leadAnimal = null;
    protected int sleepOffset = 0;
    protected int wantsLearning = 0;
    protected boolean wantsTavern = false;
    protected int lastSadTick = 0;
    protected int lastSadThrottle = 200;
    protected int daysAlive = 0;
    private PacketVillagerThought nextThought = null;
    private MovementMode lastMovementMode;
    private VillageStructure lastCrowdCheck = null;
    private int idle = 0;
    private int dayCheckTime = 0;
    private Map<String, DataParameter<Boolean>> aiFilters;
    private LinkedList<Integer> recentEats = new LinkedList();

    public EntityVillagerTek(World worldIn, ProfessionType profType, int roleMask) {
        super(worldIn, roleMask);
        this.professionType = profType;
        setCanPickUpLoot(true);
        this.villagerInventory = new VillagerInventory(this, "Items", false, 27);
        setSize(0.6F, 1.95F);
        setHunger(getMaxHunger());
        setHappy(getMaxHappy());
        setIntelligence(0);

        ModEntities.makeTaggedEntity(this, EntityTagType.VILLAGER);

        Runnable foodChomp = () ->
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1.0F, this.rand.nextFloat() * 0.2F + 0.9F, false);


        Runnable doneEating = () -> {
            unequipActionItem();
            if (getRNG().nextInt(12) == 0) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.NEUTRAL, 1.0F, this.rand.nextFloat() * 0.2F + 0.9F, false);
            }
        };
        if (this.world.isRemote) {
            addAnimationTrigger("tektopia:villager_eat", 25, foodChomp);
            addAnimationTrigger("tektopia:villager_eat", 50, foodChomp);
            addAnimationTrigger("tektopia:villager_eat", 75, foodChomp);
            addAnimationTrigger("tektopia:villager_eat", 90, doneEating);
        }

        if (!worldIn.isRemote) {
            randomizeGoals();
            if (this.professionType != null && getBaseSkill(this.professionType) < 1) {
                setSkill(this.professionType, 1);
            }
        }
    }

    protected static void setupAnimations(AnimationHandler animHandler, String modelName) {
        EntityVillageNavigator.setupAnimations(animHandler, modelName);
        animHandler.addAnim("tektopia", "villager_eat", modelName, false);
        animHandler.addAnim("tektopia", "villager_sleep", modelName, true);
        animHandler.addAnim("tektopia", "villager_sit", modelName, true);
        animHandler.addAnim("tektopia", "villager_sit_cheer", modelName, false);
        animHandler.addAnim("tektopia", "villager_walk", modelName, true);
        animHandler.addAnim("tektopia", "villager_walk_sad", modelName, true);
        animHandler.addAnim("tektopia", "villager_run", modelName, true);
        animHandler.addAnim("tektopia", "villager_read", modelName, false);
    }

    public static Function<EntityVillagerTek, VillageStructure> findLocalTavern() {
        return p -> {
            if (p.hasVillage()) {
                VillageStructure tavern = p.getVillage().getNearestStructure(VillageStructureType.TAVERN, p.getBlockPos());
              return tavern;
            }
            return null;
        };
    }

    public static Function<EntityVillagerTek, VillageStructure> getVillagerHome() {
        return p -> {
            if (p.hasHome()) {
                return p.getVillage().getStructureFromFrame(p.homeFrame);
            }
            return null;
        };
    }

    private static void cleanUpInventory(EntityVillagerTek v) {
        v.cleanUpInventory();
    }

    public static Function<ItemStack, Integer> foodBetterThan(EntityVillagerTek v, int foodValue) {
        return p -> {
            int val = foodValue(v).apply(p);
            return (val > foodValue) ? val : -1;
        };
    }

    public static Function<ItemStack, Integer> foodValue(EntityVillagerTek v) {
        return p -> Integer.valueOf((int) ((ModItems.isTaggedItem(p, ItemTagType.VILLAGER) ? 1.0F : 0.5F) * foodItemValue(v).apply(p.getItem()).intValue()));
    }

    public static Function<Item, Integer> foodItemValue(EntityVillagerTek v) {
        return i -> Integer.valueOf(EntityAIEatFood.getFoodScore(i, v));
    }

    protected void initEntityAIBase() {
        Function<ItemStack, Integer> bestFood = p -> Integer.valueOf(EntityAIEatFood.getFoodScore(p.getItem(), this));
        getDesireSet().addItemDesire(new UpgradeItemDesire("Food", bestFood, 1, 3, 4, p -> (p.isHungry() || p.isStoragePriority())));

        addTask(50, new EntityAIEatFood(this));
        addTask(50, new EntityAIRetrieveFromStorage2(this));
        addTask(50, new EntityAISleep(this));
        addTask(50, new EntityAIDeliverToStorage2(this));
        addTask(50, new EntityAITavernVisit(this, p -> (p.wantsTavern() && !p.isWorkTime() && !p.shouldSleep())));
        addTask(50, new EntityAIWanderStructure(this, getVillagerHome(), p -> (!p.isWorkTime() && !wantsTavern()), 12));
        addTask(50, new EntityAIReadBook(this));


        addTask(60, new EntityAIGenericMove(this, p -> (p.isWorkTime() && p.hasVillage() && p.getIdle() > 100), v -> this.village.getLastVillagerPos(), MovementMode.WALK, null, null));


        addTask(150, new EntityAIIdleCheck(this));
    }

    protected void initEntityAI() {
        this.desireSet = new ItemDesireSet();

        addTask(0, new EntityAISwimming(this));
        addTask(1, new EntityAIFleeEntity(this, p -> isFleeFrom(p), 16.0F));

        addTask(15, new EntityAIUseDoor(this, true));
        addTask(15, new EntityAIOpenGate(this));

        initEntityAIBase();
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        setCustomName(generateName());

        double intel = getRand().nextGaussian() * 10.0D;
        if (intel < 0.0D) {
            intel *= 0.3D;
        }
        intel = Math.max(intel + 10.0D, 2.0D);
        setIntelligence((int) intel);

        return super.onInitialSpawn(difficulty, livingdata);
    }

    public Text generateName() {
        String nameTytpe = isMale() ? "malename" : "femalename";

        String firstSTotal = I18n.translate("villager." + nameTytpe + ".total");
        int firstTotal = Integer.parseInt(firstSTotal);
        String lastSTotal = I18n.translate("villager.lastname.total");
        int lastTotal = Integer.parseInt(lastSTotal);

        String firstName = I18n.translate("villager." + nameTytpe + "." + getRand().nextInt(firstTotal));
        String lastName = I18n.translate("villager.lastname." + getRand().nextInt(lastTotal));

        return new LiteralText(firstName + " " + lastName);
    }

    public String getLastName() {
        Text name = getCustomName();
        String[] splitNames = Objects.requireNonNull(name).getString().split("\\s+");
        if (splitNames.length >= 2) {
            return splitNames[1];
        }

        return "";
    }

    public String getFirstName() {
        Text name = getCustomName();
        String[] splitNames = Objects.requireNonNull(name).getString().split("\\s+");
        if (splitNames.length >= 2) {
            return splitNames[0];
        }

        return "";
    }

    public String getDebugName() {
        return getClass().getSimpleName() + getEntityId();
    }

    public ProfessionType getProfessionType() {
        return this.professionType;
    }

    protected boolean hasTavern() {
        return (hasVillage() && getVillage().hasStructure(VillageStructureType.TAVERN));
    }

    protected boolean wantsTavern() {
        if (getHappy() >= 100 || !hasTavern()) {
            return false;
        }
        if (!isWorkTime() && (
                getHappy() < 70 || this.wantsTavern)) {
            return true;
        }


      return getHappy() < 10;
    }

    protected void addTask(int priority, EntityAIBase task) {
        this.tasks.addTask(priority, task);
    }

    public boolean isDeliveryTime() {
        return isWorkTime();
    }

    protected void setupServerJobs() {
        super.setupServerJobs();

        addJob(new TickJob(60, 120, true, new CleanUpRunnable(this)));
        addJob(new TickJob(23900, 200, true, new GoalRandomizerRunnable(this)));


        addJob(new TickJob(80, 10, true, () -> {
            if (isStarving()) {
                attackEntityFrom(DamageSource.STARVE, 1.0F);
            }
        }));


        addJob(new TickJob(30, 30, true, this::scanForEnemies));

        addJob(new TickJob(100, 200, true, this::fixOffGraph));

        addJob(new TickJob(200, 300, true, this::dayCheck));

        addJob(new TickJob(200, 100, true, this::crowdingCheck));


        addJob(new TickJob(40, 100, true, new BedCheckRunnable(this)));


        addJob(new TickJob(20, 30, false, () -> {
            VillageManager vm = VillageManager.get(this.world);
            vm.addScanBox((new AxisAlignedBB(getPosition())).grow(64.0D));
        }));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(MAX_HUNGER);
        getAttributeMap().registerAttribute(MAX_HAPPY);
        getAttributeMap().registerAttribute(MAX_INTELLIGENCE);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(MAX_HUNGER).setBaseValue(100.0D);
        getEntityAttribute(MAX_HAPPY).setBaseValue(100.0D);
        getEntityAttribute(MAX_INTELLIGENCE).setBaseValue(100.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

        this.dataManager.set(HUNGER, getMaxHunger());
        this.dataManager.set(HAPPY, getMaxHappy());
        this.dataManager.set(INTELLIGENCE, getMaxIntelligence());
        this.dataManager.set(FORCE_AXIS, -1);
        this.dataManager.set(SLEEPING, Boolean.FALSE);
        this.dataManager.set(SITTING, Boolean.FALSE);
        this.dataManager.set(ACTION_ITEM, ItemStack.EMPTY);
        this.dataManager.set(MOVEMENT_MODE, MovementMode.WALK.id);
        this.dataManager.set(BLESSED, 0);


        for (DataParameter<Integer> skill : SKILLS.values()) {
            this.dataManager.set(skill, 0);
        }

        for (DataParameter<Boolean> aiFilter : this.aiFilters.values()) {
            this.dataManager.set(aiFilter, Boolean.TRUE);
        }
    }

    protected void entityInit() {
        this.aiFilters = new HashMap();
        registerAIFilter("read_book", READ_BOOK);
        registerAIFilter("visit_tavern", VISIT_TAVERN);

        this.dataTracker.startTracking(HUNGER, 0);
        this.dataTracker.startTracking(HAPPY, 0);
        this.dataTracker.startTracking(INTELLIGENCE, 1);
        this.dataTracker.startTracking(FORCE_AXIS, -1);
        this.dataTracker.startTracking(SLEEPING, Boolean.FALSE);
        this.dataTracker.startTracking(SITTING, Boolean.FALSE);
        this.dataTracker.startTracking(ACTION_ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(MOVEMENT_MODE, (byte) 0);
        this.dataTracker.startTracking(BLESSED, 0);

        for (DataParameter<Integer> skill : SKILLS.values()) {
            this.dataTracker.startTracking(skill, 0);
        }

        super.entityInit();
        onStopSit();
    }

    public boolean isAITick(String aiFilter) {
        return (isAITick() && isAIFilterEnabled(aiFilter));
    }

    protected boolean villageHasStorageCount(Predicate<ItemStack> pred, int count) {
        if (hasVillage()) {
            return (getVillage().getStorageCount(pred) >= count);
        }

        return false;
    }

    protected void crowdingCheck() {
        if (!isSleeping()) {
            VillageStructure struct = getCurrentStructure();
            if (struct != this.lastCrowdCheck) {
                this.lastCrowdCheck = struct;
                if (struct != null) {
                    float crowdFactor = struct.getCrowdedFactor();
                    if (crowdFactor > 0.0F) {
                        int CROWD_PENALTY = -5;
                        setThought(VillagerThought.CROWDED);
                        int penalty = (int) (-5.0F * crowdFactor);
                        modifyHappy(penalty);
                        debugOut("Crowding penalty [" + penalty + "] in " + struct.type.name() + " at " + getPosition());
                    }
                }
            }
        }
    }

    public int getDaysAlive() {
        return this.daysAlive;
    }

    protected void dayCheck() {
        int curTime = (int) Village.getTimeOfDay(this.world);
        if (curTime < this.dayCheckTime) {
            this.dayCheckTime = curTime;
            this.daysAlive++;
            onNewDay();
        } else {

            this.dayCheckTime = curTime;
        }
    }

    protected void onNewDay() {
        randomizeGoals();
    }

    protected void fixOffGraph() {
        if (!hasVillage()) {
            Village v = VillageManager.get(this.world).getNearestVillage(getPosition(), 130);
            if (v != null) {

                BlockPos bestPos = null;
                double bestDist = Double.MAX_VALUE;
                for (BlockPos testPos : BlockPos.iterate(getBlockPos().add(-3, -3, -3), getBlockPos().add(3, 3, 3))) {
                    if (v.getPathingGraph().isInGraph(testPos)) {
                        double dist = getBlockPos().distanceSq(testPos);
                        if (bestPos == null || dist < bestDist) {
                            bestPos = testPos;
                            bestDist = dist;
                        }
                    }
                }

                if (bestPos != null) {
                    setPositionAndUpdate(bestPos.getX(), bestPos.getY(), bestPos.getZ());
                }
            }
        }
    }

    protected void scanForEnemies() {
        if (!isRole(VillagerRole.VENDOR) && !isRole(VillagerRole.VISITOR)) {
            ListIterator<EntityLiving> itr = this.world.getEntitiesWithinAABB(EntityLiving.class, getEntityBoundingBox().grow(30.0D, 6.0D, 30.0D), isEnemy()).listIterator();
            while (itr.hasNext()) {
                EntityLiving enemy = itr.next();
                if (canEntityBeSeen(enemy) && hasVillage()) {
                    getVillage().addOrRenewEnemy(enemy, 1);
                }
                IAttributeInstance attribute = enemy.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
                if (attribute != null) {
                    double distMe = enemy.getDistanceSq(this);
                    double enemyRange = attribute.getAttributeValue() * attribute.getAttributeValue();

                    if (distMe < enemyRange) {

                        if (enemy.getAttackTarget() == null) {
                            enemy.setAttackTarget(this);
                            continue;
                        }
                        double distTarget = enemy.getDistanceSq(enemy.getAttackTarget());

                        if (distMe < distTarget) {
                            enemy.setAttackTarget(this);
                            continue;
                        }
                        if (hasVillage()) {
                            boolean meIndoors = getVillage().isInStructure(getPosition());
                            boolean currentTargetIndoors = getVillage().isInStructure(enemy.getAttackTarget().getPosition());

                            if (!meIndoors && currentTargetIndoors) {
                                enemy.setAttackTarget(this);
                            }
                        }
                    }
                }
            }
        }
    }

    public void damageArmor(float damage) {
        if (damage < 1.0F) {
            damage = 1.0F;
        }

        int finalDmg = (int) damage;
        getArmorItems().forEach(a -> {
            if (a.getItem() instanceof net.minecraft.item.ArmorItem && getRand().nextBoolean()) {
                damageItem(a, finalDmg);
            }
        });
    }

    public void damageItem(ItemStack itemStack, int amount) {
        int itemDamage = ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER) ? amount : (amount * 5);

        ItemStack oldItem = null;
        if (itemStack.getItemDamage() + itemDamage >= itemStack.getMaxDamage()) {
            oldItem = itemStack.copy();
        }
        itemStack.damageItem(itemDamage, this);

        if (itemStack.isEmpty() && oldItem != null) {
            onInventoryUpdated(oldItem);
        }
    }

    @Nullable
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        return null;
    }

    public boolean canBePushed() {
        if (isSitting() || isSleeping()) {
            return false;
        }
        return super.canBePushed();
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!isSitting()) {
            if (entityIn instanceof EntityVillagerTek &&
                    isDoorNearby(1, 1)) {
                return;
            }

            super.collideWithEntity(entityIn);
        }
    }

    protected void collideWithNearbyEntities() {
        if (!isSitting() && !isSleeping())
            super.collideWithNearbyEntities();
    }

    protected boolean isDoorNearby(int xx, int zz) {
        for (int x = -xx; x <= xx; x++) {
            for (int z = -zz; z <= zz; z++) {
                BlockPos bp = getPosition().east(x).north(z);
                if (VillageStructure.isWoodDoor(this.world, bp) || VillageStructure.isGate(this.world, bp)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected SoundEvent getAmbientSound() {
        if (isSleeping() &&
                this.world.rand.nextInt(2) == 0) {
            return ModSoundEvents.villagerSleep;
        }
        return null;
    }

    protected void bedCheck() {
        if (hasVillage()) {
            if (getBedPos() != null && this.homeFrame != null) {
                VillageStructure struct = this.village.getStructureFromFrame(this.homeFrame);
                if (struct != null && struct instanceof VillageStructureHome) {
                    VillageStructureHome home = (VillageStructureHome) struct;
                    if (!home.canVillagerSleep(this)) {
                        clearHome();
                    }
                }
            }

            if (getBedPos() == null) {
                if (this.homeFrame != null) {

                    VillageStructure struct = this.village.getStructureFromFrame(this.homeFrame);
                    if (struct != null && struct instanceof VillageStructureHome) {
                        VillageStructureHome home = (VillageStructureHome) struct;
                        if (home.canVillagerSleep(this) && !home.isFull()) {
                            this.homeFrame = null;
                            home.addResident(this);
                        } else {
                            clearHome();
                        }
                    } else if (this.ticksExisted > 200) {

                        clearHome();
                    }
                } else {
                    VillageStructureHome home = this.village.getAvailableHome(this);
                    if (home != null) {
                        home.addResident(this);
                    }
                }
            }
        }
    }

    public VillageStructure getCurrentStructure() {
        if (hasVillage()) {
            return getVillage().getStructure(getPosition());
        }
        return null;
    }

    public void attachToVillage(Village v) {
        super.attachToVillage(v);

        this.sleepOffset = genOffset(400);

        if (isRole(VillagerRole.VILLAGER)) {
            v.addResident(this);
        }

        if (getIntelligence() < 1) {
            setIntelligence(1);
        }
    }

    protected void detachVillage() {
        if (hasVillage()) {
            this.village.removeResident(this);
        }
        super.detachVillage();
    }

    protected int genOffset(int range) {
        return getRNG().nextInt(range) - range / 2;
    }

    protected void randomizeGoals() {
        if (getRNG().nextInt(100) < 28 && getIntelligence() < getMaxIntelligence()) {
            this.wantsLearning = getRNG().nextInt(6) + 3;
        } else {
            this.wantsLearning = 0;
        }

        this.wantsTavern = (getRNG().nextInt(10) < 3);
    }

    protected void cleanUpInventory() {
    }

    protected void onNewPotionEffect(StatusEffect effect) {
        if (effect.getType() == ModPotions.potionBless) {
            this.dataTracker.set(BLESSED, Integer.valueOf(effect.getAmplifier()));
        }

        super.onNewPotionEffect(effect);
    }

    protected void onFinishedPotionEffect(StatusEffect effect) {
        if (effect.getPotion() == ModPotions.potionBless) {
            this.dataManager.set(BLESSED, Integer.valueOf(0));
        }

        super.onFinishedPotionEffect(effect);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (getLeashed() && getRNG().nextInt((getHappy() < 50) ? 30 : 50) == 0) {
            modifyHappy(-1);
            if (getHappy() <= 0) {
                clearLeashed(true, true);
            }
        }

        if (!isWorldRemote()) {
            this.lastSadTick++;

            if (this.nextThought != null && this.ticksExisted % 80 == 0) {
                TekVillager.NETWORK.sendToAllAround(this.nextThought, new NetworkRegistry.TargetPoint(getDimension(), this.posX, this.posY, this.posZ, 64.0D));
                this.nextThought = null;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void startWalking() {
        MovementMode mode = getMovementMode();
        if (mode != this.lastMovementMode) {
            if (this.lastMovementMode != null) {
                stopWalking();
            }

            this.lastMovementMode = mode;

            if (mode != null) {
                playClientAnimation(mode.anim);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    protected void stopWalking() {
        if (this.lastMovementMode != null) {
            stopClientAnimation(this.lastMovementMode.anim);
            this.lastMovementMode = null;
        }
    }

    public BlockPos getBedPos() {
        return this.bedPos;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, 1, teleport);
    }

    public void setHome(BlockPos bedPos, BlockPos homeFrame) {
        this.bedPos = bedPos;
        this.homeFrame = homeFrame;
    }

    public void clearHome() {
        this.bedPos = null;
        this.homeFrame = null;
    }

    public boolean hasHome() {
        return (hasVillage() && this.homeFrame != null);
    }

    public VillageStructureHome getHome() {
        if (hasHome()) {
            return (VillageStructureHome) this.village.getStructureFromFrame(this.homeFrame);
        }


        return null;
    }

    public void addVillagerPosition() {
        if (hasVillage()) {
            getVillage().addVillagerPosition(this);
        }
    }

    public final int getMaxHunger() {
        return (int) getEntityAttribute(MAX_HUNGER).getAttributeValue();
    }

    public int getHunger() {
        return ((Integer) this.dataManager.get(HUNGER)).intValue();
    }

    public void setHunger(int hunger) {
        if (isRole(VillagerRole.VILLAGER)) {
            this.dataManager.set(HUNGER, Integer.valueOf(MathHelper.clamp(hunger, 0, getMaxHunger())));
            if (hunger < 0 && isHungry()) {
                setThought(VillagerThought.HUNGRY);
            }
        }
    }

    public final int getMaxHappy() {
        return (int) getEntityAttribute(MAX_HAPPY).getAttributeValue();
    }

    public int getHappy() {
        return ((Integer) this.dataManager.get(HAPPY)).intValue();
    }

    public int setHappy(int happy) {
        int prevHappy = getHappy();
        this.dataManager.set(HAPPY, Integer.valueOf(MathHelper.clamp(happy, 0, getMaxHappy())));
        return getHappy() - prevHappy;
    }

    public final int getMaxIntelligence() {
        return (int) getAttributeInstance(MAX_INTELLIGENCE).getAttributeValue();
    }

    public int getIntelligence() {
        return Math.max(this.dataTracker.get(INTELLIGENCE), 1);
    }

    public void setIntelligence(int intel) {
        this.dataTracker.set(INTELLIGENCE, MathHelper.clamp(intel, 1, getMaxIntelligence()));
    }

    public int getBlessed() {
        return this.dataTracker.get(BLESSED);
    }

    public int getSkill(ProfessionType pt) {
        int skill = getBaseSkill(pt);

        if (pt == getProfessionType()) {
            skill += getBlessed();
        }
        return Math.min(skill, 100);
    }

    public int getBaseSkill(ProfessionType pt) {
        int skill = ((Integer) this.dataManager.get((DataParameter) SKILLS.get(pt))).intValue();
        return Math.min(skill, 100);
    }

    public int getSkillLerp(ProfessionType pt, int min, int max) {
        if (min < max) {
            return (int) MathHelper.clampedLerp(min, max, getSkill(pt) / 100.0D);
        }
        return (int) MathHelper.clampedLerp(max, min, (100.0D - getSkill(pt)) / 100.0D);
    }

    public void setSkill(ProfessionType pt, int val) {
        debugOut("Skill Change - " + pt.name + " --> " + val);
        this.dataManager.set((DataParameter) SKILLS.get(pt), Integer.valueOf(MathHelper.clamp(val, 0, 100)));
    }

    public int getForceAxis() {
        return this.dataTracker.get(FORCE_AXIS);
    }

    public void setForceAxis(int axes) {
        this.dataTracker.set(FORCE_AXIS, axes);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    protected void setSleeping(boolean sleep) {
        this.dataTracker.set(SLEEPING, Boolean.valueOf(sleep));
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSitting(boolean sit) {
        this.dataTracker.set(SITTING, sit);
    }

    public MovementMode getMovementMode() {
        return MovementMode.valueOf(String.valueOf(this.dataTracker.get(MOVEMENT_MODE).byteValue()));
    }


    public void setMovementMode(MovementMode mode) {
        this.dataTracker.set(MOVEMENT_MODE, mode.id);
    }

    public boolean isHungry() {
        return (getHunger() < 30);
    }

    public boolean isStarving() {
        return (getHunger() <= 0);
    }

    public boolean shouldSleep() {
        return (isSleepingTime() || (!isRole(VillagerRole.DEFENDER) && getHealth() < getHealthMaximum() * 0.5F));
    }

    public boolean isSleepingTime() {
      return Village.isTimeOfDay(this.world, (SLEEP_START_TIME + this.sleepOffset + (wantsTavern() ? 4000 : 0)), (SLEEP_END_TIME + this.sleepOffset));
    }

    public boolean isWorkTime() {
        return (Village.isTimeOfDay(this.world, WORK_START_TIME, WORK_END_TIME, this.sleepOffset) && !this.world.isRaining());
    }

    public boolean isLearningTime() {
        if (this.wantsLearning > 0 && Village.isTimeOfDay(this.world, (this.sleepOffset + 2500), (this.sleepOffset + 8000))) {
            return isAIFilterEnabled("read_book");
        }

        return false;
    }

    public void addIntelligence(int delta) {
        if (delta > 0 && getIntelligence() < 100) {

            if (getRNG().nextInt(100) * 2 > getIntelligence()) {
                setIntelligence(getIntelligence() + delta);
                setItemThought(Items.BOOK);
                this.wantsLearning--;
            }
        }
    }

    public void addIntelligenceDelay(int delta, int ticks) {
        addJob(new TickJob(ticks, 0, false, () -> addIntelligence(delta)));
    }

    public void incrementSkill(ProfessionType pt) {
        setSkill(pt, getBaseSkill(pt) + 1);
        setItemThought(ModItems.getProfessionToken(pt));
        skillUpdated(pt);


        if (!isChild()) {
            List<EntityChild> children = this.world.getEntitiesWithinAABB(EntityChild.class, getEntityBoundingBox().grow(12.0D, 8.0D, 12.0D));
            if (!children.isEmpty()) {
                children.forEach(c -> {
                    boolean proximityLearn = true;
                    if (c.hasVillage()) {
                        VillageStructure struct = c.getVillage().getStructure(c.getPosition());
                        if (struct != null && struct instanceof net.tangotek.tektopia.structures.VillageStructureSchool) {
                            proximityLearn = false;
                        }
                    }

                    if (proximityLearn && c.getSkill(pt) < getSkill(pt) / 2) {
                        int chance = Math.max(c.getBaseSkill(pt) / 2, 1);
                        if (c.getRNG().nextInt(chance) == 0) {
                            c.incrementSkill(pt);
                        }
                    }
                });
            }
        }
    }

    public int getIdle() {
        return this.idle;
    }

    public void setIdle(int idle) {
        this.idle = idle;
    }

    protected void skillUpdated(ProfessionType pt) {
    }

    public void tryAddSkill(ProfessionType pt, int chance) {
        if (!this.world.isRemote) {
            double skill = getBaseSkill(pt);
            if (skill < 100.0D) {
                double intel = Math.max(getIntelligence(), 5.0D);

                double gapMod = 1.0D / Math.pow(skill / intel, 2.0D);


                double intMod = 0.2D;

                double intCheck = Math.min(gapMod * 0.2D, 1.0D);


                int rate = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getGameRules().getInt("villagerSkillRate");

                double roll = this.world.rand.nextDouble();
                double rollModded = roll * rate / 100.0D;
                if (rollModded <= intCheck &&
                        this.world.rand.nextInt(chance) == 0) {
                    incrementSkill(pt);
                }
            }
        }
    }

    public void modifyHungerDelay(int delta, int ticks) {
        addJob(new TickJob(ticks, 0, false, () -> modifyHunger(delta)));
    }

    public void modifyHunger(int delta) {
        if (delta < 0 && hasVillage() && !getVillage().hasStructure(VillageStructureType.STORAGE)) {
            return;
        }
        setHunger(getHunger() + delta);
    }

    public void modifyHappyDelay(int delta, int ticks) {
        addJob(new TickJob(ticks, 0, false, () -> modifyHappy(delta)));
    }

    public void modifyHappy(int delta) {
        if (delta > 0) {
            this.world.setEntityState(this, (byte) 14);

            if (getRNG().nextInt(6) == 0 && !isSleeping()) {
                playSound(ModSoundEvents.villagerHappy);
            }
        } else if (delta < 0) {
            this.world.setEntityState(this, (byte) 13);
            if (getRNG().nextInt(2) == 0 && !isSleeping()) {
                playSound(ModSoundEvents.villagerAngry);
            }
        }
        int realChange = setHappy(getHappy() + delta);

        if (hasVillage() && realChange != 0)
            getVillage().trackHappy(realChange);
    }

    public void throttledSadness(int delta) {
        if (this.lastSadTick > this.lastSadThrottle && getRNG().nextBoolean()) {
            addJob(new TickJob(20, 60, false, () -> modifyHappy(delta)));
            this.lastSadTick = 0;
            this.lastSadThrottle = 50 + getRNG().nextInt(100);
        }
    }

    public void cheerBeer(int happy) {
        int offset = getRNG().nextInt(25);
        if (!isPlayingAnimation("villager_sit_cheer")) {
            if (isSitting()) {
                addJob(new TickJob(offset, 0, false, () -> playServerAnimation("villager_sit_cheer")));
                addJob(new TickJob(8 + offset, 0, false, () -> equipActionItem(new ItemStack(ModItems.beer))));
                addJob(new TickJob(52 + offset, 0, false, () -> unequipActionItem()));
                addJob(new TickJob(58 + offset, 0, false, () -> {
                    if (isSitting()) {
                        playServerAnimation("villager_sit");
                    }
                }));
            }
            addJob(new TickJob(10 + offset * 2, 0, false, () -> playSound(ModSoundEvents.villagerHappy, 1.2F, getRNG().nextFloat() * 0.4F + 0.8F)));
            addJob(new TickJob(40, 0, false, () -> modifyHappy(happy)));
        }
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);


        if (!this.curAnim.isEmpty()) {
            playServerAnimation(this.curAnim);
        }
    }

    public float getAIMoveSpeed() {
        float baseSpeed = 0.45F * (getMovementMode()).speedMult;

        float modifiedSpeed = baseSpeed;

        int blessed = getBlessed();
        if (blessed > 0) {
            modifiedSpeed = (float) (modifiedSpeed * (1.05D + blessed * 0.002D));
        }
        return modifiedSpeed;
    }

    public EntityAnimal getLeadAnimal() {
        return this.leadAnimal;
    }

    public void setLeadAnimal(EntityAnimal animal) {
        this.leadAnimal = animal;
    }

    protected boolean canDespawn() {
        return false;
    }

    public VillagerInventory getInventory() {
        return this.villagerInventory;
    }

    public ItemDesireSet getDesireSet() {
        return this.desireSet;
    }

    public void onStorageChange(ItemStack storageItem) {
        this.desireSet.onStorageUpdated(this, storageItem);
    }

    public void onInventoryUpdated(ItemStack updatedItem) {
        this.desireSet.onInventoryUpdated(this, updatedItem);
    }

    protected boolean canVillagerPickupItem(ItemStack itemIn) {
        return false;
    }

    public boolean hasNoGravity() {
        return (isSitting() || super.hasNoGravity());
    }

    public double getSitOffset() {
        return 0.0D;
    }

    public void onStartSit(int sitAxis) {
        setForceAxis(sitAxis);
        setSitting(true);
        equipActionItem(ModItems.EMPTY_HAND_ITEM);
        if (!this.curAnim.isEmpty() && !this.curAnim.equals("villager_sit")) {
            stopServerAnimation(this.curAnim);
        }
        playServerAnimation("villager_sit");
    }

    public void onStopSit() {
        setSitting(false);
        setForceAxis(-1);
        stopServerAnimation("villager_sit");
        setNoGravity(false);
        unequipActionItem(ModItems.EMPTY_HAND_ITEM);
    }

    public void onStartSleep(int sleepAxis) {
        setForceAxis(sleepAxis);
        setSleeping(true);
        if (!this.curAnim.isEmpty() && this.curAnim != "villager_sleep") {
            stopServerAnimation(this.curAnim);
        }
        playServerAnimation("villager_sleep");
    }

    public void onStopSleep() {
        if (isSleeping() &&
                !isSleepingTime()) {


            checkSpawnHeart();

            modifyHappy(this.rand.nextInt(20) + 10);
        }


        setForceAxis(-1);
        setSleeping(false);
        stopServerAnimation("villager_sleep");
    }

    private void checkSpawnHeart() {
        int MIN_HAPPY = (int) (getMaxHappy() * 0.7F);
        if (hasVillage() && getHappy() >= MIN_HAPPY) {

            float happyFactor = (getMaxHappy() - getHappy()) / (getMaxHappy() - MIN_HAPPY);
            int CONSTANT_DIFFICULTY = 15;
            int chance = 15 + this.village.getResidentCount() + (int) (this.village.getResidentCount() * happyFactor * 2.0F);
            if (getRNG().nextInt(chance) == 0 &&
                    hasVillage() && !isChild() && getProfessionType() != ProfessionType.NITWIT) {
                IVillageData vd = getVillage().getTownData();
                if (vd != null && this.bedPos != null &&
                        vd.isChildReady(this.world.getTotalWorldTime())) {
                    VillageStructure struct = getVillagerHome().apply(this);
                    if (struct != null && struct.type.isHome()) {
                        VillageStructureHome home = (VillageStructureHome) struct;
                        vd.childSpawned(this.world);
                        addJob(new TickJob(100, 0, false, () -> {
                            ItemStack itemHeart = ModItems.createTaggedItem(ModItems.heart, ItemTagType.VILLAGER);
                            itemHeart.getSubCompound("village").setUniqueId("parent", getUniqueID());
                            EntityItem heartEntity = new EntityItem(this.world, this.bedPos.getX(), (this.bedPos.getY() + 1), this.bedPos.getZ(), itemHeart);
                            this.world.spawnEntity(heartEntity);
                        }));
                    }
                }
            }
        }
    }

    public MovementMode getDefaultMovement() {
        if (getHappy() < getMaxHappy() / 5) {
            return MovementMode.SULK;
        }
        return MovementMode.WALK;
    }

    public void updateMovement(boolean arrived) {
        if (this.world.rand.nextInt(50) == 0) {
            modifyHunger(-1);
        }
        if (!arrived) {

            if (hasVillage() && getRNG().nextInt(20) == 0) {
                addVillagerPosition();
            }
        }
    }

    public void resetMovement() {
        super.resetMovement();
    }

    protected ItemStack modifyPickUpStack(ItemStack itemStack) {
        return itemStack;
    }

    public void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack itemStack = itemEntity.getItem();

        if (canVillagerPickupItem(itemStack) && ModItems.canVillagerSee(itemStack)) {

            ItemStack modStack = modifyPickUpStack(itemStack.copy());
            if (!modStack.isEmpty()) {
                ItemStack leftOverStack = this.villagerInventory.addItem(modStack);

                if (leftOverStack.isEmpty()) {
                    itemEntity.setDead();
                } else {
                    int actuallyTaken = modStack.getCount() - leftOverStack.getCount();
                    int newCount = itemStack.getCount() - actuallyTaken;
                    if (newCount <= 0) {
                        itemEntity.setDead();
                    } else {
                        itemStack.setCount(newCount);
                    }
                }
            }
        }
    }

    public boolean isEntityInvulnerable(DamageSource source) {
        if (source == DamageSource.IN_WALL) {
            return true;
        }

        return super.isEntityInvulnerable(source);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        float beforeHealth = getHealth();
        if (super.attackEntityFrom(source, amount)) {
            float afterHealth = getHealth();

            float actualDamage = beforeHealth - afterHealth;
            if (actualDamage > 0.0F) {

                if (!isRole(VillagerRole.DEFENDER)) {
                    modifyHappy(-8);
                }
                if (hasVillage() && actualDamage > 0.0F) {
                    getVillage().reportVillagerDamage(this, source, actualDamage);
                }
                if (isSleeping()) {
                    onStopSleep();
                }
            }

            return true;
        }

        return false;
    }

    public void onDeath(DamageSource cause) {
        if (hasVillage() && getBedPos() != null) {
            int happyMod = -25;
            if (isChild()) {
                happyMod *= 2;
            }
            List<EntityVillagerTek> villagers = this.world.getEntitiesWithinAABB(EntityVillagerTek.class, getEntityBoundingBox().grow(200.0D), p -> (p != this));
            for (EntityVillagerTek v : villagers) {
                if (v.getVillage() == getVillage() && v.getBedPos() != null && !v.isSleeping()) {
                    v.modifyHappy(happyMod - getRNG().nextInt(15));
                }
            }
        }

        if (hasVillage() && isRole(VillagerRole.VILLAGER)) {
            getVillage().reportVillagerDeath(this, cause);
        }
        super.onDeath(cause);

        if (!this.world.isRemote)
            dropAllItems();
    }

    private void dropAllItems() {
        VillagerInventory villagerInventory1 = getInventory();
        for (int i = 0; i < villagerInventory1.getSizeInventory(); i++) {

            ItemStack itemStack = villagerInventory1.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                entityDropItem(itemStack, 0.5F);
            }
        }
        villagerInventory1.clear();
    }

    public void pickupItems(int grow) {
        for (EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(grow, 3.0D, grow))) {

            if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
                updateEquipmentIfNeeded(entityitem);
            }
        }
    }

    public void setThought(VillagerThought thought) {
        this.nextThought = new PacketVillagerThought(this, thought, thought.getScale());
    }

    public void setItemThought(Item item) {
        ModEntities.sendItemThought(this, item);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 12) {

            spawnParticles(EnumParticleTypes.HEART);
        } else if (id == 13) {

            spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
        } else if (id == 14) {

            spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
        } else {

            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles(EnumParticleTypes particleType) {
        for (int i = 0; i < 5; i++) {

            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(particleType, this.posX + (this.rand.nextFloat() * this.width * 2.0F) - this.width, this.posY + 1.0D + (this.rand.nextFloat() * this.height), this.posZ + (this.rand.nextFloat() * this.width * 2.0F) - this.width, d0, d1, d2, new int[0]);
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleThought(PacketVillagerThought msg) {
        ParticleThought part = new ParticleThought(this.world, Minecraft.getMinecraft().getTextureManager(), this, msg.getScale(), msg.getThought().getTex());
        (Minecraft.getMinecraft()).effectRenderer.addEffect(part);
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if (itemStack.getItem() == Items.NAME_TAG) {

            itemStack.interactWithEntity(player, this, hand);
            return true;
        }
        if (itemStack.getItem() instanceof ItemProfessionToken && hasVillage()) {
            ItemProfessionToken token = (ItemProfessionToken) itemStack.getItem();
            if (canConvertProfession(token.getProfessionType()) && (ModItems.isItemVillageBound(itemStack, getVillage()) || !ModItems.isItemVillageBound(itemStack)) &&
                    !this.world.isRemote) {
                itemStack.shrink(1);
                EntityVillagerTek villager = token.createVillager(this.world, this);
                if (villager != null) {
                    villager.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                    villager.onInitialSpawn(this.world.getDifficultyForLocation(getPosition()), null);
                    villager.cloneFrom(this);

                    this.world.spawnEntity(villager);

                    player.playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.0F, 1.0F);


                    return true;
                }

                return false;
            }
        }


        if (!this.world.isRemote) {

            player.openGui(TekVillager.instance, 0, this.world, getEntityId(), 0, 0);
            getNavigator().clearPath();
        }

        if (player.isSneaking()) {
            debugSpam();
        }

        return true;
    }

    protected void debugSpam() {
        debugOut("+ + + + + + + + + + + + + +");
        debugOut("Debug for " + getDebugName());
        getInventory().debugSpam();
        for (EntityAITasks.EntityAITaskEntry task : this.tasks.taskEntries) {
            if (task.using) {
                debugOut("    Active Task: " + task.action.getClass().getSimpleName());
            }
        }
        debugOut("Hunger: " + getHunger());
        debugOut("Happy: " + getHappy());
        debugOut("Health: " + getHealth());
        debugOut("Intelligence: " + getIntelligence());

        for (ProfessionType pt : ProfessionType.values()) {
            if (getBaseSkill(pt) > 0) {
                debugOut("     " + pt.name + ": " + getBaseSkill(pt));
            }
        }
        debugOut("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^");
    }

    protected void registerAIFilter(String filterName, DataParameter<Boolean> param) {
        if (this.aiFilters.put(filterName, param) != null) {
            debugOut("ERROR: registerAIFilter( " + filterName + " ).  Double registration");
        }
        this.dataManager.register(param, Boolean.valueOf(true));
    }

    protected void removeAIFilter(String filterName) {
        this.aiFilters.remove(filterName);
    }

    public List<String> getAIFilters() {
        return new ArrayList(this.aiFilters.keySet());
    }

    public boolean isAIFilterEnabled(String filterName) {
        DataParameter<Boolean> param = this.aiFilters.get(filterName);
        if (param != null) {
            return ((Boolean) this.dataManager.get(param)).booleanValue();
        }


        debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        debugOut("ERROR: (isAIFilterEnabled) AI Filter " + filterName + " does not exist!");
        return true;
    }

    public void setAIFilter(String filterName, boolean enabled) {
        DataParameter<Boolean> param = this.aiFilters.get(filterName);
        if (param != null) {
            debugOut("AI Filer " + filterName + " -> " + enabled);
            this.dataManager.set(param, Boolean.valueOf(enabled));
        } else {

            debugOut("ERROR: (setAIFilter) AI Filter " + filterName + " does not exist!");
        }
    }

    public boolean canConvertProfession(ProfessionType pt) {
        return (pt != this.professionType && pt != ProfessionType.CAPTAIN);
    }

    public void setRevengeTarget(@Nullable EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            return;
        }
        super.setRevengeTarget(target);

        if (hasVillage() && target != null) {
            getVillage().addOrRenewEnemy(target, 5);

            if (isEntityAlive()) {
                this.world.setEntityState(this, (byte) 14);
            }
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (MOVEMENT_MODE.equals(key)) {
            if (isWalking()) {
                startWalking();
            }
        }
    }

    public void equipActionItem(ItemStack toolItem) {
        this.dataManager.set(ACTION_ITEM, toolItem.copy());
    }

    public ItemStack getActionItem() {
        return (ItemStack) this.dataManager.get(ACTION_ITEM);
    }

    public void unequipActionItem() {
        this.dataTracker.set(ACTION_ITEM, ItemStack.EMPTY);
    }

    public void unequipActionItem(ItemStack actionItem) {
        if (actionItem != null && actionItem.getItem() == getActionItem().getItem()) {
            this.dataTracker.set(ACTION_ITEM, ItemStack.EMPTY);
        }
    }

    public Predicate<Entity> isEnemy() {
        return e -> (isHostile().test(e) || (e instanceof EntityLivingBase && EntityNecromancer.isMinion((EntityLivingBase) e)));
    }

    public Predicate<Entity> isHostile() {
        return e -> ((e instanceof net.minecraft.entity.monster.EntityZombie && !(e instanceof net.minecraft.entity.monster.EntityPigZombie)) || e instanceof net.minecraft.entity.monster.EntityWitherSkeleton || e instanceof net.minecraft.entity.monster.EntityEvoker || e instanceof net.minecraft.entity.monster.EntityVex || e instanceof net.minecraft.entity.monster.EntityVindicator || e instanceof EntityNecromancer);
    }

    public boolean isFleeFrom(Entity e) {
        return isHostile().test(e);
    }

    public void addRecentEat(Item item) {
        this.recentEats.add(Integer.valueOf(Item.getIdFromItem(item)));
        while (this.recentEats.size() > 5) {
            this.recentEats.remove();
        }
    }

    public int getRecentEatModifier(Item item) {
        int itemId = Item.getIdFromItem(item);
        int eatCount = MathHelper.clamp((int) this.recentEats.stream().filter(i -> (i.intValue() == itemId)).count(), 0, 5);
        return recentEatPenalties[eatCount];
    }

    public boolean isVillageMember(Village v) {
        return (getVillage() == v && getBedPos() != null);
    }

    public boolean isMale() {
        return (getUniqueID().getLeastSignificantBits() % 2L == 0L);
    }

    public Predicate<ItemStack> isHarvestItem() {
        return p -> false;
    }

    public void equipBestGear() {
    }

    public void equipBestGear(EntityEquipmentSlot slot, Function<ItemStack, Integer> bestFunc) {
        ItemStack bestItem = getItemStackFromSlot(slot);
        int bestScore = bestFunc.apply(bestItem).intValue();
        int swapSlot = -1;

        for (int i = 0; i < getInventory().getSizeInventory(); i++) {
            ItemStack itemStack = getInventory().getStackInSlot(i);
            int thisScore = bestFunc.apply(itemStack).intValue();
            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestItem = itemStack;
                swapSlot = i;
            }
        }

        if (swapSlot >= 0) {
            ItemStack oldGear = getItemStackFromSlot(slot);
            setItemStackToSlot(slot, bestItem);
            getInventory().setInventorySlotContents(swapSlot, oldGear);
            debugOut("Equipping new gear: " + bestItem + "   Removing old gear: " + oldGear);
        } else if (bestScore == -1) {
            ItemStack oldGear = getItemStackFromSlot(slot);
            if (!oldGear.isEmpty()) {
                setItemStackToSlot(slot, ItemStack.EMPTY);
                getInventory().addItem(oldGear);
            }
        }
    }

    protected void cloneFrom(EntityVillagerTek source) {
        setCustomNameTag(source.getCustomNameTag());


        while (source.isMale() != isMale()) {
            setUniqueId(UUID.randomUUID());
        }

        setHappy(source.getHappy());
        setIntelligence(source.getIntelligence());
        setHunger(source.getHunger());

        this.villagerInventory.mergeItems(source.villagerInventory);
        this.bedPos = source.bedPos;
        this.homeFrame = source.homeFrame;
        this.daysAlive = source.daysAlive;
        this.dayCheckTime = source.dayCheckTime;


        source.applySkillsTo(this);

        getInventory().cloneFrom(source.getInventory());


        source.setDead();
    }

    protected void applySkillsTo(EntityVillagerTek target) {
        for (ProfessionType pt : SKILLS.keySet()) {
            int skill = getBaseSkill(pt);
            if (skill > target.getBaseSkill(pt) && pt.canCopy) {
                target.setSkill(pt, skill);
            }
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        compound.setInteger("happy", getHappy());
        compound.setInteger("hunger", getHunger());
        compound.setInteger("intelligence", getIntelligence());
        compound.setInteger("daysAlive", this.daysAlive);
        compound.setInteger("dayCheckTime", this.dayCheckTime);

        for (ProfessionType pt : SKILLS.keySet()) {
            if (pt.canCopy) {
                compound.setInteger(pt.name, getBaseSkill(pt));
            }
        }
        for (String filterName : this.aiFilters.keySet()) {
            compound.setBoolean("ai_" + filterName, isAIFilterEnabled(filterName));
        }

        compound.setBoolean("hasHome", (this.homeFrame != null));
        if (this.homeFrame != null) {
            writeBlockPosNBT(compound, "homeFrame", this.homeFrame);
        }
        compound.setIntArray("recentEats", this.recentEats.stream().mapToInt(i -> i.intValue()).toArray());
        this.villagerInventory.writeNBT(compound);
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        setHappy(compound.getInteger("happy"));
        setHunger(compound.getInteger("hunger"));
        setIntelligence(compound.getInteger("intelligence"));
        this.daysAlive = compound.getInteger("daysAlive");
        this.dayCheckTime = compound.getInteger("dayCheckTime");

        for (ProfessionType pt : SKILLS.keySet()) {
            if (pt.canCopy) {
                setSkill(pt, compound.getInteger(pt.name));
            }
        }
        for (String filterName : this.aiFilters.keySet()) {
            String key = "ai_" + filterName;
            if (compound.hasKey(key)) {
                setAIFilter(filterName, compound.getBoolean("ai_" + filterName));
                continue;
            }
            setAIFilter(filterName, true);
        }

        boolean hasHome = compound.getBoolean("hasHome");
        if (hasHome) {
            this.homeFrame = readBlockPosNBT(compound, "homeFrame");
        }
        this.recentEats.clear();
        int[] eats = compound.getIntArray("recentEats");
        Arrays.stream(eats).forEach(i -> this.recentEats.add(Integer.valueOf(i)));

        setNoGravity(false);

        this.villagerInventory.readNBT(compound);
        getDesireSet().forceUpdate();
    }

    protected BlockPos readBlockPosNBT(NBTTagCompound compound, String key) {
        NBTTagList nbttaglist = compound.getTagList(key, 6);
        return new BlockPos(nbttaglist.getDoubleAt(0), nbttaglist.getDoubleAt(1), nbttaglist.getDoubleAt(2));
    }

    protected void writeBlockPosNBT(NBTTagCompound compound, String key, BlockPos val) {
        compound.setTag(key, newDoubleNBTList(new double[]{val.getX(), val.getY(), val.getZ()}));
    }


    public enum MovementMode {
        WALK((byte) 1, 1.0F, "villager_walk"),
        SKIP((byte) 2, 1.1F, "villager_skip"),
        RUN((byte) 3, 1.4F, "villager_run"),
        SULK((byte) 4, 0.7F, "villager_walk_sad");
        public float speedMult;
        public byte id;
        public String anim;

        MovementMode(byte id, float mult, String anim) {
            this.speedMult = mult;
            this.id = id;
            this.anim = anim;
        }
    }


    public enum VillagerThought {
        BED(115, "red_bed.png"),
        HUNGRY(116, "food.png"),
        PICK(117, "iron_pick.png"),
        HOE(118, "iron_hoe.png"),
        AXE(119, "iron_axe.png"),
        SWORD(120, "iron_sword.png"),
        BOOKSHELF(121, "bookshelf.png"),
        PIG_FOOD(122, "pig_carrot.png"),
        SHEEP_FOOD(123, "sheep_wheat.png"),
        COW_FOOD(124, "cow_wheat.png"),
        CHICKEN_FOOD(125, "chicken_seeds.png"),
        BUCKET(126, "bucket.png"),
        SHEARS(127, "shears.png"),
        TAVERN(128, "structure_tavern.png"),
        NOTEBLOCK(129, "noteblock.png"),
        TEACHER(130, "prof_teacher.png"),
        TORCH(131, "torch.png"),
        INSOMNIA(132, "insomnia.png"),
        CROWDED(133, "crowded.png"),
        DO_NOT_USE(999, "meh.png");
        private int numVal;
        private String texture;

        VillagerThought(int val, String tex) {
            this.numVal = val;
            this.texture = tex;
        }

        public int getVal() {
            return this.numVal;
        }

        public String getTex() {
            return this.texture;
        }

        public float getScale() {
            return 1.0F;
        }
    }

    private static class CleanUpRunnable
            implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public CleanUpRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<>(v);
        }

        public void run() {
            if (this.villager.get() != null)
                Objects.requireNonNull(this.villager.get()).cleanUpInventory();
        }
    }

    private static class GoalRandomizerRunnable
            implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public GoalRandomizerRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<>(v);
        }

        public void run() {
            if (this.villager.get() != null)
                Objects.requireNonNull(this.villager.get()).randomizeGoals();
        }
    }

    private static class BedCheckRunnable
            implements Runnable {
        private final WeakReference<EntityVillagerTek> villager;

        public BedCheckRunnable(EntityVillagerTek v) {
            this.villager = new WeakReference<>(v);
        }

        public void run() {
            if (this.villager.get() != null)
                Objects.requireNonNull(this.villager.get()).bedCheck();
        }
    }
}
*/
