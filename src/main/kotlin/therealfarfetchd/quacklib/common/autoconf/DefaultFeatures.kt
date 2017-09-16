package therealfarfetchd.quacklib.common.autoconf

import net.minecraftforge.fml.common.registry.GameRegistry
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.util.AutoLoad
import therealfarfetchd.quacklib.common.util.shutupForge
import therealfarfetchd.quacklib.common.world.QWorldGenerator

@Suppress("MemberVisibilityCanPrivate")
@AutoLoad
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
  val VirtualNikoliteOre = VirtualFeature("nikolite ore")
  val VirtualBlutricity = VirtualFeature("blutricity")
  val TeckleCompat = VirtualFeature("teckle compat")
  val MCMultipartCompat = VirtualFeature("mcmultipart compat")

  val OreGeneration = Feature("ore generation") {
    action {
      GameRegistry.registerWorldGenerator(QWorldGenerator, 2)
    }
  }

  val NikoliteOre: Feature = Feature("nikolite ore") {
    depends(Nikolite, OreGeneration); provides(VirtualNikoliteOre)

    action {
      shutupForge {
        QWorldGenerator.registerOreGenerator(BlockNikoliteOre.defaultState, 0..16, 8, 8)
      }
    }
  }

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
  val SiliconWaferBlue = ItemFeature(19) { depends(VirtualSiliconWafer); depends(VirtualNikolite); provides(VirtualSiliconWaferBlue) }

  val RedAlloy = ItemFeature(20) { provides(VirtualRedAlloy); oreDict("ingotRedAlloy") }
  val BlueAlloy = ItemFeature(21) { provides(VirtualBlueAlloy); depends(VirtualNikolite); oreDict("ingotBlueAlloy") }
  val Brass = ItemFeature(22) { provides(VirtualBrass); oreDict("ingotBrass") }
  val Nikolite = ItemFeature(23) { depends(NikoliteOre); provides(VirtualNikolite); oreDict("dustNikolite", "dyeBlue") }

  val CopperWire = ItemFeature(24) { depends(Drawplate) }
  val IronWire = ItemFeature(25) { depends(Drawplate) }
  val CopperCoil = ItemFeature(26) { depends(CopperWire) }
  val Motor = ItemFeature(27) { depends(CopperCoil, VirtualBlueAlloy) }

  val TeckleMod = Feature("teckle mod") {
    //    provides(VirtualAlloyFurnace, VirtualSilicon, VirtualSiliconWafer, VirtualSiliconWaferRed, VirtualSiliconWaferBlue,
    //      VirtualRedAlloy, VirtualBlueAlloy, VirtualBrass, VirtualNikolite, VirtualNikoliteOre)
    provides(TeckleCompat)
    priority = 1000
    manualReg = true
  }

  val MultipartMod = Feature("mcmultipart mod") {
    provides(MCMultipartCompat)
    priority = 1000
    manualReg = true

    action {
      GameRegistry.registerTileEntity(QBContainerTileMultipart::class.java, "$ModID:qblock_container_mp")
      GameRegistry.registerTileEntity(QBContainerTileMultipart.Ticking::class.java, "$ModID:qblock_container_mp_t")
    }
  }
}