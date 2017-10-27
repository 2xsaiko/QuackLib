package therealfarfetchd.quacklib.client.api.render.wires

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.client.api.model.IIconRegister
import therealfarfetchd.quacklib.client.api.model.ModelBuilder
import therealfarfetchd.quacklib.client.api.model.SimpleModel
import therealfarfetchd.quacklib.common.api.util.Vec2
import therealfarfetchd.quacklib.common.api.wires.BlockWireCentered

class CenteredWireBakery(val textureLocation: ResourceLocation, val textureSize: Float, val width: Float) : SimpleModel(), IIconRegister {
  private val radius = width / 2
  private val scaleFactor: Float = textureSize / 16F
  private val rmin = 0.5 - radius / scaleFactor
  private val rmax = 0.5 + radius / scaleFactor

  private val side1Uv = Vec2(0.0f, 0.0f)
  private val centerUv = Vec2(0.0f, 0.5f - radius) / scaleFactor
  private val side2Uv = Vec2(0.0f, 0.5f + radius) / scaleFactor
  private val bottomUv = Vec2(0.0f, 1.0f) / scaleFactor
  private val cornerUv = Vec2(width, 0.0f) / scaleFactor

  lateinit var texture: TextureAtlasSprite

  override val particleTexture: TextureAtlasSprite
    get() = texture

  override fun addShapes(state: IExtendedBlockState, model: ModelBuilder) {
    var connections: Set<EnumFacing> = emptySet()

    if (state.getValue(BlockWireCentered.PropConnDown)) connections += EnumFacing.DOWN
    if (state.getValue(BlockWireCentered.PropConnUp)) connections += EnumFacing.UP
    if (state.getValue(BlockWireCentered.PropConnNorth)) connections += EnumFacing.NORTH
    if (state.getValue(BlockWireCentered.PropConnSouth)) connections += EnumFacing.SOUTH
    if (state.getValue(BlockWireCentered.PropConnWest)) connections += EnumFacing.WEST
    if (state.getValue(BlockWireCentered.PropConnEast)) connections += EnumFacing.EAST

    addShapes(connections, model)
  }

  override fun addShapes(stack: ItemStack, model: ModelBuilder) {
    addShapes(setOf(EnumFacing.UP, EnumFacing.DOWN), model)

    model {
      box {
        min = vec(rmin, 0.0, rmin)
        max = vec(rmax, 1.0, rmax)

        cull = false

        val tex = texture(texture, cornerUv, width, width)
        up = tex
        down = tex
      }
    }
  }

  private fun addShapes(connections: Set<EnumFacing>, model: ModelBuilder) = model {
    box {
      min = vec(rmin, rmin, rmin)
      max = vec(rmax, rmax, rmax)

      cull = false

      val straight = connections.size == 2 && connections.map { it.axis }.toSet().size == 1

      val axis = connections.firstOrNull()?.axis

      val tex = if (straight)
        texture(texture, centerUv, width, width)
      else
        texture(texture, cornerUv, width, width)

      if (straight) {

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (axis) {
          EnumFacing.Axis.X -> transform = "°Z090"
          EnumFacing.Axis.Z -> transform = "°X090"
        }

        north = tex
        south = tex
        west = tex
        east = tex
      } else {
        if (EnumFacing.DOWN !in connections) down = tex
        if (EnumFacing.UP !in connections) up = tex
        if (EnumFacing.NORTH !in connections) north = tex
        if (EnumFacing.SOUTH !in connections) south = tex
        if (EnumFacing.WEST !in connections) west = tex
        if (EnumFacing.EAST !in connections) east = tex
      }
    }

    for (c in connections) box {
      min = vec(rmin, 0.0, rmin)
      max = vec(rmax, rmin, rmax)

      cull = false

      transform = when (c) {
        EnumFacing.DOWN -> ""
        EnumFacing.UP -> "|Y"
        EnumFacing.NORTH -> "°X…90|Z"
        EnumFacing.SOUTH -> "°X…90"
        EnumFacing.WEST -> "°Z…90|X"
        EnumFacing.EAST -> "°Z…90"
      }

      val tex = when (c) {
        EnumFacing.DOWN -> texture(texture, side2Uv, width, 0.5f - radius)
        EnumFacing.UP -> texture(texture, centerUv, width, -0.5f + radius)
        EnumFacing.NORTH -> texture(texture, centerUv, width, -0.5f + radius)
        EnumFacing.SOUTH -> texture(texture, side2Uv, width, 0.5f - radius)
        EnumFacing.WEST -> texture(texture, centerUv, width, -0.5f + radius)
        EnumFacing.EAST -> texture(texture, side2Uv, width, 0.5f - radius)
      }

      north = tex
      south = tex
      west = tex
      east = tex
    }
  }

  override fun registerIcons(textureMap: TextureMap) {
    texture = textureMap.registerSprite(textureLocation)
  }
}