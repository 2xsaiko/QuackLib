package therealfarfetchd.quacklib.hax

import net.minecraft.launchwrapper.IClassTransformer

class QuackLibTransformer : IClassTransformer {
  override fun transform(name: String?, transformedName: String?, basicClass: ByteArray?): ByteArray? {
    // don't throw up on loading kotlin classes
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    if ((name as java.lang.String?)?.startsWith("kotlin") != false)
      return basicClass
    return transform0(name!!, transformedName!!, basicClass)
  }

  fun transform0(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
    return basicClass
  }
}