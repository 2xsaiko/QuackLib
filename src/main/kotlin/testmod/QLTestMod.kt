package therealfarfetchd.quacklib.testmod

import net.minecraft.block.material.Material
import net.minecraftforge.fml.common.Mod
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.tools.isDebugMode

@Mod(modid = "qltestmod", version = "1.0.0", name = "QuackLib Test Mod", dependencies = "required-after:quacklib", modLanguageAdapter = KotlinAdapter)
object QLTestMod : BaseMod() {

  override fun initContent(ctx: InitializationContext) = ctx {
    if (!isDebugMode) return@ctx

    val testBlock = addBlock("test_block") {
      material = Material.IRON
      hardness = 0.5f
      validTools = setOf(Tool("pickaxe", 2))

      val comp = apply(DummyComp())

      val model = useModel(TestModel())

      link {
        model {
          comp.rot provides rotFloating
          comp.scale provides scaleFloating
        }
      }
    }

    val testBlockItem = addPlacementItem(testBlock) {
      val comp = apply(DummyCompItem())

      val model = useModel(TestModel())

      link {
        model {
          comp.rot provides rotFloating
          comp.scale provides scaleFloating
        }
      }
    }

    val testItem = addItem("test_item")

    val wallplate = addBlock("wallplate") {
      material = Material.ROCK
      hardness = 0.5f
      validTools = setOf(Tool("pickaxe", 2))

      val side = apply(ComponentSurfacePlacement())
      val box = apply(ComponentBounds(2 / 16f))
      val rs = apply(ComponentRedstone())

      val part = apply(SidedMultipart())

      link {
        side.facing provides box.facing
        side.facing provides part.facing
        side.facing provides rs.facing
      }
    }

    val wallplateItem = addPlacementItem(wallplate)

    addTab("standard", item("minecraft:stone")) {
      include(testBlockItem)
      include(wallplateItem)
      include(testItem)
      include("minecraft:diamond")
    }
  }

}
