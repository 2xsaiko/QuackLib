package therealfarfetchd.quacklib.testmod

import therealfarfetchd.quacklib.api.render.model.SimpleModel

class TestModel : SimpleModel() {
  private val tex = useTexture("qltestmod:blocks/mainframe")

  val rotFloating = useRenderParam<Float>()
  val scaleFloating = useRenderParam<Float>()

  override fun getParticleTexture(): PreparedTexture = tex

  override fun ModelContext.addObjects() {
    coordsScale(16)
    trNew {
      translate(0f, 16f, 0f)
      add(OBJ) {
        source("qltestmod:block/mainframe")
        texture(tex, "Material")
      }
    }

    constraints(0f, 0f, 0f, 1f, 1f, 1f)

    add(OBJ) {
      source("qltestmod:block/mainframe")
      texture(tex, "Material")
    }

  }
}