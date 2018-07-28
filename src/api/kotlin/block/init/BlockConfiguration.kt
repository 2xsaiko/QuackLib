package therealfarfetchd.quacklib.api.block.init

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.core.Describable
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.model.BlockModel

interface BlockConfiguration : Describable {

  /**
   *
   */
  val name: String

  /**
   *
   */
  val rl: ResourceLocation

  /**
   *
   */
  val material: Material

  /**
   *
   */
  val soundType: SoundType

  /**
   * The hardness of the block. 'null' for unbreakable.
   */
  val hardness: Float?

  /**
   *
   */
  val needsTool: Boolean

  /**
   *
   */
  val validTools: Set<Tool>

  /**
   *
   */
  val components: List<BlockComponent>

  /**
   *
   */
  val renderers: List<BlockModel>

  /**
   * The item this block is represented by.
   */
  val item: ItemType?

  /**
   *
   */
  val isMultipart: Boolean

  override fun describe(): String = "Block $name"

}