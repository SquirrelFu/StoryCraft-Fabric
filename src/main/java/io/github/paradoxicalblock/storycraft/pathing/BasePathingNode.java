package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class BasePathingNode extends PathingNode {

    private final byte clearanceHeight;
    private long updateTick = 0L;

    public BasePathingNode(BlockPos bp, byte ch) {
        super(new PathingCell(bp, (byte) 0));
        this.clearanceHeight = ch;
        this.updateTick = System.currentTimeMillis();
    }

    public static boolean isPassable(World world, BlockPos bp) {
        BlockState blockState = world.getBlockState(bp);
        if (blockState.getMaterial().isLiquid())
            return false;
        if (blockState.getBlock().isSimpleFullBlock(blockState, world, bp))
            return true;
//        return isPortal(world, bp);
        return false;
    }

    /*private static boolean isPortal(World world, BlockPos bp) {
        if (VillageStructure.isWoodDoor(world, bp))
            return true;
        return VillageStructure.isGate(world, bp);
    }*/

    public static boolean canWalkOn(World world, BlockPos bp) {
        if (!isPassable(world, bp)) {
            BlockState blockState = world.getBlockState(bp);
            if (blockState.getMaterial().isLiquid()) {
                return false;
            }


            return !(blockState.getBlock() instanceof net.minecraft.block.FenceBlock) && !(blockState.getBlock() instanceof net.minecraft.block.WallBlock);
        }

        return false;
    }

    public byte getClearanceHeight() {
        return this.clearanceHeight;
    }

    public long getUpdateTick() {
        return this.updateTick;
    }

    public int updateConnections(World world, PathingCellMap cellMap, PathingGraph graph) {
        this.updateTick = System.currentTimeMillis();
        checkConnection(world, cellMap, graph, 1, 0);
        checkConnection(world, cellMap, graph, -1, 0);
        checkConnection(world, cellMap, graph, 0, 1);
        checkConnection(world, cellMap, graph, 0, -1);


        if (this.parent == null) {
            this.parent = new PathingNode(getCell().up());
            this.parent.addChild(this);
            graph.addLastNode(this.parent);
        }


        return 0;
    }

    private boolean checkConnection(World world, PathingCellMap cellMap, PathingGraph graph, int x, int z) {
        if (!graph.isInRange(getBlockPos().add(x, 0, z))) {
            return false;
        }
        PathingNode connected = getConnection(x, z);
        if (connected == null) {

            boolean newNode = false;
            BasePathingNode node = getExistingNeighbor(cellMap, x, z);
            if (node == null) {
                node = checkWalkableNeighbor(world, x, z);
                if (node != null) {
                    newNode = true;
                }
            }


            if (node != null && canWalkTo(node)) {

                connectNodes(this, node, graph);


                if (newNode) {
                    graph.addFirstNode(node);
                    cellMap.putNode(node, world);
                    return true;
                }

            }
        } else {

            checkParentLink(connected);
        }

        return false;
    }

    protected void notifyListeners(World world, List<ServerPlayerEntity> listeners) {
//        listeners.forEach(p -> TekVillager.NETWORK.sendTo(new PacketPathingNode(new PathingNodeClient(this)), p));
    }

    private BasePathingNode checkWalkableNeighbor(World world, int x, int z) {
        BlockPos bp = getBlockPos().add(x, 0, z);
        if (!canWalkOn(world, bp)) {
            bp = bp.down();
            if (!canWalkOn(world, bp)) {
                bp = bp.down();
                if (!canWalkOn(world, bp)) {
                    bp = null;
                }
            }
        }

        if (bp != null) {
            bp = bp.up();
            byte clearance = 0;
            if (isPassable(world, bp) && isPassable(world, bp.up(1))) {
                clearance = 2;
                if (isPassable(world, bp.up(2))) {
                    clearance = (byte) (clearance + 1);
                }
            }
            if (clearance >= 2) {
                return new BasePathingNode(bp, clearance);
            }
        }
        return null;
    }

    private boolean canWalkTo(BasePathingNode node) {
        return ((node.getCell()).y == (getCell()).y - 1 && node.getClearanceHeight() >= 3) ||
                (node.getCell()).y == (getCell()).y || (
                (node.getCell()).y == (getCell()).y + 1 && getClearanceHeight() >= 3);
    }


    private BasePathingNode getExistingNeighbor(PathingCellMap cellMap, int x, int z) {
        return cellMap.getNodeYRange((getCell()).x + x, (getCell()).y - 1, (getCell()).y + 1, (getCell()).z + z);
    }
}
