/*
package io.github.paradoxicalblock.storycraft;

import io.github.paradoxicalblock.storycraft.blockfinder.BlockFinder;
import io.github.paradoxicalblock.storycraft.blockfinder.SaplingScanner;
import io.github.paradoxicalblock.storycraft.blockfinder.SugarCaneScanner;
import io.github.paradoxicalblock.storycraft.blockfinder.TreeScanner;
import io.github.paradoxicalblock.storycraft.caps.IVillageData;
import io.github.paradoxicalblock.storycraft.entity.EntityVillagerTek;
import io.github.paradoxicalblock.storycraft.pathing.BasePathingNode;
import io.github.paradoxicalblock.storycraft.pathing.PathingGraph;
import io.github.paradoxicalblock.storycraft.structures.VillageStructure;
import io.github.paradoxicalblock.storycraft.structures.VillageStructureType;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import io.github.paradoxicalblock.storycraft.tickjob.TickJobQueue;
import net.minecraft.block.Block;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.world.BlockEvent;
import net.tangotek.tektopia.caps.VillageData;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.storage.InventoryScanner;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Village {
    public static final int VILLAGE_SIZE = 120;
    private static final VillageData emptyVillageData = new VillageData();
    protected Map<VillageStructureType, List<VillageStructure>> structures;
    protected BlockFinder blockFinder;
    protected FarmFinder farmFinder;
    protected MineshaftFinder mineFinder;
    protected PathingGraph pathingGraph;
    protected TickJobQueue jobs;
    protected MerchantScheduler merchantScheduler;
    protected NomadScheduler nomadScheduler;
    protected RaidScheduler raidScheduler;
    private ArrayDeque<BlockPos> villagerPositions;
    private long lastVillagerPosTime;
    private boolean isDestroyed;
    private World world;
    private List<ChestBlockEntity> storage;
    private Map<BlockPos, InventoryScanner> storageScanners;
    private BlockPos center;
    private BlockPos origin;
    private CapabilityDispatcher capabilities;
    private int cleanTick;
    private int tickCounter;
    private int guardSleepOffset;
    private int clericSleepOffset;
    private Box aabb;
    private List<VillageEnemy> enemies;
    private BlockPos enemySighting;
    private IVillageData villageData;
    private String villageName;

    private UUID villageUUID;
    private List<EntityVillagerTek> residents;
    private Set<EntityVillagerTek> activeDefenders;

    public Village(World worldIn, BlockPos origin) {
        this.structures = new HashMap();
        this.villagerPositions = new ArrayDeque();
        this.lastVillagerPosTime = 0L;
        this.isDestroyed = false;
        this.storage = new ArrayList();
        this.storageScanners = new HashMap();
        this.cleanTick = 0;
        this.tickCounter = 0;
        this.guardSleepOffset = 1000 - EntityVillagerTek.SLEEP_START_TIME;
        this.clericSleepOffset = 1000 - EntityVillagerTek.SLEEP_START_TIME;
        this.enemies = new ArrayList();
        this.enemySighting = null;
        this.jobs = new TickJobQueue();
        this.villageName = "";
        this.residents = new ArrayList();
        this.activeDefenders = new HashSet();
        this.capabilities = null;
        this.blockFinder = new BlockFinder();
        this.farmFinder = new FarmFinder(worldIn, this);
        this.mineFinder = new MineshaftFinder(worldIn, this);
        this.origin = origin;
        this.pathingGraph = new PathingGraph(worldIn, this);
        this.world = worldIn;
        this.merchantScheduler = new MerchantScheduler(worldIn, this);
        this.nomadScheduler = new NomadScheduler(worldIn, this);
        this.raidScheduler = new RaidScheduler(worldIn, this);
        this.cleanTick = this.world.random.nextInt(40) + 40;


        this.blockFinder.registerBlockScanner(new TreeScanner(this, 30));
        this.blockFinder.registerBlockScanner(new SugarCaneScanner(this, 15));
        this.blockFinder.registerBlockScanner(new SaplingScanner(this, 15));

        setupServerJobs();
    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(character));
        }
        return builder.toString();
    }

    private static long fixTime(long time) {
        while (time > 24000L) {
            time -= 24000L;
        }
        while (time < 0L) {
            time += 24000L;
        }
        return time;
    }

    public static boolean isTimeOfDay(World world, long startTime, long endTime, long offset) {
        startTime = fixTime(startTime + offset);
        endTime = fixTime(endTime + offset);
        long time = getTimeOfDay(world);
        if (endTime > startTime) {
            return (time >= startTime && time <= endTime);
        }
        return (time >= startTime || time <= endTime);
    }

    public static boolean isTimeOfDay(World world, long startTime, long endTime) {
        return isTimeOfDay(world, startTime, endTime, 0L);
    }

    public static long getTimeOfDay(World world) {
        return world.getWorldTime() % 24000L;
    }

    public static boolean isNightTime(World world) {
        long timeOfDay = getTimeOfDay(world);
        return (timeOfDay > 11500L || timeOfDay < 500L || timeOfDay < 200L || world.isRaining());
    }

    public void addJob(TickJob job) {
        this.jobs.addJob(job);
    }

    public void debugOut(String text) {
        System.out.println("[" + this.villageName + "] " + text);
    }

    protected void setupServerJobs() {
        addJob(new TickJob(100, 100, true, () -> this.merchantScheduler.update()));
        addJob(new TickJob(200, 100, true, () -> this.nomadScheduler.update()));

        addJob(new TickJob(10, 5, true, () -> this.enemySighting = null));
        addJob(new TickJob(4000, 2000, true, () -> {
            IVillageData vd = getTownData();
            if (vd != null) vd.getEconomy().refreshValues(this);
        }));
    }

    public ItemStack getVillageToken() {
        VillageStructure townHall = getNearestStructure(VillageStructureType.TOWNHALL, getOrigin());
        if (townHall != null) {
            ItemFrameEntity frame = townHall.getItemFrame();
            if (frame != null) {
                return frame.getHeldItemStack();
            }
        }
        return ItemStack.EMPTY;
    }

    public IVillageData getTownData() {
        if (this.origin != null) {
            VillageStructure townHall = getNearestStructure(VillageStructureType.TOWNHALL, this.origin);
            if (townHall != null) {
                this.villageData = townHall.getData();
                if (this.villageData != null) {
                    return this.villageData;
                }
            }
        }
        debugOut("Returning EMPTY village data");
        return emptyVillageData;
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    public World getWorld() {
        return this.world;
    }

    public PathingGraph getPathingGraph() {
        return this.pathingGraph;
    }

    public boolean isLoaded() {
        if (getOrigin() != null) {
            return this.world.isBlockLoaded(getOrigin());
        }
        return false;
    }

    private boolean canAddStructure(VillageStructure struct) {
        if (isStructureValid(struct)) {
            List<VillageStructure> structs = this.structures.get(struct.type);
            return structs == null || struct.getMaxAllowed() == 0 || structs.size() < struct.getMaxAllowed();
        }

        return false;
    }

    public boolean addStructure(VillageStructure struct) {
        if (canAddStructure(struct)) {
            List<VillageStructure> structList = this.structures.get(struct.type);
            if (structList == null) {
                structList = new ArrayList<VillageStructure>();
                this.structures.put(struct.type, structList);
            }

            if (!structList.contains(struct)) {
                if (structList.add(struct)) {


                    ItemFrameEntity frame = struct.getItemFrame();
                    if (frame != null && frame.getHeldItemStack() != null) {
                        ModItems.bindItemToVillage(struct.getItemFrame().getHeldItemStack(), this);
                    }
                    if (struct.type == VillageStructureType.TOWNHALL && this.pathingGraph.nodeCount() <= 0) {
                        this.pathingGraph.seedVillage(struct.getDoorOutside());
                        this.origin = struct.getDoorOutside();
                        addVillagerPosition(struct.getDoorOutside());
                        ItemStack villageToken = getVillageToken();
                        if (!villageToken.isEmpty() && villageToken.hasCustomName() && !villageToken.getName().getString().equals("Town Hall")) {
                            this.villageName = villageToken.getName().getString();
                        } else {
                            this.villageName = randomAlphaNumeric(3);
                        }
                        this.villageUUID = getTownData().getUUID();
                        debugOut("Village ADDED - [" + getName() + "]   " + this.villageUUID);
                    }

                    debugOut("Adding structure " + struct.type + " [" + struct.getDoor() + "]");

                    if (struct.adjustsVillageCenter()) {
                        updateCenter();
                    }

                    return true;
                }
            } else {
                debugOut("Tried adding structure that already existed - " + struct.type.name());
            }
        }

        return false;
    }

    public void forceRaid(int raidPoints) {
        this.raidScheduler.forceRaid(raidPoints);
    }

    public boolean canSleepAt(BlockPos pos) {
        if (VillageStructureHome.isBed(this.world, pos)) {
            List<VillageStructure> homes = getHomes();
            for (VillageStructure struct : homes) {
                VillageStructureHome home = (VillageStructureHome) struct;
                if (!home.canSleepAt(pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getNextGuardSleepOffset() {
        int result = this.guardSleepOffset;
        this.guardSleepOffset += 7677;
        if (this.guardSleepOffset > 24000) {
            this.guardSleepOffset -= 24000;
        }
        return result;
    }

    public int getNextClericSleepOffset() {
        int result = this.clericSleepOffset;
        this.clericSleepOffset += 7777;
        if (this.clericSleepOffset > 24000) {
            this.clericSleepOffset -= 24000;
        }
        return result;
    }

    public List<VillageStructure> getHomes() {
        return getStructures(VillageStructureType.BARRACKS, VillageStructureType.HOME2, VillageStructureType.HOME4, VillageStructureType.HOME6);
    }

    public List<VillageStructure> getStructures(VillageStructureType... types) {
        List<VillageStructure> outList = new ArrayList<VillageStructure>();
        for (VillageStructureType arg : types) {
            outList.addAll(getStructures(arg));
        }
        return outList;
    }

    public List<VillageStructure> getStructures(VillageStructureType type) {
        List<VillageStructure> structList = this.structures.get(type);
        if (structList != null && !structList.isEmpty()) {
            return new ArrayList<>(structList);
        }

        return new ArrayList<>();
    }

    public boolean hasStructure(VillageStructureType type) {
        List<VillageStructure> structList = this.structures.get(type);
        return structList != null && !structList.isEmpty();
    }

    public VillageStructure getNearestStructure(VillageStructureType type, BlockPos pos) {
        List<VillageStructure> structList = getStructures(type);
        double min = Double.MAX_VALUE;
        VillageStructure closest = null;
        for (VillageStructure struct : structList) {
            double dist = pos.getSquaredDistance(struct.getDoor());
            if (dist < min) {
                min = dist;
                closest = struct;
            }
        }

        return closest;
    }

    public boolean isStructureValid(VillageStructure struct) {
        if (!struct.isValid()) {
            return false;
        }

        int count = 0;
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {


                if (vs != struct && vs.isStructureOverlapped(struct)) {
                    debugOut("Structures overlap | " + struct.type + "  " + vs.getAABB());
                    return false;
                }
            }
        }

        return true;
    }

    public void addVillagerPosition(EntityVillagerTek villager) {
        addVillagerPosition(villager.getPosition());
    }

    public void addVillagerPosition(BlockPos pos) {
        if (this.world.getTimeOfDay() - this.lastVillagerPosTime > 20L && getStructure(pos) == null) {
            this.villagerPositions.addLast(pos);
            this.lastVillagerPosTime = this.world.getTimeOfDay();

            while (this.villagerPositions.size() > 20) {
                this.villagerPositions.removeFirst();
            }
        }
    }

    public BlockPos getLastVillagerPos() {
        if (this.villagerPositions.isEmpty()) {
            return getOrigin();
        }

        BlockPos pos = this.villagerPositions.removeFirst();
        this.villagerPositions.addLast(pos);
        return pos;
    }

    public Box getAABB() {
        return this.aabb;
    }

    public boolean isInStructure(BlockPos bp) {
        return (getStructure(bp) != null);
    }

    public VillageStructure getStructure(BlockPos bp) {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {
                if (vs.isBlockInside(bp)) {
                    return vs;
                }
            }
        }
        return null;
    }

    public VillageStructure getStructureFromFrame(BlockPos bp) {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {
                if (vs.getFramePos().equals(bp)) {
                    return vs;
                }
            }
        }
        return null;
    }

    public void purchaseFromMerchant(MerchantRecipe recipe, EntityMerchant merchant, EntityPlayer player) {
        if (recipe.getItemToSell().getItem() == Items.SKULL) {
            String skullOwner = ModItems.getSkullAnimal(recipe.getItemToSell());
            if (!skullOwner.isEmpty()) {
                this.merchantScheduler.addOrder(skullOwner);

                player.inventory.setItemStack(ItemStack.EMPTY);
                player.inventory.clearMatchingItems(Items.SKULL, 3, 0, null);


                player.sendMessage(new TextComponentString("The merchant will return tomorrow with a " + skullOwner + "."));
            }
        }

        IVillageData vd = getTownData();
        if (vd != null) {
            debugOut("Player selling " + recipe.getItemToBuy().getItem().getTranslationKey() + " [" + player.getName() + "]");
            vd.getEconomy().sellItem(recipe, this);
        }
    }

    public void trackHappy(int delta) {
    }

    public void resetStorage() {
        this.storage.clear();
        this.storageScanners.clear();
    }

    public void addStorageChest(ChestBlockEntity chest) {
        this.storage.add(chest);
        this.storageScanners.put(chest.getPos(), new InventoryScanner(chest));
        debugOut("Storage Chest [" + chest.getPos() + "]   Added    (" + this.storage.size() + " total)");
    }

    public void removeStorageChest(ChestBlockEntity chest) {
        this.storage.remove(chest);
        this.storageScanners.remove(chest.getPos());
        debugOut("Storage Chest [" + chest.getPos() + "]   Removed    (" + this.storage.size() + " total)");
    }

    public int getStorageCount(Predicate<ItemStack> predicate) {
        int count = 0;
        for (ChestBlockEntity chest : this.storage) {
            if (!chest.isInvalid() && !chest.isInvEmpty()) {
                for (int i = 0; i < chest.getInvSize(); i++) {
                    ItemStack itemStack = chest.getInvStack(i);
                    if (predicate.test(itemStack)) {
                        count += itemStack.getCount();
                    }
                }
            }
        }
        return count;
    }

    public ChestBlockEntity getStorageChestWithItem(Function<ItemStack, Integer> function) {
        int bestResult = 0;
        ChestBlockEntity bestChest = null;
        for (ChestBlockEntity chest : this.storage) {
            if (!chest.isInvalid() && !chest.isInvEmpty()) {
                for (int i = 0; i < chest.getInvSize(); i++) {
                    ItemStack itemStack = chest.getInvStack(i);
                    if (!itemStack.isEmpty()) {
                        int result = function.apply(itemStack);
                        if (result > bestResult) {
                            bestResult = result;
                            bestChest = chest;
                        }
                    }
                }
            }
        }

        return bestChest;
    }

    private int chestCompareTo(ChestBlockEntity chest1, ChestBlockEntity chest2, BlockPos origin) {
        double d1 = chest1.getSquaredDistance(origin.getX(), origin.getY(), origin.getZ());
        double d2 = chest2.getSquaredDistance(origin.getX(), origin.getY(), origin.getZ());
        return Double.compare(d1, d2);
    }

    public ChestBlockEntity getAvailableStorageChest(ItemStack testStack, BlockPos origin) {
        ChestBlockEntity bestChest = null;

        this.storage.sort((chest1, chest2) -> chestCompareTo(chest1, chest2, origin));
        for (ChestBlockEntity chest : this.storage) {
            if (!chest.isInvalid()) {
                int openSlot = -1;
                boolean matchedThisChest = false;
                for (int i = 0; i < chest.getSizeInventory(); i++) {
                    ItemStack itemStack = chest.getStackInSlot(i);
                    if (itemStack.isEmpty() && openSlot < 0) {
                        openSlot = i;
                        if (matchedThisChest) {
                            return chest;
                        }
                    } else if (VillagerInventory.areItemsStackable(itemStack, testStack)) {
                        matchedThisChest = true;
                        if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                            return chest;
                        }
                    }
                }

                if (openSlot >= 0) {
                    if (matchedThisChest)
                        return chest;
                    if (bestChest == null) {
                        bestChest = chest;
                    }
                }
            }
        }
        return bestChest;
    }

    public VillageStructureHome getAvailableHome(EntityVillagerTek villager) {
        List<VillageStructure> homes = getHomes();
        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome) struct;
            if (!home.isFull() && home.canVillagerSleep(villager)) {
                return home;
            }
        }

        return null;
    }

    public int getSize() {
        return 120;
    }

    private void occupancySpam() {
        int beds = 0;
        int residents = 0;
        List<VillageStructure> homes = getHomes();
        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome) struct;
            beds += home.getMaxResidents();
            residents += home.getCurResidents();
        }

        debugOut("Residents:  " + residents + " / " + beds);
    }

    public int getResidentCount() {
        return this.residents.size();
    }

    public String getName() {
        return this.villageName;
    }

    public void villageReport(String reportType) {
        IVillageData vd = getTownData();
        if (vd != null) {
            StringBuilder reportOut = new StringBuilder();
            reportOut.append("\n");
            reportOut.append("----- Village Report --[" + this.villageName + "]---------\n");

            if (isReportType(reportType, "homes")) {
                homeReport(reportOut);
            }

            if (isReportType(reportType, "happy")) {
                happyReport(reportOut);
            }

            if (isReportType(reportType, "hunger")) {
                hungerReport(reportOut);
            }

            if (isReportType(reportType, "levels")) {
                levelReport(reportOut);
            }

            if (isReportType(reportType, "economy")) {
                vd.getEconomy().report(reportOut);
            }
            reportOut.append("------------------------------------\n");
            debugOut(reportOut.toString());
        } else {

            System.err.println("==== Village Without VillageData?? ====");
        }
    }

    private void homeReport(StringBuilder builder) {
        List<VillageStructure> homes = getHomes();
        builder.append("    " + homes.size() + " homes - " + this.residents.size() + " residents");

        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome) struct;
            home.villageReport(builder);
        }
    }

    private void happyReport(StringBuilder builder) {
        int HAPPY_BUCKETS = 5;
        int BUCKET_SIZE = 20;
        int[] happyBuckets = new int[5];
        for (EntityVillagerTek villager : this.residents) {
            int bucket = MathHelper.clamp(villager.getHappy() / 20, 0, 4);
            happyBuckets[bucket] = happyBuckets[bucket] + 1;
        }

        for (int i = 0; i < 5; i++) {
            int step = 20 * i;
            builder.append("Happy " + String.format("%-2s", Integer.valueOf(step)) + " - " + String.format("%-2s", Integer.valueOf(step + 20 - 1)) + ") " + String.format("%-3s", Integer.valueOf(happyBuckets[i])));

            if (happyBuckets[i] > 0) {
                char[] chars = new char[happyBuckets[i]];
                Arrays.fill(chars, 'P');
                builder.append("  " + new String(chars));
            }
            builder.append("\n");
        }
    }

    private void hungerReport(StringBuilder builder) {
        int HUNGER_BUCKETS = 5;
        int BUCKET_SIZE = 20;
        int[] hungerBuckets = new int[5];
        for (EntityVillagerTek villager : this.residents) {
            int bucket = MathHelper.clamp(villager.getHunger() / 20, 0, 4);
            hungerBuckets[bucket] = hungerBuckets[bucket] + 1;
        }

        for (int i = 0; i < 5; i++) {
            int step = 20 * i;
            builder.append("Hunger " + String.format("%-2s", Integer.valueOf(step)) + " - " + String.format("%-2s", Integer.valueOf(step + 20 - 1)) + ") " + String.format("%-3s", Integer.valueOf(hungerBuckets[i])));

            if (hungerBuckets[i] > 0) {
                char[] chars = new char[hungerBuckets[i]];
                Arrays.fill(chars, 'H');
                builder.append("  " + new String(chars));
            }
            builder.append("\n");
        }
    }

    private void levelReport(StringBuilder builder) {
        class ProfessionLevelTracker {
            public int ct;
            public int sum;
            public int days;
            public ProfessionType pt;
            List<EntityVillagerTek> villagers;

            private ProfessionLevelTracker(ProfessionType pt) {
                this.ct = 0;
                this.sum = 0;
                this.days = 0;

                this.villagers = new ArrayList<>();

                this.pt = pt;
            }

            int getAvg() {
                return this.sum / this.ct;
            }

            float getSkillsPerDay() {
                return this.sum / this.days;
            }

            public void report(StringBuilder builder) {
                builder.append(String.format("%-15s", this.pt.name)).append(" [").append(String.format("%-2s", getAvg()))
                        .append("]   [").append(String.format("%.2f", getSkillsPerDay())).append(" per day] ");
                this.villagers.forEach(v -> builder.append(String.format("%-2s", v.getBaseSkill(this.pt))).append(" "));
                builder.append("\n");
            }
        }

        Map<ProfessionType, ProfessionLevelTracker> levelMap = new HashMap<ProfessionType, ProfessionLevelTracker>();


        for (EntityVillagerTek v : this.residents) {
            ProfessionLevelTracker tracker = levelMap.get(v.getProfessionType());
            if (tracker == null) {
                tracker = new ProfessionLevelTracker(v.getProfessionType());
                levelMap.put(v.getProfessionType(), tracker);
            }

            int baseSkill = v.getBaseSkill(v.getProfessionType());
            tracker.ct++;
            tracker.sum += baseSkill;
            tracker.days += v.getDaysAlive();
            tracker.villagers.add(v);
        }

        levelMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().getAvg())).forEach(e -> e.getValue().report(builder));
    }

    private boolean isReportType(String reportType, String testType) {
        return (reportType.equals("all") || reportType.equals(testType));
    }

    public BlockPos getEdgeNode() {
        BasePathingNode node = this.pathingGraph.getEdgeNode(getOrigin(), getSize() * 0.9D);
        if (node != null) {
            BlockPos pos = node.getBlockPos();
            if (getStructure(pos) == null) {
                return pos;
            }
        }
        return null;
    }

    private void updateCenter() {
        double farthestStructure = 0.0D;
        this.center = getOrigin();
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure struct : lst) {
                if (struct.adjustsVillageCenter()) {
                    if (struct.type == VillageStructureType.STORAGE) {
                        this.center = struct.getDoorOutside();
                    }


                    if (this.aabb == null) {
                        this.aabb = struct.getAABB();
                        continue;
                    }
                    this.aabb = this.aabb.union(struct.getAABB());
                }
            }
        }
    }

    public void destroy() {
        debugOut("== Village being destroyed ==");
        this.isDestroyed = true;
    }

    private void cleanVillage() {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (int i = lst.size() - 1; i >= 0; i--) {
                VillageStructure struct = lst.get(i);
                if (!isStructureValid(struct)) {
                    debugOut("Removing structure " + struct.type + " [" + struct.getDoor() + "]");
                    struct.onDestroy();
                    lst.remove(i);
                    if (struct.adjustsVillageCenter()) {
                        updateCenter();
                    }
                }
            }
        }

        VillageManager vm = VillageManager.get(this.world);
        vm.addScanBox((new Box(this.center)).expand(64.0D, 64.0D, 64.0D));


        this.structures.entrySet().removeIf(entry -> entry.getValue().isEmpty());


        clearDeadEnemies();
        cleanDebugTurds();
        this.cleanTick = this.world.rand.nextInt(40) + 40;
    }

    private void tickInventoryScanners() {
        for (InventoryScanner scanner : this.storageScanners.values()) {
            int changedSlot = scanner.tickSlot();
            if (changedSlot >= 0) {
                notifyResidentsStorageUpdate(scanner.getChangedItem());
            }
        }
    }

    private void notifyResidentsStorageUpdate(ItemStack updatedItem) {
        if (!updatedItem.isEmpty())
            this.residents.forEach(v -> v.onStorageChange(updatedItem));
    }

    public void onStorageChange(TileEntityChest chest, int slot, ItemStack updatedItem) {
        InventoryScanner scanner = this.storageScanners.get(chest.getPos());
        if (scanner != null) {
            scanner.updateSlotSilent(slot);
        }
        notifyResidentsStorageUpdate(updatedItem);
    }

    public void addResident(EntityVillagerTek v) {
        this.residents.add(v);
    }

    public void removeResident(EntityVillagerTek v) {
        this.residents.remove(v);
    }

    public void update() {
        this.jobs.tick();
        this.blockFinder.update();
        this.farmFinder.update();
        this.mineFinder.update();
        this.pathingGraph.update();
        this.raidScheduler.update();

        tickInventoryScanners();

        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure struct : lst) {
                struct.update();
            }
        }

        this.tickCounter++;
        this.cleanTick--;
        if (this.cleanTick <= 0) {
            cleanVillage();
        }


        if (!this.world.isDaylight()) {
            this.merchantScheduler.resetDay();
            this.nomadScheduler.resetDay();
        }
    }

    public boolean isValid() {
        if (this.isDestroyed) {
            return false;
        }
        ItemStack villageToken = getVillageToken();
        if (villageToken.isEmpty()) {
            debugOut("Village has no Token");
            return false;
        }

        if (!getTownData().getUUID().equals(this.villageUUID)) {
            debugOut("Village UUID changed.  Token swap?");
            return false;
        }

        if (getOrigin() != null && !this.world.isBlockLoaded(getOrigin())) {
            debugOut("Village unloaded");
            return false;
        }

        if (this.center == null) {
            System.err.println("Village has no center");
            return false;
        }


        if (this.structures.size() <= 0) {
            debugOut("Village has no structures");
            return false;
        }

        return true;
    }

    public boolean isInVillage(BlockPos pos) {
        return (Math.max(Math.abs(getOrigin().getZ() - pos.getZ()), Math.abs(getOrigin().getX() - pos.getX())) < 120);
    }

    public BlockPos getEnemySighting() {
        return this.enemySighting;
    }

    public boolean enemySeenRecently() {
        return !this.enemies.isEmpty();
    }

    private void clearDeadEnemies() {
        this.enemies.removeIf(enemy -> !enemy.enemy.isEntityAlive() || Math.abs(this.tickCounter - enemy.aggressionTime) > 400);
    }

    private void cleanDebugTurds() {
        List<ArmorStandEntity> stands = this.world.getEntitiesWithinAABB(EntityArmorStand.class, getAABB());
        for (ArmorStandEntity st : stands) {
            if (st.getCustomName().equals("PathNode") && st.deathTime > 400) {
                st.kill();
            }
        }
    }

    public void addOrRenewEnemy(EntityLivingBase enemy, int threat) {
        this.enemySighting = enemy.getPosition();

        for (VillageEnemy existingEnemy : this.enemies) {

            if (existingEnemy.enemy == enemy) {
                existingEnemy.threat += threat;
                existingEnemy.aggressionTime = this.tickCounter;

                return;
            }
        }
        this.enemies.add(new VillageEnemy(enemy, this.tickCounter));
    }

    public EntityLivingBase getEnemyTarget(EntityVillagerTek villager) {
        double closest = Double.MAX_VALUE;
        boolean foundMinion = false;
        EntityLivingBase target = null;
        for (VillageEnemy existingEnemy : this.enemies) {

            if (existingEnemy.enemy.isEntityAlive()) {

                boolean isMinion = EntityNecromancer.isMinion(existingEnemy.enemy);
                if (isMinion && !foundMinion) {
                    closest = Double.MAX_VALUE;
                    target = null;
                }

                if (isMinion || !foundMinion) {
                    double dist = existingEnemy.enemy.getDistanceSq(villager);
                    if (dist < closest) {
                        closest = dist;
                        target = existingEnemy.enemy;
                    }

                    foundMinion = isMinion;
                }
            }
        }

        return target;
    }

    private void cleanDefenders() {
        this.activeDefenders.removeIf(d -> (d.getAttackTarget() == null || !d.getAttackTarget().isEntityAlive() || !d.isEntityAlive()));
    }

    public void addActiveDefender(EntityVillagerTek villager) {
        this.activeDefenders.add(villager);
    }

    public EntityVillagerTek getActiveDefender(BlockPos pos) {
        cleanDefenders();
        return this.activeDefenders.stream().min(Comparator.comparing(e -> Float.valueOf(e.getHealth()))).orElse(null);
    }

    public void onBlockUpdate(World w, BlockPos bp) {
        this.pathingGraph.onBlockUpdate(w, bp);
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        if (isInVillage(event.getPos())) {
            this.farmFinder.onCropGrowEvent(event);
        }
    }

    public void reportVillagerDamage(EntityVillagerTek villager, DamageSource damageSrc, float damageAmount) {
        villager.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200));
        String damageMessage = villager.getDisplayName().getUnformattedText() + " [" + ((villager.getProfessionType() == null) ? "" : (villager.getProfessionType()).name) + "] has taken " + String.format("%.2f", Float.valueOf(damageAmount)) + " damage";

        if (damageSrc.getTrueSource() != null) {
            damageMessage = damageMessage + " from " + damageSrc.getTrueSource().getDisplayName().getUnformattedText();
        }
        String finalDamageMessage = damageMessage;

        sendChatMessage(finalDamageMessage);

        debugOut("[Villager Damage] " + finalDamageMessage);
    }

    public void reportVillagerDeath(EntityVillagerTek villager, DamageSource damageSrc) {
        String damageMessage = villager.getDisplayName().getUnformattedText() + " [" + (villager.getProfessionType()).name + "] has been killed by " + damageSrc.getDamageType() + " damage. [" + villager.getPosition().getX() + ", " + villager.getPosition().getY() + ", " + villager.getPosition().getZ() + "]";
        playEvent(SoundEvents.ENTITY_VILLAGER_DEATH, new TextComponentString(damageMessage));

        List<EntityNecromancer> necros = this.world.getEntitiesWithinAABB(EntityNecromancer.class, (new AxisAlignedBB(getOrigin())).grow(120.0D));
        necros.forEach(n -> n.notifyVillagerDeath());
    }

    public void sendChatMessage(String msg) {
        sendChatMessage(new TextComponentString(msg));
    }

    public void sendChatMessage(ITextComponent textComponent) {
        List<EntityPlayerMP> nearbyPlayers = this.world.getPlayers(EntityPlayerMP.class, p -> (p.getPosition().distanceSq(getOrigin()) < 40000.0D));
        nearbyPlayers.stream().forEach(p -> p.sendMessage(textComponent));
        debugOut(textComponent.getUnformattedText());
    }

    public void playEvent(SoundEvent soundEvent, ITextComponent textComponent) {
        for (EntityPlayerMP entityPlayer : this.world.getEntitiesWithinAABB(EntityPlayerMP.class, getAABB().grow(50.0D))) {

            entityPlayer.world.playSound(null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, soundEvent, SoundCategory.MASTER, 1.2F, 1.0F);
            entityPlayer.sendMessage(textComponent);
        }
    }

    public boolean hasBlock(Block b) {
        return this.blockFinder.hasBlock(b);
    }

    public BlockPos requestBlock(Block b) {
        return this.blockFinder.requestBlock(b);
    }

    public void releaseBlockClaim(Block block, BlockPos pos) {
        this.blockFinder.releaseClaim(this.world, block, pos);
    }

    public VillageStructureMineshaft requestMineshaft(EntityVillagerTek miner, Predicate<VillageStructureMineshaft> pred, BiPredicate<VillageStructureMineshaft, VillageStructureMineshaft> compare) {
        return this.mineFinder.requestMineshaft(miner, pred, compare);
    }

    public BlockPos requestMaxAgeCrop() {
        return this.farmFinder.getMaxAgeCrop();
    }

    public BlockPos requestFarmland(Predicate<BlockPos> pred) {
        return this.farmFinder.getFarmland(pred);
    }

    class VillageEnemy {
        public EntityLivingBase enemy;
        public int aggressionTime;
        public int threat;

        VillageEnemy(EntityLivingBase enemy, int agressionTimeIn) {
            this.enemy = enemy;
            this.aggressionTime = agressionTimeIn;
            this.threat = 1;
        }
    }
}
*/
