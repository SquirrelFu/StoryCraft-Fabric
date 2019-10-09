/*
package io.github.paradoxicalblock.storycraft;

import io.github.paradoxicalblock.storycraft.structures.VillageStructureType;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import io.github.paradoxicalblock.storycraft.tickjob.TickJobQueue;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.BlockEvent;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.network.PacketVillage;
import net.tangotek.tektopia.pathing.PathingWorldListener;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureFactory;

import java.util.*;
import java.util.function.Supplier;

public class VillageManager extends PersistentState {
    private static final String DATA_IDENTIFIER = "tektopia_VillageManager";
    private final VillageStructureFactory structureFactory = new VillageStructureFactory();
    private final PathingWorldListener pathWorldListener;
    protected World world;
    protected Set<Village> villages = new HashSet();
    protected Queue<Box> scanBoxes = new LinkedList();
    protected BlockPos lastVillagerPos;
    protected TickJobQueue jobs = new TickJobQueue();
    private long tick = 0L;
    private BlockPos lastStuck = null;
    private boolean debugOn = false;

    public VillageManager(World worldIn) {
        super("tektopia_VillageManager");
        this.world = worldIn;
        this.pathWorldListener = new PathingWorldListener(this);
        this.world.addEventListener(this.pathWorldListener);

        this.jobs.addJob(new TickJob(200, 0, true, this::sendVillagesToClients));
        this.jobs.addJob(new TickJob(100, 20, true, () -> processPlayerPositions(128, 32, 128)));
        this.jobs.addJob(new TickJob(15, 10, true, () -> processPlayerPositions(16, 6, 16)));
    }

    public static VillageManager get(World world) {
        ServerWorld overworld = world.getServer().getWorld(DimensionType.OVERWORLD);
        PersistentStateManager storage = overworld.getPersistentStateManager();
        VillageManager instance = (VillageManager) storage.getOrCreate((Supplier<PersistentState>) () -> {
            try {
                return VillageManager.class.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }, "storyCraft_VillageManager");

        if (instance == null) {
            instance = new VillageManager(world);
            storage.method_20786((Supplier<PersistentState>) () -> {
                try {
                    return VillageManager.class.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }, "storyCraft_VillageManager");
        }
        return instance;
    }


    public static int getItemValue(Item item) {
        return 1;
    }


    private boolean isChunkFullyLoaded(World world, BlockPos pos) {
        if (world.isRemote) return true;
        long i = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        Chunk chunk = (Chunk) ((ChunkProviderServer) world.getChunkProvider()).loadedChunks.get(i);
        return (chunk != null && !chunk.unloadQueued);
    }


    public void update() {
        this.tick++;
        this.jobs.tick();

        Iterator<Village> itr = this.villages.iterator();
        while (itr.hasNext()) {
            Village v = itr.next();
            if (!isChunkFullyLoaded(this.world, v.getOrigin()) || !v.isValid()) {
                IVillageData vd = v.getTownData();
                if (vd != null) {
                    v.debugOut("Village REMOVED - [" + v.getName() + "]   " + vd.getUUID());
                }
                v.destroy();
                itr.remove();
                continue;
            }
            v.update();
        }


        procesScanBoxes();
    }

    private void sendVillagesToClients() {
        List<ServerPlayerEntity> players = this.world.getPlayers(ServerPlayerEntity.class, EntitySelectors.IS_ALIVE);
        for (EntityPlayerMP p : players) {
            Village v = getNearestVillage(p.getPosition(), 200);
            TekVillager.NETWORK.sendTo(new PacketVillage((v == null) ? null : new VillageClient(v)), p);
        }
    }

    private void processPlayerPositions(int x, int y, int z) {
        List<EntityPlayerMP> players = this.world.getPlayers(EntityPlayerMP.class, EntitySelectors.IS_ALIVE);
        for (EntityPlayerMP p : players) {
            Box aabb = p.getEntityBoundingBox().grow(x, y, z);
            addScanBox(aabb);
        }
    }

    private void procesScanBoxes() {
        while (!this.scanBoxes.isEmpty()) {
            Box aabb = this.scanBoxes.poll();
            ListIterator<ItemFrameEntity> itr = this.world.getEntities(ItemFrameEntity.class, aabb).listIterator();

            while (itr.hasNext()) {
                ItemFrameEntity itemFrame = itr.next();

                VillageStructureType structType = this.structureFactory.getByItem(itemFrame.getHeldItemStack());
                if (structType != null) {
                    boolean validStructure = false;
                    BlockPos framePos = itemFrame.getHangingPosition();
                    Village village = getNearestVillage(framePos, 360);
                    if (village == null && structType == VillageStructureType.TOWNHALL) {
                        village = new Village(this.world, framePos);
                        this.villages.add(village);
                    } else {

                        village = getVillageAt(framePos);


                        if (village != null &&
                                !ModItems.isItemVillageBound(itemFrame.getDisplayedItem(), village)) {

                            if (!ModItems.isItemVillageBound(itemFrame.getDisplayedItem())) {

                                village.debugOut("Binding Structure Marker to village - " + framePos);
                                ModItems.bindItemToVillage(itemFrame.getDisplayedItem(), village);
                            } else {

                                village.debugOut("Structure Marker " + itemFrame.getDisplayedItem().getTranslationKey() + " bound to INCORRECT village - " + framePos);
                                village = null;
                            }
                        }
                    }


                    if (village != null) {


                        VillageStructure struct = village.getStructureFromFrame(framePos);
                        if (struct == null) {
                            struct = this.structureFactory.create(structType, this.world, village, itemFrame);
                            if (village.addStructure(struct)) {
                                validStructure = true;
                            }
                        } else {
                            validStructure = true;
                        }
                    }

                    if (validStructure && !ModItems.isTaggedItem(itemFrame.getDisplayedItem(), ItemTagType.STRUCTURE)) {
                        ModItems.makeTaggedItem(itemFrame.getDisplayedItem(), ItemTagType.STRUCTURE);
                        itemFrame.setDisplayedItem(itemFrame.getDisplayedItem());
                        continue;
                    }
                    if (!validStructure && ModItems.isTaggedItem(itemFrame.getDisplayedItem(), ItemTagType.STRUCTURE)) {
                        ModItems.untagItem(itemFrame.getDisplayedItem(), ItemTagType.STRUCTURE);
                        itemFrame.setDisplayedItem(itemFrame.getDisplayedItem());
                    }
                }
            }
        }
    }


    public void addScanBox(Box aabb) {
        this.scanBoxes.add(aabb);
    }


    public Village getVillageAt(BlockPos blockPos) {
        for (Village v : this.villages) {
            if (v.isLoaded() &&
                    v.isInVillage(blockPos)) {
                return v;
            }
        }

        return null;
    }

    public Village getNearestVillage(BlockPos blockPos, int maxDist) {
        Village result = null;
        double closest = Double.MAX_VALUE;
        int maxDistSq = maxDist * maxDist;
        for (Village v : this.villages) {
            if (v.isLoaded()) {
                double distSq = v.getOrigin().getSquaredDistance(blockPos);
                if (distSq < closest && distSq < maxDistSq) {
                    closest = distSq;
                    result = v;
                }
            }
        }

        return result;
    }

    public List<Village> getVillagesNear(BlockPos blockPos, int maxDist) {
        List<Village> result = new ArrayList<Village>();
        int maxDistSq = maxDist * maxDist;
        for (Village v : this.villages) {
            if (v.isLoaded()) {
                double distSq = v.getOrigin().getSquaredDistance(blockPos);
                if (distSq < maxDistSq) {
                    result.add(v);
                }
            }
        }

        return result;
    }


    public boolean canSleepAt(BlockPos pos) {
        for (Village v : this.villages) {
            if (!v.canSleepAt(pos)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDebugOn() {
        return this.debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public void submitStuck(BlockPos bp) {
        this.lastStuck = bp;
    }


    public BlockPos getLastStuck() {
        return this.lastStuck;
    }


    public void onBlockUpdate(World w, BlockPos bp) {
        for (Village v : this.villages)
            v.onBlockUpdate(w, bp);
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        for (Village v : this.villages)
            v.onCropGrowEvent(event);
    }

    public void villageReport(String reportType) {
        System.out.println("VILLAGE REPORT - [" + this.villages.size() + " villages]");
        for (Village v : this.villages) {
            if (v.isLoaded()) {
                v.villageReport(reportType);
            }
        }
    }

    public void fromTag(CompoundTag nbt) {
    }


    public CompoundTag toTag(CompoundTag compound) {
        return compound;
    }
}
*/
