package therealfarfetchd.quacklib.block.render

import net.minecraft.block.state.IBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.PropertyType
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyBlock
import therealfarfetchd.quacklib.block.data.render.PropertyData

data class BlockRenderStateImpl(val block: BlockType, val state: IBlockState) : BlockRenderState {

  override fun <T> get(rp: SimpleModel.RenderParam<T>): T {
    return rp.getValueBlock(block, this)
  }

  override fun <T> getValue(value: RenderProperty<*, Block, T>): T {
    value as RenderPropertyBlock<*, T>

    val pt = unsafe { value.getMCProperty() }

    @Suppress("UNCHECKED_CAST")
    return when (pt) {
      is PropertyType.Standard -> state.getValue((pt.prop) as PropertyData<T>).value
      is PropertyType.Extended -> (state as IExtendedBlockState).getValue(pt.prop)
    }
  }

}