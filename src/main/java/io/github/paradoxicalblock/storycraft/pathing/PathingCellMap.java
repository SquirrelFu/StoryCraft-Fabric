package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class PathingCellMap {
    private final int defaultCapacity;
    private int nodeCount;
    private BasePathingNode firstNode;
    private Map<Integer, Map<Integer, Set<BasePathingNode>>> baseNodes;
    private NavigableSet<BasePathingNode> edgeNodes;
    private Random rnd;

    public PathingCellMap(int defaultMapCapacity) {
        this.nodeCount = 0;
        this.firstNode = null;

        this.edgeNodes = new TreeSet<>(Comparator.comparingInt(a -> (int) (a).getBlockPos().getSquaredDistance(this.firstNode.getBlockPos())));
        this.rnd = new Random();


        this.defaultCapacity = defaultMapCapacity;
        this.baseNodes = new HashMap<>(this.defaultCapacity);
    }

    public void putNode(BasePathingNode node, World world) {
        if (this.firstNode == null) {
            this.firstNode = node;
            this.edgeNodes.add(node);
        }

        Map<Integer, Set<BasePathingNode>> zMap = this.baseNodes.computeIfAbsent((node.getCell()).x, k -> new HashMap<>(this.defaultCapacity));

        Set<BasePathingNode> nodeSet = zMap.computeIfAbsent((node.getCell()).z, k -> new HashSet<>());

        if (this.rnd.nextInt(30) == 0) {
            int edgeDist = getAxisDistance(this.firstNode.getBlockPos(), node.getBlockPos());
            if (edgeDist < 115 && world.isSkyVisible(node.getBlockPos())) {
                this.edgeNodes.add(node);
                if (this.edgeNodes.size() > 10) {
                    this.edgeNodes.pollFirst();
                }
            }
        }
        if (!nodeSet.add(node)) {
            throw new IllegalArgumentException("Duplicate BasePathingNode encountered");
        }
        this.nodeCount++;
    }


    private int getAxisDistance(BlockPos bp1, BlockPos bp2) {
        return Math.max(Math.abs(bp1.getX() - bp2.getX()), Math.abs(bp1.getZ() - bp2.getZ()));
    }


    public void removeNode(BasePathingNode node, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = getXZSet((node.getCell()).x, (node.getCell()).z);
        if (nodeSet != null && nodeSet.remove(node)) {
            node.destroy(graph);
            this.nodeCount--;
        }
    }


    public int nodeCount() {
        return this.nodeCount;
    }


    public BasePathingNode getEdgeNode(BlockPos origin, double minDist) {
        if (!this.edgeNodes.isEmpty()) {

            int index = this.rnd.nextInt(this.edgeNodes.size());
            int i = 0;
            for (BasePathingNode edgeNode : this.edgeNodes) {
                if (i == index) {
                    return edgeNode;
                }
                i++;
            }
        }

        return null;
    }

    public void debugEdgeNodes(World world) {
        for (BasePathingNode node : this.edgeNodes) {
            System.out.println("Edge Node at " + node.getBlockPos());
            ArmorStandEntity ent = new ArmorStandEntity(world, node.getBlockPos().getX(), node.getBlockPos().getY(), node.getBlockPos().getZ());
            ent.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200));
            ent.setHealth(0.0F);
            ent.deathTime = -200;
            world.spawnEntity(ent);
        }
    }


    public BasePathingNode getNode(int x, int y, int z) {
        return getNodeYRange(x, y, y, z);
    }


    public BasePathingNode getNodeYRange(int x, int y1, int y2, int z) {
        Set<BasePathingNode> nodeSet = getXZSet(x, z);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if ((node.getCell()).y >= y1 && (node.getCell()).y <= y2) {
                    return node;
                }
            }
        }
        return null;
    }

    public void updateNodes(int x, int y1, int y2, int z, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = getXZSet(x, z);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if ((node.getCell()).y >= y1 && (node.getCell()).y <= y2) {
                    graph.addFirstNode(node);
                }
            }
        }
    }

    private Set<BasePathingNode> getXZSet(int x, int z) {
        Map<Integer, Set<BasePathingNode>> zMap = this.baseNodes.get(Integer.valueOf(x));
        if (zMap != null) {
            return zMap.get(Integer.valueOf(z));
        }

        return null;
    }

    public Set<PathingNode> getTopNodes() {
        PathingNode topNode = this.firstNode.getTopParent();
        Set<PathingNode> outNodes = new HashSet<PathingNode>();
        fillConnections(topNode, outNodes);
        return outNodes;
    }


    public void notifyListenerInitial(World world, ServerPlayerEntity player) {
        List<ServerPlayerEntity> listeners = new ArrayList<ServerPlayerEntity>(1);
        listeners.add(player);
        for (Map<Integer, Set<BasePathingNode>> zMap : this.baseNodes.values()) {
            for (Set<BasePathingNode> nodeSet : zMap.values()) {
                for (BasePathingNode node : nodeSet) {
                    node.notifyListeners(world, listeners);
                }
            }
        }
    }

    private void fillConnections(PathingNode node, Set<PathingNode> outNodes) {
        if (!outNodes.contains(node)) {
            outNodes.add(node);
            for (PathingNode peer : node.connections) {
                fillConnections(peer, outNodes);
            }
        }
    }

    public BasePathingNode randomNode() {
        int numX = (int) (Math.random() * this.baseNodes.size());
        for (Map<Integer, Set<BasePathingNode>> xMap : this.baseNodes.values()) {
            if (--numX < 0) {
                int numZ = (int) (Math.random() * xMap.size());
                for (Set<BasePathingNode> zSet : xMap.values()) {
                    if (--numZ < 0) {
                        int numY = (int) (Math.random() * zSet.size());
                        for (BasePathingNode node : zSet) {
                            if (--numY < 0) {
                                return node;
                            }
                        }
                    }
                }
            }
        }
        throw new AssertionError();
    }
}
