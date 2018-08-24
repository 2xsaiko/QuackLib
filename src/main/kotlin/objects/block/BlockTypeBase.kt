package therealfarfetchd.quacklib.objects.block

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.objects.block.BlockType

abstract class BlockTypeBase(override val registryName: ResourceLocation) : BlockType {

  override fun equals(other: Any?): Boolean =
    other is BlockType && other.registryName == registryName

  override fun hashCode(): Int =
    registryName.hashCode()

  override fun toString(): String {
    return "Block '$registryName'"
  }

}