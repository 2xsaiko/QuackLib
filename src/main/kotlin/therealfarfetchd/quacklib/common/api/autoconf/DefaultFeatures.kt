package therealfarfetchd.quacklib.common.api.autoconf

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.client.gui.GuiAlloyFurnace
import therealfarfetchd.quacklib.common.QGuiHandler
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.api.util.AutoLoad
import therealfarfetchd.quacklib.common.api.util.shutupForge
import therealfarfetchd.quacklib.common.api.world.QWorldGenerator
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.block.ContainerAlloyFurnace

@Suppress("MemberVisibilityCanPrivate")
@AutoLoad
object DefaultFeatures {
  val VirtualPower = VirtualFeature("power")
  val VirtualItemTubes = VirtualFeature("itemtubes")
  val VirtualBundledCable = VirtualFeature("bundled cable")
  val MCMultipartCompat = VirtualFeature("mcmultipart compat")

  val OreGeneration = Feature("ore generation") {
    action {
      GameRegistry.registerWorldGenerator(QWorldGenerator, 2)
    }
  }

  val NikoliteOre: Feature = Feature("nikolite ore") {
    depends(Nikolite, OreGeneration);

    action {
      shutupForge {
        QWorldGenerator.registerOreGenerator(BlockNikoliteOre.defaultState, 0..16, 8, 8)
      }
    }
  }

  val AlloyFurnace = Feature("alloy furnace") {
    action {
      QGuiHandler.registerClientGui(ResourceLocation(ModID, "alloy_furnace")) { _, qb, player -> GuiAlloyFurnace(player.inventory, qb as IQBlockInventory) }
      QGuiHandler.registerServerGui(ResourceLocation(ModID, "alloy_furnace")) { _, qb, player -> ContainerAlloyFurnace(player.inventory, qb as IQBlockInventory) }
    }
  }

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

  val Silicon = ItemFeature(16) { depends(AlloyFurnace) }
  val SiliconWafer = ItemFeature(17) { depends(Silicon) }
  val SiliconWaferRed = ItemFeature(18) { depends(AlloyFurnace, SiliconWafer) }
  val SiliconWaferBlue = ItemFeature(19) { depends(AlloyFurnace, SiliconWafer, Nikolite) }

  val RedAlloy = ItemFeature(20) { depends(AlloyFurnace); oreDict("ingotRedAlloy") }
  val BlueAlloy = ItemFeature(21) { depends(AlloyFurnace); depends(Nikolite); oreDict("ingotBlueAlloy") }
  val Brass = ItemFeature(22) { depends(AlloyFurnace); oreDict("ingotBrass") }
  val Nikolite = ItemFeature(23) { depends(NikoliteOre); oreDict("dustNikolite", "dyeCyan") }

  val CopperWire = ItemFeature(24) { depends(Drawplate) }
  val IronWire = ItemFeature(25) { depends(Drawplate) }
  val CopperCoil = ItemFeature(26) { depends(CopperWire) }
  val Motor = ItemFeature(27) { depends(CopperCoil, BlueAlloy) }

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