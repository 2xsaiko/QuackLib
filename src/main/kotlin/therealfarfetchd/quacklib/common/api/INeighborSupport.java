package therealfarfetchd.quacklib.common.api;

import net.minecraft.util.EnumFacing;

@FunctionalInterface
public interface INeighborSupport<T> {
    T getNeighborAt(EnumFacing facing);
}