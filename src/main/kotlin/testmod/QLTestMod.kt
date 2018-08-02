package therealfarfetchd.quacklib.testmod

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.common.Mod
import therealfarfetchd.quacklib.api.block.component.BlockComponentCollision
import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.block.component.fix
import therealfarfetchd.quacklib.api.block.component.renderProperty
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.tools.isDebugMode
import kotlin.math.sin

@Mod(modid = "qltestmod", version = "1.0.0", name = "QuackLib Test Mod", dependencies = "required-after:quacklib", modLanguageAdapter = KotlinAdapter)
object QLTestMod : BaseMod() {

  override fun initContent(ctx: InitializationContext) = ctx {
    if (!isDebugMode) return@ctx

    val testBlock = addBlock("test_block") {
      material = Material.IRON
      hardness = 0.5f
      validTools = setOf(Tool("pickaxe", 2))

      class DummyComp : BlockComponentRenderProperties, BlockComponentCollision {

        override val rl: ResourceLocation = ResourceLocation("qltestmod:dummy")

        val rot = renderProperty<Float>("rotation") {
          output {
            ((it.worldMutable?.totalTime ?: 0L) * 0.5f % 360)
          }
        } fix this

        val scale = renderProperty<Float>("scale") {
          output { (sin((it.worldMutable?.totalTime ?: 0L) * 0.1f) + 1.75f) / 2.75f }
        } fix this

        override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> {
          return listOf(MCBlockType.FULL_BLOCK_AABB)
        }
      }

      class TestModel : SimpleModel() {
        val base = useTexture("minecraft:blocks/piston_bottom")
        val cube = useTexture("minecraft:blocks/red_sand")

        val rotFloating = useRenderParam<Float>()
        val scaleFloating = useRenderParam<Float>()

        override fun ModelContext.addObjects() {
          coordsScale(16)

          dynamic {
            val rot = when (
              val d = data) {
              is DataSource.Block -> d.state[rotFloating]
              else -> 0f
            }

            val scale = when (
              val d = data) {
              is DataSource.Block -> d.state[scaleFloating]
              else -> 0f
            }

            translate(8f, 8f, 8f)
            rotate(rot, 0f, 1f, 0f)
            scale(scale)
            translate(-8f, -8f, -8f)

            add(Box) {
              from(4f, 4f, 4f)
              to(12f, 12f, 12f)

              textureAll(cube)
            }
          }

          add(Box) {
            to(16f, 2f, 16f)

            textureAll(base)
          }
        }
      }

      val comp = apply(DummyComp())

      val model = apply(TestModel())

      link {
        comp.rot provides model.rotFloating
        comp.scale provides model.scaleFloating
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
