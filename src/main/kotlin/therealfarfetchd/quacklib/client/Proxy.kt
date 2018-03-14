package therealfarfetchd.quacklib.client

import mcmultipart.api.event.DrawMultipartHighlightEvent
import net.minecraft.block.Block
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
import net.minecraft.client.renderer.GlStateManager.DestFactor.ZERO
import net.minecraft.client.renderer.GlStateManager.SourceFactor.ONE
import net.minecraft.client.renderer.GlStateManager.SourceFactor.SRC_ALPHA
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Level
import org.lwjgl.opengl.GL11.GL_LINES
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.client.api.gui.GuiElementRegistry
import therealfarfetchd.quacklib.client.api.gui.GuiLogicRegistry
import therealfarfetchd.quacklib.client.api.gui.NullGuiLogic
import therealfarfetchd.quacklib.client.api.gui.elements.Button
import therealfarfetchd.quacklib.client.api.gui.elements.Dummy
import therealfarfetchd.quacklib.client.api.gui.elements.Frame
import therealfarfetchd.quacklib.client.api.gui.elements.Label
import therealfarfetchd.quacklib.client.api.model.*
import therealfarfetchd.quacklib.client.api.qbr.DynamicModelRenderer
import therealfarfetchd.quacklib.client.api.qbr.QBContainerTileRenderer
import therealfarfetchd.quacklib.client.api.qbr.bindSpecialRenderer
import therealfarfetchd.quacklib.common.Proxy
import therealfarfetchd.quacklib.common.api.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.api.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.api.block.IBlockAdvancedOutline
import therealfarfetchd.quacklib.common.api.extensions.RGBA
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTileInventory
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.IBlockDefinition
import therealfarfetchd.quacklib.common.api.util.IItemDefinition
import therealfarfetchd.quacklib.common.api.util.math.Vec3
import therealfarfetchd.quacklib.common.block.BlockAlloyFurnace
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.item.ItemComponent
import kotlin.reflect.KClass

/**
 * Created by marco on 16.07.17.
 */

class Proxy : Proxy() {
  private var outlineCache: Map<Set<AxisAlignedBB>, Set<Pair<Vec3, Vec3>>> = emptyMap()

  override fun preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)

    ClientRegistry.bindTileEntitySpecialRenderer(QBContainerTile::class.java, QBContainerTileRenderer)
    ClientRegistry.bindTileEntitySpecialRenderer(QBContainerTileInventory::class.java, QBContainerTileRenderer)
    if (FeatureManager.isRequired(DefaultFeatures.MCMultipartCompat))
      ClientRegistry.bindTileEntitySpecialRenderer(QBContainerTileMultipart::class.java, QBContainerTileRenderer)
  }

  override fun init(e: FMLInitializationEvent) {
    super.init(e)
    with(GuiElementRegistry) {
      register("quacklib:dummy", Dummy::class)
      register("minecraft:frame", Frame::class)
      register("minecraft:label", Label::class)
      register("minecraft:button", Button::class)
    }

    GuiLogicRegistry.register(GuiLogicRegistry.fallback, NullGuiLogic::class)
  }

  @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  @SubscribeEvent
  fun registerModels(e: ModelRegistryEvent) {
    (IBlockDefinition.definitions + IItemDefinition.definitions)
      .filter { it.item != null && it.registerModels }
      .forEach { def ->
        val mrl = ModelResourceLocation(def.item!!.registryName, "inventory")
        QuackLib.Logger.log(Level.INFO, "Registered model resource location for item ${def.item!!.registryName} to $mrl")
        def.metaModels.forEach { meta -> ModelLoader.setCustomModelResourceLocation(def.item, meta, mrl) }
      }

    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      ModelLoader.setCustomModelResourceLocation(BlockNikoliteOre.Item, 0, ModelResourceLocation(BlockNikoliteOre.Item.registryName, "inventory"))
    }

    if (FeatureManager.isRequired(DefaultFeatures.ComponentItem)) {
      for (i in ItemComponent.getValidMetadata()) {
        ModelLoader.setCustomModelResourceLocation(ItemComponent, i, ModelResourceLocation("${ItemComponent.registryName}/$i", "inventory"))
      }
    }

    if (FeatureManager.isRequired(DefaultFeatures.AlloyFurnace)) {
      ModelLoader.setCustomModelResourceLocation(BlockAlloyFurnace.Item, 0, ModelResourceLocation(BlockAlloyFurnace.Item.registryName, "inventory"))
    }

//    registerModelBakery(MultiblockExtension.Block, null, InvisibleModel)
  }

  @SubscribeEvent
  fun drawBlockOutline(e: DrawBlockHighlightEvent) {
    val world = when {
      e::class.qualifiedName == "mcmultipart.api.event.DrawMultipartHighlightEvent" -> (e as DrawMultipartHighlightEvent).partInfo.partWorld
      else                                                                          -> e.player.world
    }
    val pos = e.target.blockPos ?: return
    val state = world.getBlockState(pos)
    val block = state.block
    if (block is IBlockAdvancedOutline) {
      val outlineBoxes = block.getOutlineBoxes(world, pos, state)
      val lines = outlineCache[outlineBoxes] ?: run {
        val bb = outlineBoxes.map { it.grow(0.002) }
        val lines = bb
          .flatMap { it.getLines().map { it1 -> it to it1 } }
          .asSequence()
          .filter { (k, v) -> bb.filterNot { it == k }.none { v.first.toVec3d() in it.grow(0.002) && v.second.toVec3d() in it.grow(0.002) } }
          .map { it.second }
          .toSet()

        outlineCache += outlineBoxes to lines
        lines
      }

      drawLines(e.player, e.partialTicks, RGBA(0f, 0f, 0f, 0.4f), lines, pos)
      e.isCanceled = true
    }
  }

  private fun AxisAlignedBB.getLines() = setOf(
    // bottom
    Vec3(minX, minY, minZ) to Vec3(minX, minY, maxZ),
    Vec3(maxX, minY, minZ) to Vec3(maxX, minY, maxZ),
    Vec3(minX, minY, minZ) to Vec3(maxX, minY, minZ),
    Vec3(minX, minY, maxZ) to Vec3(maxX, minY, maxZ),
    // top
    Vec3(minX, maxY, minZ) to Vec3(minX, maxY, maxZ),
    Vec3(maxX, maxY, minZ) to Vec3(maxX, maxY, maxZ),
    Vec3(minX, maxY, minZ) to Vec3(maxX, maxY, minZ),
    Vec3(minX, maxY, maxZ) to Vec3(maxX, maxY, maxZ),
    // sides
    Vec3(minX, minY, minZ) to Vec3(minX, maxY, minZ),
    Vec3(minX, minY, maxZ) to Vec3(minX, maxY, maxZ),
    Vec3(maxX, minY, maxZ) to Vec3(maxX, maxY, maxZ),
    Vec3(maxX, minY, minZ) to Vec3(maxX, maxY, minZ)
  )

  private fun drawLines(player: Entity, partialTicks: Float, color: RGBA, lines: Collection<Pair<Vec3, Vec3>>, pos: BlockPos) {
    enableBlend()
    tryBlendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO)
    glLineWidth(2.0f)
    disableTexture2D()
    depthMask(false)
    color(color.first, color.second, color.third, color.fourth)

    val offset = -Vec3(
      player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks,
      player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks,
      player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks
    )

    pushMatrix()
    translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

    val tessellator = Tessellator.getInstance()
    val bufferbuilder = tessellator.buffer
    bufferbuilder.begin(GL_LINES, POSITION)
    for ((min, max) in lines.map { it.first + offset to it.second + offset }) {
      bufferbuilder.pos(min.x, min.y, min.z).endVertex()
      bufferbuilder.pos(max.x, max.y, max.z).endVertex()
    }
    tessellator.draw()

    popMatrix()

    depthMask(true)
    enableTexture2D()
    disableBlend()
  }

  @SubscribeEvent
  fun bakeModels(e: ModelBakeEvent) {
    CachedBakedModel.clearCache()
    for ((model, mrl) in BakedModelRegistry.models) {
      e.modelRegistry.putObject(mrl, model)
    }
  }

  @SubscribeEvent
  fun textureLoad(e: TextureStitchEvent.Pre) {
    val map = e.map
    if (map.basePath == "textures") {
      for (reg in IIconRegister.iconRegisters) {
        reg.registerIcons(map)
      }
    }
  }
}


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SideOnly(Side.CLIENT)
fun <T : QBlock, B> registerModelBakery(qb: KClass<T>, block: Block, item: Item?, bakery: B) where B : IModel, B : IDynamicModel<T> {
  registerModelBakery(block, item, bakery)
  qb.bindSpecialRenderer(DynamicModelRenderer(bakery))
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SideOnly(Side.CLIENT)
fun registerModelBakery(block: Block, item: Item?, bakery: IModel) {
  val b = StateMap.Builder()
  b.ignore(*block.defaultState.propertyKeys.toTypedArray())
  val map = b.build()
  val rl = ModelResourceLocation(block.registryName, "normal")

  CachedBakedModel(bakery).registerBakedModel(rl)
  ModelLoader.setCustomStateMapper(block, map)

  if (item != null) {
    (IBlockDefinition.definitions + IItemDefinition.definitions)
      .filter { it.item == item && !it.registerModels }
      .flatMap { it.metaModels }
      .forEach { ModelLoader.setCustomModelResourceLocation(item, it, rl) }
  }

  if (bakery is IIconRegister) bakery.registerIconRegister()
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SideOnly(Side.CLIENT)
fun registerModelBakery(item: Item, bakery: IModel) {
  val rl = ModelResourceLocation(item.registryName, "normal")
  CachedBakedModel(bakery).registerBakedModel(rl)
  ModelLoader.setCustomModelResourceLocation(item, 0, rl)
  if (bakery is IIconRegister) bakery.registerIconRegister()
}