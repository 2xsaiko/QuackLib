package therealfarfetchd.quacklib.render.vanilla

import net.minecraft.util.ResourceLocation

interface TextureSubstitution {

}

data class DirectTexture(val rl: ResourceLocation) : TextureSubstitution {

}

data class TextureRef(val spec: String) : TextureSubstitution {

}