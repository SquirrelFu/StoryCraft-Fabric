package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathingNode {
    protected final PathingCell cell;
    public Set<PathingNode> connections;
    public Set<PathingNode> children;
    protected PathingNode parent;
    protected boolean queued;
    protected boolean destroyed;

    public PathingNode(PathingCell cell) {
        this.parent = null;
        this.queued = false;
        this.destroyed = false;
        this.connections = new HashSet<>();
        this.children = new HashSet<>();


        this.cell = cell;
    }

    protected static void connectNodes(PathingNode node1, PathingNode node2, PathingGraph graph) {
        node1.connections.add(node2);
        node1.checkParentLink(node2);

        node2.connections.add(node1);
        node2.checkParentLink(node1);


        if (node1.parent != node2.parent) {
            if (node1.parent != null && node1.connections.size() > 0) {
                graph.addLastNode(node1.parent);
            }
            if (node2.parent != null && node2.connections.size() > 0) {
                graph.addLastNode(node2.parent);
            }
        }
    }

    public PathingCell getCell() {
        return this.cell;
    }

    public BlockPos getBlockPos() {
        return getCell().getBlockPos();
    }

    public void process(World world, PathingCellMap cellMap, PathingGraph graph) {
        this.queued = false;
        updateConnections(world, cellMap, graph);
    }

    public int updateConnections(World world, PathingCellMap cellMap, PathingGraph graph) {
        Set<PathingNode> lastConnections = new HashSet<PathingNode>(this.connections);

        for (PathingNode child : this.children) {
            for (PathingNode childConnection : child.connections) {
                if (childConnection.parent != null &&
                        childConnection.parent != this) {
                    lastConnections.remove(childConnection.parent);
                    if (this.connections.contains(childConnection.parent)) {
                        checkParentLink(childConnection.parent);
                        continue;
                    }
                    connectNodes(this, childConnection.parent, graph);
                }
            }
        }


        for (PathingNode toBreak : lastConnections) {
            breakConnection(toBreak, graph);
        }


        if (this.parent == null && this.cell.level < 4) {
            this.parent = new PathingNode(getCell().up());
            this.parent.addChild(this);
            graph.addLastNode(this.parent);
        }


        return this.connections.size();
    }

    public void queue() {
        this.queued = true;
    }

    public boolean isQueued() {
        return this.queued;
    }

    public PathingNode getConnection(int x, int z) {
        for (PathingNode node : this.connections) {
            if ((node.getCell()).x == this.cell.x + x && (node.getCell()).z == this.cell.z + z) {
                return node;
            }
        }
        return null;
    }

    public boolean isConnected(PathingNode node) {
        return this.connections.contains(node);
    }

    protected void notifyListeners(World world, List<ServerPlayerEntity> listeners) {
        for (PathingNode child : this.children) {
            child.notifyListeners(world, listeners);
        }
    }

    protected void breakConnection(PathingNode node2, PathingGraph graph) {
        this.connections.remove(node2);
        node2.connections.remove(this);

        if (this.parent != node2.parent && node2.parent != null) {
            graph.addLastNode(node2.parent);
        }
    }

    protected void checkParentLink(PathingNode node) {
        if (this.parent == null && node.parent != null &&
                node.parent.cell.equals(this.cell.up())) {
            node.parent.addChild(this);
        }
    }


    protected void removeChild(PathingNode child) {
        child.parent = null;
        this.children.remove(child);
    }

    protected void addChild(PathingNode child) {
        child.parent = this;
        this.children.add(child);
    }


    public PathingNode getParent() {
        return this.parent;
    }

    public PathingNode getParent(int levels) {
        PathingNode p = this;
        while (p.parent != null && levels > 0) {
            levels--;
            p = p.parent;
        }

        return p;
    }

    public PathingNode getTopParent() {
        PathingNode p = this;
        while (p.parent != null) {
            p = p.parent;
        }
        return p;
    }


    public boolean isDestroyed() {
        return this.destroyed;
    }


    public void destroy(PathingGraph graph) {
        this.destroyed = true;
        new HashSet(this.connections).forEach(c -> breakConnection((PathingNode) c, graph));

        if (this.parent != null) {
            PathingNode par = this.parent;
            this.parent.removeChild(this);
            if (par.children.size() <= 0) {
                par.destroy(graph);
            }
        }
    }


    public String toString() {
        return this.cell.toString();
    }
}
