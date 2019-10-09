/*
package io.github.paradoxicalblock.storycraft.blockfinder;

import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class SaplingScanner extends BlockScanner {

    public SaplingScanner(Village v, int scansPerTick) {
        super(v, scansPerTick, Blocks.ACACIA_SAPLING, Blocks.BIRCH_SAPLING, Blocks.DARK_OAK_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING);
    }

    public static boolean isSapling(BlockState blockState) {
        return (blockState.getBlock() == Blocks.ACACIA_SAPLING) || (blockState.getBlock() == Blocks.BIRCH_SAPLING)
                || (blockState.getBlock() == Blocks.DARK_OAK_SAPLING) || (blockState.getBlock() == Blocks.JUNGLE_SAPLING)
                || (blockState.getBlock() == Blocks.OAK_SAPLING) || (blockState.getBlock() == Blocks.SPRUCE_SAPLING);
    }

    public BlockPos testBlock(World w, BlockPos bp) {
        BlockState blockState = w.getBlockState(bp);
        if (isSapling(blockState)) {
            return bp;
        }

        return null;
    }

    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.iterate(bp.getX() - 7, bp.getY() - 2, bp.getZ() - 7, bp.getX() + 7, bp.getY() + 2, bp.getZ() + 7)) {
            scanBlock(scanPos);
        }
    }
}
*/
