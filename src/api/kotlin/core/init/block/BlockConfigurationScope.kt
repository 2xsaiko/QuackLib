package therealfarfetchd.quacklib.api.core.init.block

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.Tool
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.render.BlockRenderer
import therealfarfetchd.quacklib.api.core.init.InitDSL

@InitDSL
interface BlockConfigurationScope {

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
  var material: Material

  /**
   * The hardness of the block. 'null' for unbreakable.
   */
  var hardness: Float?

  /**
   *
   */
  var needsTool: Boolean

  /**
   *
   */
  var validTools: Set<Tool>

  /**
   *
   */
  fun apply(component: BlockComponent)

  /**
   *
   */
  fun apply(renderer: BlockRenderer)

}