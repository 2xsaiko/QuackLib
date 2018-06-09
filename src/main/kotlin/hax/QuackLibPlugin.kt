package therealfarfetchd.quacklib.hax

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

class QuackLibPlugin : IFMLLoadingPlugin {
  override fun getModContainerClass(): String? = null

  override fun getASMTransformerClass(): Array<String> = arrayOf("therealfarfetchd.quacklib.hax.QuackLibTransformer")

  override fun getSetupClass(): String? = null

  override fun injectData(data: MutableMap<String, Any>?) {}

  override fun getAccessTransformerClass(): String? = null
}