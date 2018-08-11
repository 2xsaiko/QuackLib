package therealfarfetchd.quacklib.testmod

import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.SimpleModel

class TestModel : SimpleModel(useDynamic = true) {
  private val base = useTexture("minecraft:blocks/piston_bottom")
  private val cube = useTexture("minecraft:blocks/red_sand")

  val rotFloating = useRenderParam<Float>()
  val scaleFloating = useRenderParam<Float>()

  override fun ModelContext.addObjects() {
    coordsScale(16)

    dynamic {
      val rot = when (
        val d = data) {
        is DataSource.Block -> d.state[rotFloating]
        is DataSource.Item -> d.state[rotFloating]
        else -> 0f
      }

      val scale = when (
        val d = data) {
        is DataSource.Block -> d.state[scaleFloating]
        is DataSource.Item -> d.state[scaleFloating]
        else -> 0.5f
      }

      translate(8f, 8f, 8f)
      rotate(rot, 0f, 1f, 0f)
      scale(scale)
      translate(-8f, -8f, -8f)

      add(Box) {
        from(4f, 4f, 4f)
        to(12f, 12f, 12f)

        textureAll(cube) {
          uv(0f, 0f, 1f, 1f)
        }
      }
    }

    add(Box) {
      to(16f, 2f, 16f)

      textureAll(base)
    }
  }
}