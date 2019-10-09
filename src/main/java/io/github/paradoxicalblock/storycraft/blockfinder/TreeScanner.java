/*
package io.github.paradoxicalblock.storycraft.blockfinder;

import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class TreeScanner extends BlockScanner {
  
    public TreeScanner(Village v, int scansPerTick) {
        super(v, scansPerTick, Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.SPRUCE_LOG);
    }

    public static BlockPos treeTest(World world, BlockPos bp) {
        while (isLog(world.getBlockState(bp))) {
            bp = bp.down();


            if (world.getBlockState(bp).getBlock() == Blocks.DIRT) {

                BlockPos treePos = bp.up();


                bp = bp.up(3);
                for (int i = 0; i < 9; i++) {

                    BlockState westBlock = world.getBlockState(bp.west());
                    BlockState eastBlock = world.getBlockState(bp.east());
                    if ((isLeaf(westBlock) || isLog(westBlock)) && (isLeaf(eastBlock) || isLog(eastBlock))) {
                        return treePos;
                    }
                    bp = bp.up();
                }
                return null;
            }
        }

        return null;
    }

    public static boolean isLog(BlockState blockState) {
        return (blockState.getBlock() == Blocks.ACACIA_LOG) || (blockState.getBlock() == Blocks.BIRCH_LOG)
                || (blockState.getBlock() == Blocks.DARK_OAK_LOG) || (blockState.getBlock() == Blocks.JUNGLE_LOG)
                || (blockState.getBlock() == Blocks.OAK_LOG) || (blockState.getBlock() == Blocks.SPRUCE_LOG);
    }

    public static boolean isLeaf(BlockState blockState) {
        return (blockState.getBlock() == Blocks.ACACIA_LEAVES) || (blockState.getBlock() == Blocks.BIRCH_LEAVES)
                || (blockState.getBlock() == Blocks.DARK_OAK_LEAVES) || (blockState.getBlock() == Blocks.JUNGLE_LEAVES)
                || (blockState.getBlock() == Blocks.OAK_LEAVES) || (blockState.getBlock() == Blocks.SPRUCE_LEAVES);
    }

    public BlockPos testBlock(World w, BlockPos bp) {
        BlockState blockState = w.getBlockState(bp);
        if (isLeaf(blockState)) {
            return findTreeFromLeaf(w, bp);
        }

        return null;
    }

    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.iterate(bp.getX() - 7, bp.getY() + 2, bp.getZ() - 7, bp.getX() + 7, bp.getY() + 2, bp.getZ() + 7)) {
            scanBlock(scanPos);
        }
    }
  
    @Nullable
    protected BlockPos findTreeFromLeaf(World world, BlockPos leafPos) {
        for (BlockPos bp : BlockPos.iterate(leafPos.getX() - 2, leafPos.getY() - 1, leafPos.getZ() - 2, leafPos.getX() + 2, leafPos.getY() - 1, leafPos.getZ() + 2)) {
            BlockPos treePos = treeTest(world, bp);
            if (treePos != null) {
                return treePos;
            }
        }
        return null;
    }

}*/
