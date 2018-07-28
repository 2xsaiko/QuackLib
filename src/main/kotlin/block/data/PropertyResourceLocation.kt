package therealfarfetchd.quacklib.block.data

import net.minecraft.util.ResourceLocation

class PropertyResourceLocation(namespace: String, path: String, val property: String) : ResourceLocation(namespace, path) {

  constructor(rl: ResourceLocation, property: String) : this(rl.namespace, rl.path, property)

  val base = ResourceLocation(namespace, path)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as PropertyResourceLocation

    if (property != other.property) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + property.hashCode()
    return result
  }

  override fun toString(): String {
    return "${super.toString()}#$property"
  }

}