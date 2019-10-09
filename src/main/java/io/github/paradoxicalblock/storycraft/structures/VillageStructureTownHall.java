package io.github.paradoxicalblock.storycraft.structures;

import io.github.paradoxicalblock.storycraft.Village;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVendor;
import net.tangotek.tektopia.entities.EntityVillagerTek;

import java.util.List;
import java.util.function.Function;


public class VillageStructureTownHall extends VillageStructure {

    protected VillageStructureTownHall(World world, Village v, ItemFrameEntity itemFrame) {
        super(world, v, itemFrame, VillageStructureType.TOWNHALL, "Town Hall");
    }

    protected void onFloorScanEnd() {
        super.onFloorScanEnd();

        if (this.village.isValid() && this.village.getResidentCount() < 1 && !this.village.getTownData().isEmpty() && !this.village.getTownData().completedStartingGifts()) {
            this.village.getTownData().executeStartingGifts(this.world, this.village, this.safeSpot);
        }
    }


    protected void setupServerJobs() {
        addJob(new TickJob(200, 0, true, () -> {
            trySpawnVendor(EntityArchitect.class, ());
            trySpawnVendor(EntityTradesman.class, ());
        }));

        super.setupServerJobs();
    }

    private <T extends EntityVendor> void trySpawnVendor(Class<T> clazz, Function<World, T> createFunc) {
        List<T> merchantList = this.world.getEntitiesWithinAABB(clazz, getAABB().grow(2.0D, 3.0D, 2.0D));

        while (merchantList.size() > 1) {
            if (merchantList.get(0) instanceof EntityVendor) {
                ((EntityVendor) merchantList.get(0)).setDead();
            }
            merchantList.remove(0);
        }

        if (merchantList.isEmpty()) {
            BlockPos pos = getRandomFloorTile();
            if (pos != null) {
                T vendor = createFunc.apply(this.world);
                vendor.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
                vendor.onInitialSpawn(this.world.getDifficultyForLocation(pos), (IEntityLivingData) null);
                this.world.spawnEntity(vendor);
            }
        }
    }


    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return (this.world.rand.nextInt(3) == 0);
    }


    public int getSitTime(EntityVillagerTek villager) {
        return 100 + villager.getRNG().nextInt(300);
    }


    public int getMaxAllowed() {
        return 1;
    }
}
