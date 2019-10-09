/*
package io.github.paradoxicalblock.storycraft.entity;

import io.github.paradoxicalblock.storycraft.Village;
import io.github.paradoxicalblock.storycraft.VillageManager;
import io.github.paradoxicalblock.storycraft.VillagerRole;
import io.github.paradoxicalblock.storycraft.structures.VillageStructure;
import io.github.paradoxicalblock.storycraft.structures.VillageStructureType;
import io.github.paradoxicalblock.storycraft.tickjob.TickJob;
import io.github.paradoxicalblock.storycraft.tickjob.TickJobQueue;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.pathing.PathFinder;
import net.tangotek.tektopia.pathing.PathNavigateVillager2;

public abstract class EntityVillageNavigator extends MobEntityWithAi {
    protected final TickJobQueue jobs;
    private final int rolesMask;
    protected Village village;
    protected String curAnim = "";
    private int idleTicks;
    private boolean isWalking;
    private int aiTick = 0;
    private boolean aiReset = false;
    private boolean storagePriority = false;
    private boolean triggeredAnimationRunning = false;

    public EntityVillageNavigator(World worldIn, int rolesMask) {
        super(worldIn);
        this.rolesMask = rolesMask;
        this.jobs = new TickJobQueue();
        if (worldIn.isClient) {
            setupClientJobs();
        } else {
            setupServerJobs();
        }
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateVillager2(this, this.world, getCanUseDoors());
    }


    public void onAddedToWorld() {
        if (!this.world.isClient) {
            debugOut("onAddedToWorld " + getNavigator());
        }


        super.onAddedToWorld();
    }

    public boolean isAITick() {
        if (this.aiTick < 0) {
            this.aiReset = true;
            return true;
        }

        return false;
    }

    public boolean isStoragePriority() {
        return this.storagePriority;
    }

    public void setStoragePriority() {
        this.storagePriority = true;
    }

    protected void setupClientJobs() {
        this.jobs.clear();
    }

    protected void setupServerJobs() {
        this.jobs.clear();


        addJob(new TickJob(50, 100, true, () -> {
            BlockPos blockPos = new BlockPos(this);
            VillageManager vm = VillageManager.get(this.world);

            Village nearVillage = vm.getNearestVillage(blockPos, 360);
            if (nearVillage != getVillage()) {
                detachVillage();
            }
            boolean inVillageGraph = (nearVillage != null && nearVillage.getPathingGraph().getNearbyBaseNode(getPositionVector(), this.width, this.height, this.width) != null);


            if (!hasVillage()) {

                if (inVillageGraph) {
                    attachToVillage(nearVillage);
                }
            } else if (!inVillageGraph) {

                detachVillage();
            }

            if (!hasVillage()) {
                detachHome();
            } else {
                setHomePosAndDistance(this.village.getOrigin(), -1);
            }
        }));
    }

    public void addJob(TickJob job) {
        this.jobs.addJob(job);
    }

    public boolean isRole(VillagerRole role) {
        return ((this.rolesMask & role.value) > 0);
    }

    protected boolean getCanUseDoors() {
        return true;
    }

    public boolean canNavigate() {
        return true;
    }

    protected void attachToVillage(Village v) {
        this.village = v;
        debugOut("Attaching to village");
    }

    protected void detachVillage() {
        this.village = null;
    }

    public void debugOut(String text) {
        if (hasVillage()) {
            getVillage().debugOut(getClass().getSimpleName() + "|" + getDisplayName().getFormattedText() + "|" + getEntityId() + " " + ((text.charAt(0) == ' ') ? text : (" " + text)));
        }
    }

    public void playSound(SoundEvent soundEvent) {
        playSound(soundEvent, getRand().nextFloat() * 0.4F + 0.8F, getRand().nextFloat() * 0.4F + 0.8F);
    }

    public void startMovement() {
    }

    public void updateMovement(boolean arrived) {
    }

    public void resetMovement() {
        this.navigation.stop();
    }

    public void tick() {
        if (this.storagePriority && isAITick() && hasVillage()) {

            VillageStructure struct = this.village.getNearestStructure(VillageStructureType.STORAGE, getBlockPos());
            if (struct == null || !struct.isBlockNear(getBlockPos(), 3.0D)) {
                this.storagePriority = false;
            }
        }


        super.tick();

        if (!isWorldRemote()) {
            this.aiTick--;
            if (this.aiReset) {
                this.aiTick = getRand().nextInt(10) + 15;
                this.aiReset = false;
            }
        }

        if (isWorldRemote()) {
            if (this.lastTickPosX == this.x && this.lastTickPosZ == this.y && this.lastTickPosY == this.z) {
                this.idleTicks++;
            } else {
                this.idleTicks = 0;
            }
            this.isWalking = (this.idleTicks <= 1);
            if (this.isWalking && this.onGround && !this.triggeredAnimationRunning) {
                startWalking();
            } else {
                stopWalking();
            }
        }

        this.jobs.tick();
    }

    protected boolean isWalking() {
        return this.isWalking;
    }

    @SideOnly(Side.CLIENT)
    protected void startWalking() {
    }

    @SideOnly(Side.CLIENT)
    protected void stopWalking() {
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, 1, teleport);
    }

    public void faceLocation(double x, double z, float maxYawChange) {
        double dx = x - this.x;
        double dz = z - this.y;

        float f = (float) (MathHelper.atan2(dz, dx) * 57.29577951308232D) - 90.0F;
        this.rotationYaw = updateRotation(this.rotationYaw, f, maxYawChange);
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        return angle + f;
    }

    public boolean hasVillage() {
        return (this.village != null && this.village.isLoaded());
    }

    public Village getVillage() {
        if (hasVillage()) {
            return this.village;
        }
        return null;
    }

    public float getAIMoveSpeed() {
        return 0.35F;
    }

    public int getDimension() {
        return this.dimension.getRawId();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isWorldRemote() {
        return this.world.isClient;
    }

    public PathFinder getPathFinder() {
        return ((PathNavigateVillager2) getNavigator()).getVillagerPathFinder();
    }

}*/
