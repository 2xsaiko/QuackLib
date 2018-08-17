package therealfarfetchd.quacklib.testmod

import therealfarfetchd.quacklib.api.render.model.SimpleModel

class TestModel : SimpleModel(useDynamic = true, useGL = true) {
  private val base = useTexture("minecraft:blocks/piston_bottom")
  private val cube = useTexture("minecraft:blocks/red_sand")

  val rotFloating = useRenderParam<Float>()
  val scaleFloating = useRenderParam<Float>()

  override fun getParticleTexture(): PreparedTexture = base

  override fun ModelContext.addObjects() {
    coordsScale(16)

    //    dynamic {
    //      val rot = when (
    //        val d = data) {
    //        is DataSource.Block -> d.state[rotFloating]
    //        is DataSource.Item -> d.state[rotFloating]
    //        else -> 0f
    //      }
    //
    //      val scale = when (
    //        val d = data) {
    //        is DataSource.Block -> d.state[scaleFloating]
    //        is DataSource.Item -> d.state[scaleFloating]
    //        else -> 0.5f
    //      }
    //
    //      translate(8f, 8f, 8f)
    //      rotate(rot, 0f, 1f, 0f)
    //      scale(scale)
    //      translate(-8f, -8f, -8f)
    //
    //      add(Box) {
    //        from(4f, 4f, 4f)
    //        to(12f, 12f, 12f)
    //
    //        textureAll(cube) {
    //          uv(0f, 0f, 1f, 1f)
    //        }
    //      }
    //    }
    //
    //    add(Box) {
    //      to(16f, 2f, 16f)
    //
    //      textureAll(base)
    //    }

    translate(0f, 0f, 16f)
    add(OBJ) {
      source("qltestmod:block/teapot")
    }

    //    gl {
    //      GlStateManager.disableDepth()
    //      GL11.glBegin(GL11.GL_LINES)
    //      GL11.glVertex3f(0f, 0f, 0f)
    //      GL11.glVertex3f(1f, 1f, 1f)
    //      GL11.glVertex3f(1f, 0f, 0f)
    //      GL11.glVertex3f(0f, 1f, 1f)
    //      GL11.glVertex3f(0f, 0f, 1f)
    //      GL11.glVertex3f(1f, 1f, 0f)
    //      GL11.glVertex3f(0f, 1f, 0f)
    //      GL11.glVertex3f(1f, 0f, 1f)
    //      GL11.glEnd()
    //      GlStateManager.enableDepth()
    //    }

    // add(Box) {
    //   texture(cube, EnumFacing.NORTH, EnumFacing.DOWN)
    // }
    //
    // val texture = texture(cube) as AtlasTexture
    // addQuad(mkQuad(texture, EnumFacing.UP, Vec3(0, 1, 0), Vec3(1, 0, 1)).copy(vert2 = Vec3(0, 0, 1), vert4 = Vec3(1, 1, 0)))
    // val slope = Quad(texture, Vec3(0,0,0), Vec3(0,0,1), Vec3(0, 1, 0), Vec3(0, 1, 0), Vec2(0, 0), Vec2(1, 0), Vec2(0, 1), Vec2(0, 1), Vec2.Origin, Color.WHITE)
    // addQuad(slope)
    // addQuad(slope.mirror(EnumFacing.Axis.X))

  }
}