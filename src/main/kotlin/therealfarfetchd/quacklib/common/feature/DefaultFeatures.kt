package therealfarfetchd.quacklib.common.feature

@Suppress("MemberVisibilityCanPrivate")
object DefaultFeatures {
  val VirtualAlloyFurnace = VirtualFeature("alloy furnace")
  val VirtualSilicon = VirtualFeature("silicon")
  val VirtualSiliconWafer = VirtualFeature("silicon wafer")
  val VirtualSiliconWaferRed = VirtualFeature("silicon wafer red")
  val VirtualSiliconWaferBlue = VirtualFeature("silicon wafer blue")
  val VirtualRedAlloy = VirtualFeature("red alloy ingot")
  val VirtualBlueAlloy = VirtualFeature("blue alloy ingot")
  val VirtualBrass = VirtualFeature("brass ingot")
  val VirtualNikolite = VirtualFeature("nikolite")

  val AlloyFurnace = Feature("alloy furnace") { provides(VirtualAlloyFurnace) }
  val Drawplate = Feature("drawplate")

  val ComponentItem = Feature("component item")

  val LumarWhite = ItemFeature(0)
  val LumarOrange = ItemFeature(1)
  val LumarMagenta = ItemFeature(2)
  val LumarLightBlue = ItemFeature(3)
  val LumarYellow = ItemFeature(4)
  val LumarLime = ItemFeature(5)
  val LumarPink = ItemFeature(6)
  val LumarGray = ItemFeature(7)
  val LumarSilver = ItemFeature(8)
  val LumarCyan = ItemFeature(9)
  val LumarPurple = ItemFeature(10)
  val LumarBlue = ItemFeature(11)
  val LumarBrown = ItemFeature(12)
  val LumarGreen = ItemFeature(13)
  val LumarRed = ItemFeature(14)
  val LumarBlack = ItemFeature(15)

  val Lumar = Feature("lumar") {
    depends(LumarWhite, LumarOrange, LumarMagenta, LumarLightBlue, LumarYellow, LumarLime,
      LumarPink, LumarGray, LumarSilver, LumarCyan, LumarPurple, LumarBlue, LumarBrown, LumarGreen, LumarRed, LumarBlack)
  }

  val Silicon = ItemFeature(16) { provides(VirtualSilicon) }
  val SiliconWafer = ItemFeature(17) { depends(VirtualSilicon); provides(VirtualSiliconWafer) }
  val SiliconWaferRed = ItemFeature(18) { depends(VirtualSiliconWafer); provides(VirtualSiliconWaferRed) }
  val SiliconWaferBlue = ItemFeature(19) { depends(VirtualSiliconWafer); provides(VirtualSiliconWaferBlue) }

  val RedAlloy = ItemFeature(20) { provides(VirtualRedAlloy); oreDict("ingotRedAlloy") }
  val BlueAlloy = ItemFeature(21) { provides(VirtualBlueAlloy); oreDict("ingotBlueAlloy") }
  val Brass = ItemFeature(22) { provides(VirtualBrass); oreDict("ingotBrass") }
  val Nikolite = ItemFeature(23) { provides(VirtualNikolite); oreDict("dustNikolite", "dyeBlue") }

  // NikoliteOre: oreNikolite

  val CopperWire = ItemFeature(24) { depends(Drawplate) }
  val IronWire = ItemFeature(25) { depends(Drawplate) }
  val CopperCoil = ItemFeature(26) { depends(CopperWire) }
  val Motor = ItemFeature(27) { depends(CopperCoil, VirtualBlueAlloy) }

  val TeckleCompat = Feature("teckle compat") {
    provides(VirtualAlloyFurnace, VirtualSilicon, VirtualSiliconWafer, VirtualSiliconWaferRed, VirtualSiliconWaferBlue,
      VirtualRedAlloy, VirtualBlueAlloy, VirtualBrass, VirtualNikolite)
    manualOnly = true
  }

  val MultipartCompat = Feature("mcmultipart compat") {
    manualOnly = true
  }
}