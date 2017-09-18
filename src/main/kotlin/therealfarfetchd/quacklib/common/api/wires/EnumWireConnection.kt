package therealfarfetchd.quacklib.common.api.wires

import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender

/**
 * Created by marco on 27.05.17.
 */
enum class EnumWireConnection(val identifierId: Int, val renderType: EnumWireRender) {
  None(0, EnumWireRender.Invisible),
  External(1, EnumWireRender.Normal),
  Internal(2, EnumWireRender.InnerCorner),
  Corner(3, EnumWireRender.Corner);

  companion object {
    fun byIdentifier(identifierId: Int): EnumWireConnection = values().find { it.identifierId == identifierId } ?: None
  }
}