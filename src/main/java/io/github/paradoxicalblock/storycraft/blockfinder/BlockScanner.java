/*
package io.github.paradoxicalblock.storycraft.blockfinder;

import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;

public abstract class BlockScanner {

    private final int scansPerTick;
    protected Village village;
    private Block scanBlock;
    private Queue<BlockPos> scannedBlocks;
    private Random rng;
    private long tickCount;
    private long releaseTick;
    private List<BlockPos> recentBlocks;
    private Map<BlockPos, Long> claimedBlocks;

    public BlockScanner(Block scanBlock, Village village, int scansPerTick) {
        this.rng = new Random();
        this.tickCount = 0L;
        this.releaseTick = 0L;
        this.recentBlocks = new LinkedList<>();
        this.claimedBlocks = new HashMap<>();

        this.scannedBlocks = new PriorityQueue<>(50, Comparator.comparingInt(a -> (int) a.getSquaredDistance(this.village.getCenter())));

        this.scanBlock = scanBlock;
        this.village = village;
        this.scansPerTick = scansPerTick;
    }

    public BlockScanner(Village village, int scansPerTick, Block... scanBlocks) {
        this.rng = new Random();
        this.tickCount = 0L;
        this.releaseTick = 0L;
        this.recentBlocks = new LinkedList<>();
        this.claimedBlocks = new HashMap<>();

        this.scannedBlocks = new PriorityQueue<>(50, Comparator.comparingInt(a -> (int) a.getSquaredDistance(this.village.getCenter())));

        for (Block block : scanBlocks) {
            this.scanBlock = block;
        }
        this.village = village;
        this.scansPerTick = scansPerTick;
    }

    public Block getScanBlock() {
        return this.scanBlock;
    }

    public void update() {
        this.tickCount++;

        if (!this.recentBlocks.isEmpty()) {
            BlockPos recent = this.recentBlocks.remove(0);
            scanNearby(recent);
        }

        for (int i = 0; i < this.scansPerTick; i++) {
            scanRandomBlock(this.rng.nextFloat());
        }
        if (this.releaseTick-- < 0L) {
            this.releaseTick = 100L;
            releaseClaimedBlocks();
        }
    }

    public boolean hasBlocks() {
        return !this.scannedBlocks.isEmpty();
    }

    public int getBlockCount() {
        return this.scannedBlocks.size();
    }

    public BlockPos requestBlock() {
        while (!this.scannedBlocks.isEmpty()) {
            BlockPos bp = this.scannedBlocks.poll();
            if (this.village.getWorld().getBlockState(bp).getBlock().equals(this.scanBlock)) {
                this.claimedBlocks.put(bp, this.tickCount);
                return bp;
            }
        }
        return null;
    }

    public void releaseClaim(BlockPos bp) {
        this.claimedBlocks.remove(bp);
    }

    protected void scanRandomBlock(float mod) {
        int radius = Math.max((int) (this.village.getSize() * mod), 20);
        int vertOffset = (int) (20.0F * mod) + 5;
        int X = this.village.getCenter().getX() + radius - this.rng.nextInt(radius * 2);
        int Y = MathHelper.nextInt(this.rng, (int) (this.village.getAABB()).minY - vertOffset, (int) (this.village.getAABB()).maxY + vertOffset);
        int Z = this.village.getCenter().getZ() + radius - this.rng.nextInt(radius * 2);
        scanBlock(new BlockPos(X, Y, Z));
    }

    protected void scanBlock(BlockPos testPos) {
        if (this.village.isInVillage(testPos)) {
            BlockPos targetPos = testBlock(this.village.getWorld(), testPos);
            if (targetPos != null &&
                    !this.scannedBlocks.contains(targetPos)) {
                if (!this.claimedBlocks.containsKey(targetPos)) {


                    this.scannedBlocks.add(targetPos);
                    this.recentBlocks.add(targetPos);
                }
            }
        }
    }

    protected void releaseClaimedBlocks() {
        Iterator<Map.Entry<BlockPos, Long>> itr = this.claimedBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<BlockPos, Long> entry = itr.next();
            long timeClaimed = this.tickCount - entry.getValue();
            if (timeClaimed > 2400L) {
                itr.remove();
            }
        }
    }

    public abstract BlockPos testBlock(World paramWorld, BlockPos paramBlockPos);

    protected abstract void scanNearby(BlockPos paramBlockPos);

}*/
