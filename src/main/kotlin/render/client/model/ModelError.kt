package therealfarfetchd.quacklib.render.client.model

import therealfarfetchd.quacklib.api.render.model.SimpleModel

object ModelError : SimpleModel() {

  val error = useTexture("error")

  override fun getParticleTexture(): PreparedTexture = error

  override fun ModelContext.addObjects() {
    add(Box) {
      textureAll(error)
    }
  }

}