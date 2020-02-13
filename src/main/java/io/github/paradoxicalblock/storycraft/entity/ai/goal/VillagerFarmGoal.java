package io.github.paradoxicalblock.storycraft.entity.ai.goal;

import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class VillagerFarmGoal extends MoveToTargetPosGoal {
    private final FamiliarsEntity owner;
    private boolean seed;
    private boolean breed;
    private int field_6456;

    public VillagerFarmGoal(FamiliarsEntity villagerEntity_1, double double_1) {
        super(villagerEntity_1, double_1, 16);
        this.owner = villagerEntity_1;
    }

    public boolean canStart() {
        if (this.cooldown <= 0) {
            if (!this.owner.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }

            this.field_6456 = -1;
            this.seed = this.owner.hasSeed();
            this.breed = this.owner.canBreed();
        }

        return super.canStart();
    }

    public boolean shouldContinue() {
        return this.field_6456 >= 0 && super.shouldContinue();
    }

    public void tick() {
        super.tick();
        this.owner.getLookControl().lookAt((double) this.targetPos.getX() + 0.5D, this.targetPos.getY() + 1, (double) this.targetPos.getZ() + 0.5D, 10.0F, (float) this.owner.getLookPitchSpeed());
        if (this.hasReached()) {
            IWorld iWorld_1 = this.owner.world;
            BlockPos blockPos_1 = this.targetPos.up();
            BlockState blockState_1 = iWorld_1.getBlockState(blockPos_1);
            Block block_1 = blockState_1.getBlock();
            if (this.field_6456 == 0 && block_1 instanceof CropBlock && ((CropBlock) block_1).isMature(blockState_1)) {
                iWorld_1.breakBlock(blockPos_1, true);
            } else if (this.field_6456 == 1 && blockState_1.isAir()) {
                BasicInventory basicInventory_1 = this.owner.getInventory();

                for (int int_1 = 0; int_1 < basicInventory_1.getInvSize(); ++int_1) {
                    ItemStack itemStack_1 = basicInventory_1.getInvStack(int_1);
                    boolean boolean_1 = false;
                    if (!itemStack_1.isEmpty()) {
                        if (itemStack_1.getItem() == Items.WHEAT_SEEDS) {
                            iWorld_1.setBlockState(blockPos_1, Blocks.WHEAT.getDefaultState(), 3);
                            boolean_1 = true;
                        } else if (itemStack_1.getItem() == Items.POTATO) {
                            iWorld_1.setBlockState(blockPos_1, Blocks.POTATOES.getDefaultState(), 3);
                            boolean_1 = true;
                        } else if (itemStack_1.getItem() == Items.CARROT) {
                            iWorld_1.setBlockState(blockPos_1, Blocks.CARROTS.getDefaultState(), 3);
                            boolean_1 = true;
                        } else if (itemStack_1.getItem() == Items.BEETROOT_SEEDS) {
                            iWorld_1.setBlockState(blockPos_1, Blocks.BEETROOTS.getDefaultState(), 3);
                            boolean_1 = true;
                        }
                    }

                    if (boolean_1) {
                        itemStack_1.decrement(1);
                        if (itemStack_1.isEmpty()) {
                            basicInventory_1.setInvStack(int_1, ItemStack.EMPTY);
                        }
                        break;
                    }
                }
            }

            this.field_6456 = -1;
            this.cooldown = 10;
        }

    }

    protected boolean isTargetPos(WorldView viewableWorld_1, BlockPos blockPos_1) {
        Block block_1 = viewableWorld_1.getBlockState(blockPos_1).getBlock();
        if (block_1 == Blocks.FARMLAND) {
            blockPos_1 = blockPos_1.up();
            BlockState blockState_1 = viewableWorld_1.getBlockState(blockPos_1);
            block_1 = blockState_1.getBlock();
            if (block_1 instanceof CropBlock && ((CropBlock) block_1).isMature(blockState_1) && this.breed && (this.field_6456 == 0 || this.field_6456 < 0)) {
                this.field_6456 = 0;
                return true;
            }

            if (blockState_1.isAir() && this.seed && (this.field_6456 == 1 || this.field_6456 < 0)) {
                this.field_6456 = 1;
                return true;
            }
        }

        return false;
    }
}
