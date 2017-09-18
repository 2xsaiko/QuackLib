package therealfarfetchd.quacklib.client.api.render.wires

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.client.api.model.AbstractModelBakery
import therealfarfetchd.quacklib.client.api.model.IIconRegister
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.client.api.render.QuadFactory
import therealfarfetchd.quacklib.common.api.extensions.get
import therealfarfetchd.quacklib.common.api.extensions.mapIf
import therealfarfetchd.quacklib.common.api.extensions.packInt
import therealfarfetchd.quacklib.common.api.util.EnumEdge
import therealfarfetchd.quacklib.common.api.util.Vec2
import therealfarfetchd.quacklib.common.api.util.Vec3
import therealfarfetchd.quacklib.common.api.wires.BlockWire
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.External
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.None

open class WireBakery(
  private val cableWidth: Float,
  private val cableHeight: Float,
  textureSize: Float,
  private val texLocations: List<ResourceLocation>,
  private val texLocationRetriever: (IExtendedBlockState) -> ResourceLocation,
  private val itemTexLocationRetriever: (ItemStack) -> ResourceLocation
) : AbstractModelBakery(), IIconRegister {

  private val armLength: Float = (1 - cableWidth) / 2
  private val armInnerLength: Float = armLength - cableHeight
  private val armInnerSp: Float = armLength - armInnerLength
  private val scaleFactor: Float = textureSize / 16F

  private var textures: Map<ResourceLocation, TextureAtlasSprite> = emptyMap()
  private lateinit var texture: TextureAtlasSprite

  override val particleTexture: TextureAtlasSprite
    get() = texture

  // texture positions
  private val arm1TopUv = Vec2(0.0F, 0.0F) / scaleFactor
  private val arm2TopUv = Vec2(0.0F, armLength + cableWidth) / scaleFactor
  private val centerTopUv = Vec2(0.0F, armLength) / scaleFactor
  private val centerTopCUv = Vec2(0.0F, 1.0F) / scaleFactor
  private val arm1Side1Uv = Vec2(cableWidth, 0.0F) / scaleFactor
  private val arm2Side1Uv = Vec2(cableWidth, armLength + cableWidth) / scaleFactor
  private val centerSide1Uv = Vec2(cableWidth, armLength) / scaleFactor
  private val arm1Side2Uv = Vec2(cableWidth + cableHeight, 0.0F) / scaleFactor
  private val arm2Side2Uv = Vec2(cableWidth + cableHeight, armLength + cableWidth) / scaleFactor
  private val centerSide2Uv = Vec2(cableWidth + cableHeight, armLength) / scaleFactor
  private val arm1BottomUv = Vec2(cableWidth + 2 * cableHeight, 0.0F) / scaleFactor
  private val arm2BottomUv = Vec2(cableWidth + 2 * cableHeight, armLength + cableWidth) / scaleFactor
  private val centerBottomUv = Vec2(cableWidth + 2 * cableHeight, armLength) / scaleFactor
  private val cableFrontUv = Vec2(cableWidth, 1.0F) / scaleFactor
  private val cableBackUv = Vec2(cableWidth + cableHeight, 1.0F) / scaleFactor
  private val cornerTop1Uv = Vec2(0.0F, 1.0F + cableWidth) / scaleFactor
  private val cornerTop2Uv = Vec2(cableWidth + 2 * cableHeight, 1.0F + cableWidth) / scaleFactor
  private val cornerSide1Uv = Vec2(cableWidth, 1.0F + cableWidth) / scaleFactor
  private val cornerSide2Uv = Vec2(cableWidth + cableHeight, 1.0F + cableWidth) / scaleFactor
  private val icornerSide1Uv = Vec2(2 * cableWidth + 2 * cableHeight, 0.0F) / scaleFactor
  private val icornerSide2Uv = Vec2(2 * cableWidth + 2 * cableHeight, cableHeight) / scaleFactor
  private val center8TopUv = Vec2(0.0F, 0.25F) / scaleFactor
  private val center8Top2Uv = arm2TopUv
  private val center8Arm1Side1Uv = Vec2(cableWidth, 0.25F) / scaleFactor
  private val center8Arm1Side2Uv = Vec2(cableWidth + cableHeight, 0.25F) / scaleFactor
  private val center8Arm2Side1Uv = arm2Side1Uv
  private val center8Arm2Side2Uv = arm2Side2Uv
  private val innerTop1Uv = Vec2(0.0F, armInnerSp) / scaleFactor
  private val innerTop2Uv = arm2TopUv
  private val innerArm1Side1Uv = Vec2(cableWidth, armInnerSp) / scaleFactor
  private val innerArm1Side2Uv = Vec2(cableWidth + cableHeight, armInnerSp) / scaleFactor
  private val innerArm2Side1Uv = arm2Side1Uv
  private val innerArm2Side2Uv = arm2Side2Uv

  constructor(cableWidth: Float, cableHeight: Float, textureSize: Float, texLocation: ResourceLocation) : this(cableWidth, cableHeight, textureSize, listOf(texLocation), { texLocation }, { texLocation })

  override fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad> {
    texture = textures[texLocationRetriever(state)]!!

    val c = state.getValue(BlockWire.PropConnections).toTypedArray()
    val side = state.getValue(BlockWire.PropFacing)

    if (face !in listOf(null, side)) return emptyList()

    return if (face == null) mkQuads(side, *c).map(Quad::bake)
    else emptyList() // TODO render bottom of wire
  }

  override fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad> {
    texture = textures[itemTexLocationRetriever(stack)]!!

    if (face != null) return emptyList()

    return mkQuads(DOWN, External, External, External, External).map { it.translate(Vec3(0F, 0.275F, 0F)) }.map(Quad::bake)
  }

  fun mkQuads(side: EnumFacing, vararg c: EnumWireConnection): List<Quad> {
    var quads: List<Quad> = emptyList()
    val crossing = c.withIndex()
      .map { it.value to c[(it.index + 1) % c.size] }
      .any { it.first.renderType != EnumWireRender.Invisible && it.second.renderType != EnumWireRender.Invisible }
    if (c.all { it == EnumWireConnection.None }) {
      quads += mkCenter(side, None, External, None, External)
      var b = false
      for (facing in BlockWire.lookupMap[side]!!) {
        quads += mkUnconnectedExt(EnumEdge.fromFaces(side, facing), b)
        b = !b
      }
    } else for ((i, c1) in c.withIndex()) {
      val edge = EnumEdge.fromFaces(side, BlockWire.lookupMap[side]!![i])
      quads += mkCenter(side, *c)
      quads += when (c1) {
        EnumWireConnection.None -> mkUnconnectedExt(edge, !crossing && c[(i + 2) % 4] != EnumWireConnection.None)
        EnumWireConnection.External -> mkNormalExt(edge)
        EnumWireConnection.Internal -> mkICornerExt(edge)
        EnumWireConnection.Corner -> mkCornerExt(edge)
      }
    }
    return quads
  }

  fun mkCenter(side: EnumFacing, vararg c: EnumWireConnection): List<Quad> {
    val crossing = c.withIndex()
      .map { it.value to c[(it.index + 1) % c.size] }
      .any { it.first.renderType != EnumWireRender.Invisible && it.second.renderType != EnumWireRender.Invisible }
    if (!crossing && c.any { it.renderType != EnumWireRender.Invisible }) {
      val up = c.withIndex().filter { it.value.renderType != EnumWireRender.Invisible }.map { TransformRules.getRule(EnumEdge.fromFaces(side, BlockWire.lookupMap[side]!![it.index])) }.first()
      return listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, armLength, cableWidth, 0F, cableWidth, UP,
          centerTopUv, cableWidth, cableWidth, texture, scaleFactor).mapIf(up.useAlt) { it.mirrorTextureY }
      ).map(up.op)
    } else {
      val up = TransformRules.getRule(side)
      return listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, armLength, cableWidth, 0F, cableWidth, UP,
          if (crossing) centerTopCUv else centerTopUv, cableWidth, cableWidth, texture, scaleFactor)
      ).map(up.op)
    }
  }

  fun mkUnconnectedExt(edge: EnumEdge, extendEnd: Boolean): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkUnconnectedExt(edge, rule.useAlt, rule.useAltForBase, extendEnd).map(rule.op)
  }

  fun mkUnconnectedExt(edge: EnumEdge, alt: Boolean, altBase: Boolean, extendEnd: Boolean): List<Quad> {
    if (!extendEnd) {
      val front = listOf(centerSide1Uv, centerSide2Uv)[packInt(altBase)]
      val rules = TransformRules.getRule(edge)
      return listOf(QuadFactory.makeQuadw(armLength, 0F, armLength, cableWidth, cableHeight, 0F, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90.mapIf(rules.mirrorBase) { it.mirrorTextureX })
    } else {
      val key = packInt(false, alt)
      val key2 = key shr 1
      val list = listOf(center8Arm1Side1Uv, center8Arm1Side2Uv, center8Arm2Side1Uv, center8Arm2Side2Uv)
      val side1 = list[key]
      val side2 = list[key xor 1]
      val front = listOf(cableFrontUv, cableBackUv)[key2]
      val top = listOf(center8TopUv, center8Top2Uv)[key2]

      return listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, 0.25F, cableWidth, 0F, armLength - 0.25F, UP, top, cableWidth, armLength - 0.25F, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
        QuadFactory.makeQuadw(armLength, cableHeight, 0.25F, 0F, -cableHeight, armLength - 0.25F, EAST, side1, cableHeight, armLength - 0.25F, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
        QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0.25F, 0F, -cableHeight, armLength - 0.25F, WEST, side2, cableHeight, armLength - 0.25F, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
        QuadFactory.makeQuadw(armLength, 0F, 0.25F, cableWidth, cableHeight, 0F, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
      )
    }
  }

  fun mkNormalExt(edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkNormalExt(rule.useAlt).map(rule.op)
  }

  fun mkNormalExt(alt: Boolean): List<Quad> {
    val key = packInt(false, alt)
    val key2 = key shr 1
    val list = listOf(arm1Side1Uv, arm1Side2Uv, arm2Side1Uv, arm2Side2Uv)
    val side1 = list[key]
    val side2 = list[key xor 1]
    val front = listOf(cableFrontUv, cableBackUv)[key2]
    val top = listOf(arm1TopUv, arm2TopUv)[key2]

    return listOf(
      QuadFactory.makeQuadw(armLength, cableHeight, 0F, cableWidth, 0F, armLength, UP, top, cableWidth, armLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, 0F, 0F, -cableHeight, armLength, EAST, side1, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0F, 0F, -cableHeight, armLength, WEST, side2, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, 0F, 0F, cableWidth, cableHeight, 0F, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
    )
  }

  fun mkCornerExt(edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkCornerExt(rule.useAlt).map(rule.op)
  }

  fun mkCornerExt(alt: Boolean): List<Quad> {
    val key = packInt(false, alt)
    val key2 = key shr 1
    val list = listOf(arm1Side1Uv, arm1Side2Uv, arm2Side1Uv, arm2Side2Uv)
    val list2 = listOf(cornerTop1Uv, cornerTop2Uv)
    val side1 = list[key]
    val side2 = list[key xor 1]
    val ctop1 = list2[key2]
    val ctop2 = list2[key2 xor 1]
    val top = listOf(arm1TopUv, arm2TopUv)[key2]
    val cside1 = cornerSide1Uv
    val cside2 = cornerSide2Uv
    val front = listOf(cableFrontUv, cableBackUv)[key2]

    return listOf(
      QuadFactory.makeQuadw(armLength, cableHeight, 0F, cableWidth, 0F, armLength, UP, top, cableWidth, armLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, 0F, 0F, -cableHeight, armLength, EAST, side1, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0F, 0F, -cableHeight, armLength, WEST, side2, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, cableHeight, -cableHeight, cableWidth, 0.0F, cableHeight, UP, ctop1, cableWidth, cableHeight, texture, scaleFactor),
      QuadFactory.makeQuadw(armLength, 0.0F, -cableHeight, cableWidth, cableHeight, 0.0F, SOUTH, ctop2, cableWidth, cableHeight, texture, scaleFactor).mirrorTextureY,
      QuadFactory.makeQuadw(armLength, 0.0F, 0.0F, 0.0F, cableHeight, -cableHeight, EAST, cside1, cableHeight, cableHeight, texture, scaleFactor),
      QuadFactory.makeQuadw(armLength + cableWidth, 0.0F, 0.0F, 0.0F, cableHeight, -cableHeight, WEST, cside2, cableHeight, cableHeight, texture, scaleFactor).flipTexturedSide,
      QuadFactory.makeQuadw(armLength, 0F, 0F, cableWidth, 0F, -cableHeight, DOWN, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
    )
  }

  fun mkICornerExt(edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkICornerExt(rule.useAlt).map(rule.op)
  }

  fun mkICornerExt(alt: Boolean): List<Quad> {
    val key = packInt(false, alt)
    val key2 = key shr 1
    val list = listOf(innerArm1Side1Uv, innerArm1Side2Uv, innerArm2Side1Uv, innerArm2Side2Uv)
    val side1 = list[key]
    val side2 = list[key xor 1]
    val top = listOf(innerTop1Uv, innerTop2Uv)[key2]
    val cside1 = icornerSide1Uv
    val cside2 = icornerSide2Uv
    val front = listOf(cableFrontUv, cableBackUv)[key2]

    return listOf(
      QuadFactory.makeQuadw(armLength, cableHeight, armInnerSp, cableWidth, 0F, armInnerLength, UP, top, cableWidth, armInnerLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, armInnerSp, 0F, -cableHeight, armInnerLength, EAST, side1, cableHeight, armInnerLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, armInnerSp, 0F, -cableHeight, armInnerLength, WEST, side2, cableHeight, armInnerLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, cableHeight, 0F, cableWidth, 0F, armInnerSp, UP, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90,
      QuadFactory.makeQuadw(armLength, 0F, 0F, 0F, cableHeight, armInnerSp, EAST, cside1, cableHeight, cableHeight, texture, scaleFactor).flipTexturedSide,
      QuadFactory.makeQuadw(armLength + cableWidth, 0F, 0F, 0F, cableHeight, armInnerSp, WEST, cside2, cableHeight, cableHeight, texture, scaleFactor)
    )
  }

  override fun registerIcons(textureMap: TextureMap) {
    textures = texLocations.map { it to textureMap.registerSprite(it) }.toMap()
  }

  override fun createKey(state: IExtendedBlockState, face: EnumFacing?): String = super.createKey(state, face) + state[BlockWire.PropConnections].joinToString()
}