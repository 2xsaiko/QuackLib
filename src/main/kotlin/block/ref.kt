package therealfarfetchd.quacklib.block

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.block.BlockReference

data class BlockReferenceDirect(override val mcBlock: Block) : BlockReference {

  override val rl: ResourceLocation
    get() = mcBlock.registryName!!

}

data class BlockReferenceByRL(override val rl: ResourceLocation) : BlockReference {

  var block: Block? = null

  override val mcBlock: Block
    get() = block?.let {
      it
    } ?: run {
      val i = ForgeRegistries.BLOCKS.getValue(rl)
      if (i != null) {
        block = i
        i
      } else {
        Blocks.AIR
      }
    }

}