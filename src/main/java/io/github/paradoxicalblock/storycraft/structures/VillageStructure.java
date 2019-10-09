package io.github.paradoxicalblock.storycraft.structures;

import io.github.paradoxicalblock.storycraft.Village;
import io.github.paradoxicalblock.storycraft.caps.IVillageData;
import io.github.paradoxicalblock.storycraft.entity.EntityVillagerTek;
import io.github.paradoxicalblock.storycraft.pathing.BasePathingNode;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import io.github.paradoxicalblock.storycraft.tickjob.TickJobQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.caps.VillageDataProvider;

import java.util.*;
import java.util.stream.Stream;

public abstract class VillageStructure {
    protected static int MAX_FLOOR = 500;
    private static int MIN_FLOOR = 4;
    public final VillageStructureType type;
    protected final int signEntityId;
    protected final World world;
    protected BlockPos door;
    protected BlockPos framePos;
    protected Direction signFacing;
    protected Village village;
    protected List<BlockPos> floorTiles;
    protected int ceilingHeightSum;
    protected boolean isValid;
    protected Box aabb;
    protected Map<Block, List<BlockPos>> specialBlocks;
    protected boolean specialAdded;
    protected TickJobQueue jobs;
    protected BlockPos safeSpot;
    private Set<BlockPos> occupiedSpecials;
    protected VillageStructure(World world, Village v, ItemFrameEntity itemFrame, VillageStructureType t, String name) {
        this.floorTiles = new ArrayList<>();
        this.ceilingHeightSum = 0;
        this.isValid = true;
        this.specialBlocks = new HashMap<>();
        this.specialAdded = false;
        this.jobs = new TickJobQueue();
        this.safeSpot = null;
        this.occupiedSpecials = new HashSet<>();


        this.type = t;
        this.world = world;
        this.village = v;
        this.framePos = itemFrame.getDecorationBlockPos();
        this.signFacing = itemFrame.getHorizontalFacing();
        this.signEntityId = itemFrame.getEntityId();
    }

    public static boolean isWoodDoor(World world, BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block instanceof net.minecraft.block.DoorBlock) {
            return (iblockstate.getMaterial() == Material.WOOD);
        }
        return false;
    }

    public static boolean isGate(World world, BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block instanceof net.minecraft.block.FenceGateBlock;
    }

    public void setup() {
        this.door = findDoor();
        if (this.door != null) {
            doFloorScan();
        }

        setupServerJobs();
        validate();
    }

    public Village getVillage() {
        return this.village;
    }

    public int getMaxAllowed() {
        return 0;
    }

    public void addJob(TickJob job) {
        if (this.world.isClient) {
            throw new IllegalStateException("Cannot add tick jobs on client");
        }
        this.jobs.addJob(job);
    }

    protected void setupServerJobs() {
        addJob(new TickJob(50, 100, true, this::validate));
        addJob(new TickJob(180, 120, true, () -> {
            if (this.isValid) {
                doFloorScan();
            }
        }));
    }

    public void update() {
        this.jobs.tick();
    }

    protected void onFloorScanStart() {
    }

    protected void onFloorScanEnd() {
    }

    public BlockPos getDoorOutside() {
        return getDoorOutside(1);
    }

    public BlockPos getDoorOutside(int dist) {
        return this.door.offset(this.signFacing, dist);
    }

    public BlockPos getDoorInside() {
        return this.door.offset(this.signFacing, -1);
    }

    protected void doFloorScan() {
        this.specialBlocks.clear();
        this.safeSpot = null;
        this.aabb = new Box(this.door, this.door.up(2));
        this.floorTiles.clear();
        this.ceilingHeightSum = 0;
        onFloorScanStart();
        scanFloor(getDoorInside());
        if (this.safeSpot == null) {
            this.safeSpot = getDoorInside();
        }
        onFloorScanEnd();
    }

    protected void scanFloor(BlockPos pos) {
        if (this.floorTiles.size() <= MAX_FLOOR && !this.floorTiles.contains(pos)) {
            int height = scanRoomHeight(pos);

            if (height >= 2 && !BasePathingNode.isPassable(this.world, pos.down())) {
                this.ceilingHeightSum += height;
                this.floorTiles.add(pos);
                Box bbox = new Box(pos);
                this.aabb = this.aabb.union(bbox);

                scanFloor(pos.west());
                scanFloor(pos.north());
                scanFloor(pos.east());
                scanFloor(pos.south());

                if (this.safeSpot == null && !this.world.doesNotCollide(new Box((pos.getX() - 1), pos.getY(), (pos.getZ() - 1), (pos.getX() + 1), (pos.getY() + 1), (pos.getZ() + 1)))) {
                    this.safeSpot = pos;
                }
            }
        }
    }

    protected int scanRoomHeight(BlockPos pos) {
        for (int i = 0; i < 30; i++) {
            BlockPos p = pos.up(i);
            Block b = this.world.getBlockState(p).getBlock();
            this.specialAdded = false;

            if (i == 0) {
                scanSpecialBlock(p, b);
            }
            if (!this.specialAdded && (
                    !BasePathingNode.isPassable(this.world, p) || isWoodDoor(this.world, pos) || isGate(this.world, pos))) {
                return i;
            }
        }

        return 0;
    }

    public BlockPos getSafeSpot() {
        return this.safeSpot;
    }

    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == ModBlocks.blockChair) {
            addSpecialBlock(ModBlocks.blockChair, pos);
        }
    }

    protected void addSpecialBlock(Block block, BlockPos bp) {
        List<BlockPos> list = this.specialBlocks.computeIfAbsent(block, k -> new ArrayList<>());
        this.specialAdded = true;
        if (!list.contains(bp)) {
            list.add(bp);
        }
    }

    public BlockPos getUnoccupiedSpecialBlock(Block block) {
        List<BlockPos> list = this.specialBlocks.get(block);
        if (list != null) {
            Collections.shuffle(list);
            return list.stream().filter(b -> !isSpecialBlockOccupied(b)).findAny().orElse(null);
        }

        return null;
    }

    public List<BlockPos> getSpecialBlocks(Block block) {
        List<BlockPos> list = this.specialBlocks.get(block);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return new ArrayList<>();
    }

    public boolean vacateSpecialBlock(BlockPos bp) {
        return this.occupiedSpecials.remove(bp);
    }

    public boolean occupySpecialBlock(BlockPos bp) {
        return this.occupiedSpecials.add(bp);
    }

    public boolean isSpecialBlockOccupied(BlockPos bp) {
        return this.occupiedSpecials.contains(bp);
    }

    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return false;
    }

    public BlockPos tryVillagerSit(EntityVillagerTek villager) {
        BlockPos result = null;
        if (shouldVillagerSit(villager)) {
            List<BlockPos> chairs = getSpecialBlocks(ModBlocks.blockChair);
            if (!chairs.isEmpty()) {
                Collections.shuffle(chairs);
                Stream<BlockPos> availableChairs = chairs.stream().filter(c -> !isSpecialBlockOccupied(c));

                if (villager.getRand().nextInt(3) == 0) {
                    result = availableChairs.findAny().orElse(null);
                } else {

                    BlockPos takenChair = chairs.stream().filter(this::isSpecialBlockOccupied).findAny().orElse(null);
                    if (takenChair == null) {

                        result = availableChairs.findAny().orElse(null);
                    } else {

                        result = availableChairs.min(Comparator.comparing(bp -> bp.getSquaredDistance(takenChair))).orElse(null);
                    }
                }
            }
        }

        if (result != null) {
            occupySpecialBlock(result);
        }

        return result;
    }

    public boolean isStructureOverlapped(VillageStructure other) {
        if (getAABB().intersects(other.getAABB())) {
            return this.floorTiles.stream().anyMatch(f -> other.floorTiles.contains(f));
        }

        return false;
    }

    public int getSitTime(EntityVillagerTek villager) {
        return 0;
    }

    protected BlockPos findDoor() {
        BlockPos dp = null;

        dp = this.framePos.offset(this.signFacing, -1).offset(this.signFacing.rotateYClockwise(), 1);
        if (!isWoodDoor(this.world, dp)) {
            dp = this.framePos.offset(this.signFacing, -1).offset(this.signFacing.rotateYClockwise(), -1);
            if (!isWoodDoor(this.world, dp)) {
                dp = this.framePos.offset(this.signFacing, -1).down(2);
                if (!isWoodDoor(this.world, dp)) {
                    dp = null;
                }
            }
        }
        if (dp != null &&
                isWoodDoor(this.world, dp.down())) {
            dp = dp.down();
        }

        return dp;
    }

    public boolean isBlockInside(BlockPos pos) {
        if (this.aabb.contains(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)))
            for (BlockPos p : this.floorTiles) {
                if (p.equals(pos)) {
                    return true;
                }
            }
        return false;
    }

    public boolean isBlockNear(BlockPos pos, double dist) {
        if (this.aabb.expand(dist, dist, dist).contains(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D))) {
            double distSq = dist * dist;
            for (BlockPos p : this.floorTiles) {
                if (p.getSquaredDistance(pos) < distSq) {
                    return true;
                }
            }
        }
        return false;
    }


    public List<PlayerEntity> getPlayersInside() {
        return this.world.getEntities(PlayerEntity.class, p -> this.aabb.contains(p.getPositionVector()));
    }


    public BlockPos getRandomFloorTile() {
        if (this.floorTiles.isEmpty()) {
            return null;
        }
        int index = this.world.rand.nextInt(this.floorTiles.size());

        BlockPos bp = this.floorTiles.get(index);
        if (BasePathingNode.isPassable(this.world, bp)) {
            return bp;
        }
        return null;
    }


    public Box getAABB() {
        return this.aabb;
    }


    public BlockPos getDoor() {
        return this.door;
    }


    public BlockPos getFramePos() {
        return this.framePos;
    }


    public void onDestroy() {
    }


    public float getCrowdedFactor() {
        int floorCount = this.floorTiles.size();
        float avgCeiling = this.ceilingHeightSum / floorCount;

        float modifier = 1.0F;


        if (this.type.tilesPerVillager > 0) {
            int villagersInside = getEntitiesInside(EntityVillagerTek.class).size();
            int densityRatio = floorCount / villagersInside;
            if (densityRatio < this.type.tilesPerVillager) {
                float compare = densityRatio / this.type.tilesPerVillager;
                modifier *= (compare - 0.5F) * 2.0F;
            }
        }


        if (avgCeiling < 2.5D) {
            modifier *= 0.5F;
        }
        return 1.0F - modifier;
    }

    public ItemFrameEntity getItemFrame() {
        Entity e = this.world.getEntityById(this.signEntityId);
        if (e instanceof ItemFrameEntity) {
            return (EntityItemFrame) e;
        }

        return null;
    }

    public IVillageData getData() {
        ItemFrameEntity frame = getItemFrame();
        if (frame != null && frame.getHeldItemStack() != null) {
            return (IVillageData) frame.getHeldItemStack().get(VillageDataProvider.VILLAGE_DATA_CAPABILITY, null);
        }


        return null;
    }

    public <T extends Entity> List<T> getEntitiesInside(Class<? extends T> clazz) {
        List<T> entList = this.world.getEntitiesWithinAABB(clazz, getAABB().grow(0.5D, 3.0D, 0.5D));
        ListIterator<T> itr = entList.listIterator();
        while (itr.hasNext()) {
            T ent = itr.next();
            if (!isBlockNear(ent.getPosition(), 2.0D)) {
                itr.remove();
            }
        }

        return entList;
    }

    public void debugOut(String text) {
        if (this.village != null) {
            this.village.debugOut("[" + this.type.name() + "] " + text);
        } else {
            System.out.println("[No Village] " + text);
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public boolean validate() {
        this.isValid = true;

        if (this.door == null) {

            this.isValid = false;
        } else if (this.world.isBlockLoaded(this.door)) {
            if (!isWoodDoor(this.world, this.door) && !isGate(this.world, this.door)) {
                debugOut("Village struct is missing its door " + getFramePos());
                this.isValid = false;
            }

            if (this.isValid && this.floorTiles.size() > MAX_FLOOR) {
                debugOut("Village struct has too many floor tiles " + getFramePos());
                this.isValid = false;
            }

            Entity e = this.world.getEntityByID(this.signEntityId);
            if (this.isValid && (e == null || !(e instanceof ItemFrameEntity))) {
                debugOut("Village struct frame is missing or wrong type | " + getFramePos());
                this.isValid = false;
            }

            ItemFrameEntity itemFrame = (ItemFrameEntity) e;
            if (this.isValid && itemFrame.getBlockPos() != this.framePos) {
                debugOut("Village struct center has moved " + getFramePos());
                this.isValid = false;
            }

            if (this.isValid && !this.type.isItemEqual(itemFrame.getHeldItemStack())) {
                debugOut("Village struct frame item has changed " + getFramePos());
                this.isValid = false;
            }

            if (this.isValid && this.floorTiles.size() < MIN_FLOOR) {
                this.isValid = false;
            }
        }


        return this.isValid;
    }

    public void checkOccupiedBlocks() {
        Iterator<BlockPos> itr = this.occupiedSpecials.iterator();
        while (itr.hasNext()) {
            BlockPos bp = itr.next();
            List<EntityVillagerTek> villagers = this.world.getEntitiesWithinAABB(EntityVillagerTek.class, (new AxisAlignedBB(bp)).grow(1.0D));
            if (villagers.isEmpty()) {
                System.out.println("UnOccupying block " + bp + ". No villagers nearby");
                itr.remove();
            }
        }
    }


    public boolean adjustsVillageCenter() {
        return true;
    }
}
