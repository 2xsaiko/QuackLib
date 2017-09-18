package therealfarfetchd.quacklib.common.api.util

import net.minecraftforge.common.property.IUnlistedProperty

/**
 * Injected into QBlocks when they don't have any unlisted properties as a way to force it to create an IExtendedBlockState.
 */
object DummyUnlistedProperty : IUnlistedProperty<Any> {
  override fun getName(): String = "\$DUMMY"

  override fun getType(): Class<Any> = Any::class.java

  override fun isValid(value: Any?): Boolean = value == null

  override fun valueToString(value: Any?): String = value.toString()
}