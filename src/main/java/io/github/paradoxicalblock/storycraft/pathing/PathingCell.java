package io.github.paradoxicalblock.storycraft.pathing;

import net.minecraft.util.math.BlockPos;

public class PathingCell {
    public final int x;
    public final int y;
    public final int z;
    public final byte level;

    public PathingCell(BlockPos bp, byte level) {
        this(bp.getX(), bp.getY(), bp.getZ(), level);
    }


    private PathingCell(int x, int y, int z, byte level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.level = level;
    }

    public static int hashCode(int x, int z) {
        return 31 * x + z;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public PathingCell up() {
        return up((byte) 1);
    }

    public PathingCell up(byte levels) {
        return new PathingCell(this.x >> levels, this.y >> levels, this.z >> levels, (byte) (this.level + levels));
    }

    public boolean equals(Object o) {
        if (!(o instanceof PathingCell)) {
            return false;
        }
        PathingCell other = (PathingCell) o;
        return (this.x == other.x && this.y == other.y && this.z == other.z && this.level == other.level);
    }

    public String toString() {
        return "[" + this.level + "][" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public int hashCode() {
        return hashCode(this.x, this.z);
    }
}
