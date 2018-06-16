package therealfarfetchd.quacklib.block.data

import net.minecraft.util.ResourceLocation

class PropertyResourceLocation(resourceDomain: String, resourcePath: String, val property: String) : ResourceLocation(resourceDomain, resourcePath) {

  constructor(rl: ResourceLocation, property: String) : this(rl.resourceDomain, rl.resourcePath, property)

  val base = ResourceLocation(resourceDomain, resourcePath)

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