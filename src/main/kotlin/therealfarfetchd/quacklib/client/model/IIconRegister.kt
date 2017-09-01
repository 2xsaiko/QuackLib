package therealfarfetchd.quacklib.client.model

import net.minecraft.client.renderer.texture.TextureMap

interface IIconRegister {
  fun registerIcons(textureMap: TextureMap)

  companion object {
    internal var iconRegisters: Set<IIconRegister> = emptySet()
  }
}

fun IIconRegister.registerIconRegister() {
  IIconRegister.iconRegisters += this
}