/*
package io.github.paradoxicalblock.storycraft.blockfinder;

import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class SugarCaneScanner extends BlockScanner {

    public SugarCaneScanner(Village v, int scansPerTick) {
        super(Blocks.SUGAR_CANE, v, scansPerTick);
    }

    public static BlockPos getCaneStalk(World w, BlockPos bp) {
        if (isCane(w.getBlockState(bp))) {

            do {
                bp = bp.down();
            } while (isCane(w.getBlockState(bp)));


            Block downBlock = w.getBlockState(bp.down()).getBlock();
            if (downBlock == Blocks.GLOWSTONE) {
                return null;
            }


            if (isCane(w.getBlockState(bp.up(2)))) {
                return bp.up();
            }
        }
        return null;
    }

    public static boolean isCane(BlockState blockState) {
        return (blockState.getBlock() == Blocks.SUGAR_CANE);
    }

    public BlockPos testBlock(World w, BlockPos bp) {
        return getCaneStalk(w, bp);
    }

    public void scanNearby(BlockPos bp) {
        for (BlockPos scanPos : BlockPos.iterate(bp.getX() - 2, bp.getY(), bp.getZ() - 2, bp.getX() + 2, bp.getY(), bp.getZ() + 2)) {
            scanBlock(scanPos);
        }
    }
}
*/
