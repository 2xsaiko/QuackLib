package therealfarfetchd.quacklib.testmod

import net.minecraft.block.material.Material
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.block.component.fix
import therealfarfetchd.quacklib.api.block.component.renderProperty
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.tools.isDebugMode

@Mod(modid = "qltestmod", version = "1.0.0", name = "QuackLib Test Mod", dependencies = "required-after:quacklib", modLanguageAdapter = KotlinAdapter)
object QLTestMod : BaseMod() {

  override fun initContent(ctx: InitializationContext) = ctx {
    if (!isDebugMode) return@ctx

    val testBlock = addBlock("test_block") {
      material = Material.IRON
      hardness = 0.5f
      validTools = setOf(Tool("pickaxe", 2))

      class DummyComp : BlockComponentRenderProperties {

        override val rl: ResourceLocation = ResourceLocation("qltestmod:dummy")

        val dummyExport = renderProperty<Float>("dummy") { output { 45f } } fix this

      }

      class TestModel : SimpleModel() {
        val walls = useTexture("quacklib:error")
        val vertical = useTexture("quacklib:pablo")

        val rot = useRenderParam<Float>()

        override fun ModelContext.addObjects() {
          coordsScale(16)

          val rotation = when (
            val d = data) {
            is DataSource.Block -> d.state[rot]
            is DataSource.Item -> d.state[rot]
            is DataSource.Unknown -> 0f
          }

          translate(8f, 8f, 8f)
          rotate(rotation, 0f, 1f, 0f)
          translate(-8f, -8f, -8f)

          add(Box) {
            from(2f, 2f, 2f)
            to(14f, 14f, 14f)

            textureAll(walls)
            texture(vertical, EnumFacing.UP, EnumFacing.DOWN)
          }
        }
      }

      val comp = apply(DummyComp())

      val model = apply(TestModel())

      link {
        comp.dummyExport provides model.rot
      }
    }

    val testBlockItem = addPlacementItem(testBlock)

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
