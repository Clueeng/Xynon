package fr.flaily.xynon.utils.path;
import net.minecraft.util.BlockPos;

public class PathNode {
    public BlockPos pos;
    public PathNode parent;
    public double gCost, hCost;

    public PathNode(BlockPos pos, PathNode parent, double gCost, double hCost) {
        this.pos = pos;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public double getFCost() {
        return gCost + hCost;
    }
}