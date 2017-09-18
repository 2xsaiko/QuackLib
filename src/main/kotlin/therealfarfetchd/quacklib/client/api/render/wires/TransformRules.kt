package therealfarfetchd.quacklib.client.api.render.wires

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.common.api.extensions.compose
import therealfarfetchd.quacklib.common.api.util.EnumEdge
import therealfarfetchd.quacklib.common.api.util.StringPackedProps

enum class TransformRules(rules: String = "", val useAlt: Boolean = false, val useAltForBase: Boolean = false, val mirrorBase: Boolean = false) {

  // edge-specific rules
  DownNorth,
  DownSouth(rules = "|Z", useAlt = true, useAltForBase = true),
  DownWest(rules = "°Y…90|X", useAlt = true, mirrorBase = true),
  DownEast(rules = "°Y…90", useAltForBase = true, mirrorBase = true),
  UpNorth(rules = "|Y", useAlt = true, mirrorBase = true),
  UpSouth(rules = "|Y|Z", useAltForBase = true, mirrorBase = true),
  UpWest(rules = "|Y°Y…90|X"),
  UpEast(rules = "|Y°Y…90", useAlt = true, useAltForBase = true),
  NorthDown(rules = "°X…90|Z", useAlt = true, mirrorBase = true),
  NorthUp(rules = "°X…90|Y|Z", useAltForBase = true, mirrorBase = true),
  NorthWest(rules = "°Z…90|Z°Y…90|Z"),
  NorthEast(rules = "°Z…90°Y…90|Z", useAlt = true, useAltForBase = true),
  SouthDown(rules = "°X…90"),
  SouthUp(rules = "°X…90|Y", useAlt = true, useAltForBase = true),
  SouthWest(rules = "°Z…90|Z°Y…90", useAlt = true, mirrorBase = true),
  SouthEast(rules = "°Z…90°Y…90", useAltForBase = true, mirrorBase = true),
  WestUp(rules = "°Y…90°Z…90|X", useAlt = true, useAltForBase = true),
  WestDown(rules = "°Y…90°Z…90|Y|X"),
  WestNorth(rules = "°Z…90|X", useAlt = true, mirrorBase = true),
  WestSouth(rules = "°Z…90|X|Z", useAltForBase = true, mirrorBase = true),
  EastUp(rules = "°Y…90°Z…90", useAltForBase = true, mirrorBase = true),
  EastDown(rules = "°Y…90°Z…90|Y", useAlt = true, mirrorBase = true),
  EastNorth(rules = "°Z…90"),
  EastSouth(rules = "°Z…90|Z", useAlt = true, useAltForBase = true),

  // face-specific rules
  Down(rules = "+DownSouth"),
  Up(rules = "+……UpSouth"),
  North(rules = "+……NorthUp"),
  South(rules = "+……SouthUp"),
  West(rules = "+………WestUp"),
  East(rules = "+………EastUp"),
  ;

  /*
   * Transform commands:
   * | - vertex mirror. Arguments: axis: [XYZ]
   * ° - Rotation. Arguments: axis: [XYZ], angle: str(3) ->Float
   * + - Execute another rule. Arguments: rule: str(9) ->TransformRules
   */

  val op: (Quad) -> Quad by lazy { parseCmd(StringPackedProps(rules)) }

  companion object {
    fun getRule(edge: EnumEdge): TransformRules = valueOf(edge.name)
    fun getRule(facing: EnumFacing): TransformRules = valueOf(facing.name2.capitalize())

    fun parseCmd(spp: StringPackedProps): (Quad)->Quad {
      var op: (Quad) -> Quad = { it }
      while (spp.hasNext) {
        op = op compose parseCmd0(spp)
      }
      return op
    }

    private fun parseCmd0(spp: StringPackedProps): (Quad) -> Quad {
      if (!spp.hasNext) return { it }
      try {
        val command = spp.getChar()
        when (command) {
          '|' -> {
            val axis = EnumFacing.Axis.valueOf(spp.getString(1))
            return { it.mirror(axis) }
          }
          '°' -> {
            val axis = EnumFacing.Axis.valueOf(spp.getString(1))
            val ang = spp.getString(3)
            return { it.rotate(axis, ang.toFloat()) }
          }
          '+' -> return valueOf(spp.getString(9)).op
          else -> error("Invalid command: $command")
        }
      } catch (e: Exception) {
        // print something like:
        // |X|Y°Xayy
        //       ~~^
        // to show where the exception is
        println(spp.string)
        println("^".padStart(spp.position - spp.lastReadStart + 1, '~').padStart(spp.position, ' '))
        throw e
      }
    }
  }

}