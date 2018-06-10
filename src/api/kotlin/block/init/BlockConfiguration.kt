package therealfarfetchd.quacklib.api.block.init

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.item.Tool

interface BlockConfiguration {

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

}