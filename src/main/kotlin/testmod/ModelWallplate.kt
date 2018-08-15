package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.api.core.extensions.AxisAlignedBB
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.SimpleModel

class ModelWallplate : SimpleModel() {

  val stone = useTexture("minecraft:blocks/stone")

  val facing = useRenderParam<EnumFacing>()

  override fun getParticleTexture(): PreparedTexture = stone

  override fun ModelContext.addObjects() {
    val f = when (
      val d = data) {
      is DataSource.Block -> d.state[facing]
      else -> EnumFacing.EAST
    }

    add(Box) {
      val fv = -f.directionVec.toVec3() * 14 / 16f
      val box = AxisAlignedBB(0f, 0f, 0f, 1f, 1f, 1f)
        .contract(fv.x.toDouble(), fv.y.toDouble(), fv.z.toDouble())

      from(box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat())
      to(box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat())

      textureAll(stone)
    }
  }
}