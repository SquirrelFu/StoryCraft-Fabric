package io.github.paradoxicalblock.storycraft.pathing;

import io.github.paradoxicalblock.storycraft.Village;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PathingGraph {
    protected final World world;
    protected final Village village;
    private final PathingCellMap baseCellMap;
    private int nodesVerified;
    private Deque<PathingNode> nodeProcessQueue;
    private boolean initialQueueComplete;
    private List<ServerPlayerEntity> listeners;

    public PathingGraph(World worldIn, Village v) {
        this.nodesVerified = 0;


        this.nodeProcessQueue = new LinkedList<>();
        this.initialQueueComplete = false;
        this.listeners = new ArrayList<>();


        this.world = worldIn;
        this.village = v;

        this.baseCellMap = new PathingCellMap(120);
    }

    public int nodeCount() {
        return this.baseCellMap.nodeCount();
    }


    public boolean isProcessing() {
        return (!this.nodeProcessQueue.isEmpty() || this.baseCellMap.nodeCount() <= 0);
    }


    public void addListener(ServerPlayerEntity player) {
        this.listeners.add(player);
        this.baseCellMap.notifyListenerInitial(this.world, player);
    }


    public void removeListener(ServerPlayerEntity player) {
        this.listeners.remove(player);
    }


    public void seedVillage(BlockPos bp) {
        byte clearanceHeight = 0;
        if (BasePathingNode.isPassable(this.world, bp) && BasePathingNode.isPassable(this.world, bp.up())) {
            clearanceHeight = 2;
            if (BasePathingNode.isPassable(this.world, bp.up(2))) {
                clearanceHeight = (byte) (clearanceHeight + 1);
            }
        }
        if (clearanceHeight >= 2) {
            BasePathingNode baseNode = new BasePathingNode(bp, clearanceHeight);
            this.baseCellMap.putNode(baseNode, this.world);
            this.nodeProcessQueue.addLast(baseNode);
        }
    }


    public void update() {
        processNodeQueue();
    }


    private void processNodeQueue() {
        int nodesProcessed = 0;
        int throttle = 16000;


        while (!this.nodeProcessQueue.isEmpty() && nodesProcessed < 16000) {
            PathingNode node = this.nodeProcessQueue.pollFirst();
            if (node != null) {
                if (node.isDestroyed()) {
                    boolean bool = true;
                } else {

                    node.process(this.world, this.baseCellMap, this);
                    if (!this.listeners.isEmpty()) {
                        node.notifyListeners(this.world, this.listeners);
                    }
                }
            }
            nodesProcessed++;
        }

        if (this.nodeProcessQueue.isEmpty() && this.baseCellMap.nodeCount() > 1000) {
            this.initialQueueComplete = true;
        }
    }


    public boolean isInitialQueueComplete() {
        return this.initialQueueComplete;
    }


    /*public boolean isInRange(BlockPos bp) {
        return this.village.isInVillage(bp);
    }*/


    public void addFirstNode(PathingNode node) {
        if (node.isDestroyed()) {
            return;
        }
        if (!node.isQueued()) {
            node.queue();
            this.nodeProcessQueue.addFirst(node);
        }
    }

    public void addLastNode(PathingNode node) {
        if (node.isDestroyed()) {
            return;
        }
        if (!node.isQueued()) {
            node.queue();

            for (PathingNode child : node.children) {
                assert child.parent == node;
            }

            this.nodeProcessQueue.addLast(node);
        }
    }


    private void verifyNode(PathingNode node) {
        for (int i = 0; i < 4 - (node.getCell()).level; i++) {
            System.out.print("    ");
        }
        System.out.print("->" + node.getCell());
        this.nodesVerified++;

        if ((node.getCell()).level == 1 && node.children.size() > 4) {
            System.err.println("Node with > 4 children " + node);
        }
        if ((node.getCell()).level > 0 && node.children.size() < 1) {
            System.err.println("Level " + (node.getCell()).level + " with no children");
        }
        System.out.print("      Connections: ");
        for (PathingNode connect : node.connections) {
            System.out.print(connect.cell + "  ");
        }
        System.out.print("\n");


        for (PathingNode child : node.children) {
            if (child.parent != node) {
                System.err.println("child/parent mismatch");
            }
            for (PathingNode childConnect : child.connections) {
                if (childConnect.parent != node &&
                        !node.isConnected(childConnect.parent)) {
                    System.err.println("Node " + node + " not connected to neighbor child " + child + " parent " + childConnect.parent);
                }
            }

            verifyNode(child);
        }
    }

    public void onBlockUpdate(World world, BlockPos bp) {
        BasePathingNode baseNode = this.baseCellMap.getNodeYRange(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ());
        while (baseNode != null) {
            this.baseCellMap.removeNode(baseNode, this);
            baseNode.notifyListeners(world, this.listeners);
            baseNode = this.baseCellMap.getNodeYRange(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ());
        }


        this.baseCellMap.updateNodes(bp.getX() + 1, bp.getY() - 2, bp.getY() + 1, bp.getZ(), this);
        this.baseCellMap.updateNodes(bp.getX() - 1, bp.getY() - 2, bp.getY() + 1, bp.getZ(), this);
        this.baseCellMap.updateNodes(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ() + 1, this);
        this.baseCellMap.updateNodes(bp.getX(), bp.getY() - 2, bp.getY() + 1, bp.getZ() - 1, this);


        if (isInitialQueueComplete()) {
            processNodeQueue();
        }
    }


    public void onChunkUnloaded(Chunk chunk) {
    }


    public void onChunkLoaded(Chunk chunk) {
    }


    public boolean isInGraph(BlockPos bp) {
        return (getBaseNode(bp.getX(), bp.getY(), bp.getZ()) != null);
    }


    public BasePathingNode getBaseNode(int x, int y, int z) {
        return this.baseCellMap.getNode(x, y, z);
    }


    public BasePathingNode getNodeYRange(int x, int y1, int y2, int z) {
        return this.baseCellMap.getNodeYRange(x, y1, y2, z);
    }


    public BasePathingNode getNearbyBaseNode(Vec3d pos, double widthX, double height, double widthZ) {
        BasePathingNode node = getBaseNode((int) pos.x, (int) pos.y, (int) pos.z);
        if (node == null) {
            double halfX = widthX / 2.0D;
            double halfZ = widthZ / 2.0D;
            BlockPos corner1 = new BlockPos(pos.x - halfX, pos.y - 1.0D, pos.z - halfZ);
            BlockPos corner2 = new BlockPos(pos.x + halfX, pos.y + height, pos.z + halfZ);

            for (BlockPos blockPos : BlockPos.iterate(corner1, corner2)) {
                node = getBaseNode(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (node != null) {
                    break;
                }
            }
        }
        return node;
    }

    public void debugEdgeNodes(World world) {
        this.baseCellMap.debugEdgeNodes(world);
    }

    public BasePathingNode getEdgeNode(BlockPos origin, Double minDist) {
        return this.baseCellMap.getEdgeNode(origin, minDist.doubleValue());
    }
}
