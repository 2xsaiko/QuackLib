package therealfarfetchd.quacklib.api.block.init

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import therealfarfetchd.quacklib.api.block.component.AppliedComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.render.BlockRenderer
import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.item.ItemType

@InitDSL
interface BlockConfigurationScope : BlockConfiguration {

  /**
   *
   */
  override var material: Material

  /**
   *
   */
  override var soundType: SoundType

  /**
   * The hardness of the block. Set to 'null' to make the block unbreakable.
   */
  override var hardness: Float?

  /**
   *
   */
  override var needsTool: Boolean

  /**
   *
   */
  override var validTools: Set<Tool>

  override var item: ItemType?

  /**
   *
   */
  fun <T : BlockComponent> apply(component: T): AppliedComponent<T>

  /**
   *
   */
  fun apply(renderer: BlockRenderer)

  /**
   *
   */
  fun link(op: BlockDataLinkScope.() -> Unit)

}