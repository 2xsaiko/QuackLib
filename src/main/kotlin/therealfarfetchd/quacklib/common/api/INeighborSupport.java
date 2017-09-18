package therealfarfetchd.quacklib.common.api;

import net.minecraft.util.EnumFacing;
import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation;

@FunctionalInterface
public interface INeighborSupport<T> {
    T getNeighborAt(EnumFaceLocation edge);

    default T getNeighborAt(EnumFacing facing) {
        return getNeighborAt(EnumFaceLocation.fromFaces(facing, null));
    }
}