package therealfarfetchd.quacklib.client.api.model.wire

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.client.api.model.IIconRegister
import therealfarfetchd.quacklib.client.api.model.IModel
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.client.api.render.QuadFactory
import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender
import therealfarfetchd.quacklib.client.api.render.wires.TransformRules
import therealfarfetchd.quacklib.common.api.extensions.get
import therealfarfetchd.quacklib.common.api.extensions.mapIf
import therealfarfetchd.quacklib.common.api.extensions.packInt
import therealfarfetchd.quacklib.common.api.util.EnumEdge
import therealfarfetchd.quacklib.common.api.util.math.Vec2
import therealfarfetchd.quacklib.common.api.util.math.Vec3
import therealfarfetchd.quacklib.common.api.wires.BlockWire
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.External
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.None

open class WireModel(
  private val cableWidth: Double,
  private val cableHeight: Double,
  textureSize: Double,
  private val texLocations: List<ResourceLocation>,
  private val texLocationRetriever: (IExtendedBlockState) -> ResourceLocation,
  private val itemTexLocationRetriever: (ItemStack) -> ResourceLocation
) : IModel, IIconRegister {
  private val armLength: Double = (1 - cableWidth) / 2
  private val armInnerLength: Double = armLength - cableHeight
  private val armInnerSp: Double = armLength - armInnerLength
  private val scaleFactor: Double = textureSize / 16F

  private var textures: Map<ResourceLocation, TextureAtlasSprite> = emptyMap()

  override lateinit var particleTexture: TextureAtlasSprite

  // texture positions
  private val arm1TopUv = Vec2(0.0, 0.0)
  private val arm2TopUv = Vec2(0.0, armLength + cableWidth) / scaleFactor
  private val centerTopUv = Vec2(0.0, armLength) / scaleFactor
  private val centerTopCUv = Vec2(0.0, 1.0) / scaleFactor
  private val arm1Side1Uv = Vec2(cableWidth, 0.0) / scaleFactor
  private val arm2Side1Uv = Vec2(cableWidth, armLength + cableWidth) / scaleFactor
  private val centerSide1Uv = Vec2(cableWidth, armLength) / scaleFactor
  private val arm1Side2Uv = Vec2(cableWidth + cableHeight, 0.0) / scaleFactor
  private val arm2Side2Uv = Vec2(cableWidth + cableHeight, armLength + cableWidth) / scaleFactor
  private val centerSide2Uv = Vec2(cableWidth + cableHeight, armLength) / scaleFactor
  private val arm1BottomUv = Vec2(cableWidth + 2 * cableHeight, 0.0) / scaleFactor
  private val arm2BottomUv = Vec2(cableWidth + 2 * cableHeight, armLength + cableWidth) / scaleFactor
  private val centerBottomUv = Vec2(cableWidth + 2 * cableHeight, armLength) / scaleFactor
  private val cableFrontUv = Vec2(cableWidth, 1.0) / scaleFactor
  private val cableBackUv = Vec2(cableWidth + cableHeight, 1.0) / scaleFactor
  private val cornerTop1Uv = Vec2(0.0, 1.0 + cableWidth) / scaleFactor
  private val cornerTop2Uv = Vec2(cableWidth + 2 * cableHeight, 1.0 + cableWidth) / scaleFactor
  private val cornerSide1Uv = Vec2(cableWidth, 1.0 + cableWidth) / scaleFactor
  private val cornerSide2Uv = Vec2(cableWidth + cableHeight, 1.0 + cableWidth) / scaleFactor
  private val icornerSide1Uv = Vec2(2 * cableWidth + 2 * cableHeight, 0.0) / scaleFactor
  private val icornerSide2Uv = Vec2(2 * cableWidth + 2 * cableHeight, cableHeight) / scaleFactor
  private val center8TopUv = Vec2(0.0, 0.25) / scaleFactor
  private val center8Top2Uv = arm2TopUv
  private val center8Arm1Side1Uv = Vec2(cableWidth, 0.25) / scaleFactor
  private val center8Arm1Side2Uv = Vec2(cableWidth + cableHeight, 0.25) / scaleFactor
  private val center8Arm2Side1Uv = arm2Side1Uv
  private val center8Arm2Side2Uv = arm2Side2Uv
  private val innerTop1Uv = Vec2(0.0, armInnerSp) / scaleFactor
  private val innerTop2Uv = arm2TopUv
  private val innerArm1Side1Uv = Vec2(cableWidth, armInnerSp) / scaleFactor
  private val innerArm1Side2Uv = Vec2(cableWidth + cableHeight, armInnerSp) / scaleFactor
  private val innerArm2Side1Uv = arm2Side1Uv
  private val innerArm2Side2Uv = arm2Side2Uv

  constructor(cableWidth: Double, cableHeight: Double, textureSize: Double, texLocation: ResourceLocation) : this(cableWidth, cableHeight, textureSize, listOf(texLocation), { texLocation }, { texLocation })

  override fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad> {
    val texture = textures[texLocationRetriever(state)]!!
    particleTexture = texture

    val c = (state.getValue(BlockWire.PropConnections) ?: emptyList()).toTypedArray()
    val side = state.getValue(BlockWire.PropFacing)

    if (face !in listOf(null, side)) return emptyList()

    return if (face == null) mkQuads(texture, side, *c).map(Quad::bake)
    else emptyList() // TODO render bottom of wire
  }

  override fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad> {
    val texture = textures[itemTexLocationRetriever(stack)]!!
    particleTexture = texture

    if (face != null) return emptyList()

    return mkQuads(texture, DOWN, External, External, External, External).map { it.translate(Vec3(0.0, 0.275, 0.0)) }.map(Quad::bake)
  }

  private fun mkQuads(texture: TextureAtlasSprite, side: EnumFacing, vararg c: EnumWireConnection): List<Quad> {
    var quads: List<Quad> = emptyList()
    val crossing = c.withIndex()
      .map { it.value to c[(it.index + 1) % c.size] }
      .any { it.first.renderType != EnumWireRender.Invisible && it.second.renderType != EnumWireRender.Invisible }
    if (c.all { it == EnumWireConnection.None }) {
      quads += mkCenter(texture, side, None, External, None, External)
      var b = false
      for (facing in BlockWire.lookupMap[side]!!) {
        quads += mkUnconnectedExt(texture, EnumEdge.fromFaces(side, facing), b)
        b = !b
      }
    } else for ((i, c1) in c.withIndex()) {
      val edge = EnumEdge.fromFaces(side, BlockWire.lookupMap[side]!![i])
      quads += mkCenter(texture, side, *c)
      quads += when (c1) {
        EnumWireConnection.None -> mkUnconnectedExt(texture, edge, !crossing && c[(i + 2) % 4] != EnumWireConnection.None)
        EnumWireConnection.External -> mkNormalExt(texture, edge)
        EnumWireConnection.Internal -> mkICornerExt(texture, edge)
        EnumWireConnection.Corner -> mkCornerExt(texture, edge)
      }
    }
    return quads
  }

  private fun mkCenter(texture: TextureAtlasSprite, side: EnumFacing, vararg c: EnumWireConnection): List<Quad> {
    val crossing = c.withIndex()
      .map { it.value to c[(it.index + 1) % c.size] }
      .any { it.first.renderType != EnumWireRender.Invisible && it.second.renderType != EnumWireRender.Invisible }
    return if (!crossing && c.any { it.renderType != EnumWireRender.Invisible }) {
      val up = c.withIndex().filter { it.value.renderType != EnumWireRender.Invisible }.map { TransformRules.getRule(EnumEdge.fromFaces(side, BlockWire.lookupMap[side]!![it.index])) }.first()
      listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, armLength, cableWidth, 0.0, cableWidth, UP,
          centerTopUv, cableWidth, cableWidth, texture, scaleFactor).mapIf(up.useAlt) { it.mirrorTextureY }
      ).map(up.op)
    } else {
      val up = TransformRules.getRule(side)
      listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, armLength, cableWidth, 0.0, cableWidth, UP,
          if (crossing) centerTopCUv else centerTopUv, cableWidth, cableWidth, texture, scaleFactor)
      ).map(up.op)
    }
  }

  private fun mkUnconnectedExt(texture: TextureAtlasSprite, edge: EnumEdge, extendEnd: Boolean): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkUnconnectedExt(texture, edge, rule.useAlt, rule.useAltForBase, extendEnd).map(rule.op)
  }

  private fun mkUnconnectedExt(texture: TextureAtlasSprite, edge: EnumEdge, alt: Boolean, altBase: Boolean, extendEnd: Boolean): List<Quad> {
    if (!extendEnd) {
      val front = listOf(centerSide1Uv, centerSide2Uv)[packInt(altBase)]
      val rules = TransformRules.getRule(edge)
      return listOf(QuadFactory.makeQuadw(armLength, 0.0, armLength, cableWidth, cableHeight, 0.0, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90.mapIf(rules.mirrorBase) { it.mirrorTextureX })
    } else {
      val key = packInt(false, alt)
      val key2 = key shr 1
      val list = listOf(center8Arm1Side1Uv, center8Arm1Side2Uv, center8Arm2Side1Uv, center8Arm2Side2Uv)
      val side1 = list[key]
      val side2 = list[key xor 1]
      val front = listOf(cableFrontUv, cableBackUv)[key2]
      val top = listOf(center8TopUv, center8Top2Uv)[key2]

      return listOf(
        QuadFactory.makeQuadw(armLength, cableHeight, 0.25, cableWidth, 0.0, armLength - 0.25, UP, top, cableWidth, armLength - 0.25, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
        QuadFactory.makeQuadw(armLength, cableHeight, 0.25, 0.0, -cableHeight, armLength - 0.25, EAST, side1, cableHeight, armLength - 0.25, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
        QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0.25, 0.0, -cableHeight, armLength - 0.25, WEST, side2, cableHeight, armLength - 0.25, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
        QuadFactory.makeQuadw(armLength, 0.0, 0.25, cableWidth, cableHeight, 0.0, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
      )
    }
  }

  private fun mkNormalExt(texture: TextureAtlasSprite, edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkNormalExt(texture, rule.useAlt).map(rule.op)
  }

  private fun mkNormalExt(texture: TextureAtlasSprite, alt: Boolean): List<Quad> {
    val key = packInt(false, alt)
    val key2 = key shr 1
    val list = listOf(arm1Side1Uv, arm1Side2Uv, arm2Side1Uv, arm2Side2Uv)
    val side1 = list[key]
    val side2 = list[key xor 1]
    val front = listOf(cableFrontUv, cableBackUv)[key2]
    val top = listOf(arm1TopUv, arm2TopUv)[key2]

    return listOf(
      QuadFactory.makeQuadw(armLength, cableHeight, 0.0, cableWidth, 0.0, armLength, UP, top, cableWidth, armLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, 0.0, 0.0, -cableHeight, armLength, EAST, side1, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0.0, 0.0, -cableHeight, armLength, WEST, side2, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, 0.0, 0.0, cableWidth, cableHeight, 0.0, SOUTH, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
    )
  }

  private fun mkCornerExt(texture: TextureAtlasSprite, edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkCornerExt(texture, rule.useAlt).map(rule.op)
  }

  private fun mkCornerExt(texture: TextureAtlasSprite, alt: Boolean): List<Quad> {
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
      QuadFactory.makeQuadw(armLength, cableHeight, 0.0, cableWidth, 0.0, armLength, UP, top, cableWidth, armLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, 0.0, 0.0, -cableHeight, armLength, EAST, side1, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, 0.0, 0.0, -cableHeight, armLength, WEST, side2, cableHeight, armLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, cableHeight, -cableHeight, cableWidth, 0.0, cableHeight, UP, ctop1, cableWidth, cableHeight, texture, scaleFactor),
      QuadFactory.makeQuadw(armLength, 0.0, -cableHeight, cableWidth, cableHeight, 0.0, SOUTH, ctop2, cableWidth, cableHeight, texture, scaleFactor).mirrorTextureY,
      QuadFactory.makeQuadw(armLength, 0.0, 0.0, 0.0, cableHeight, -cableHeight, EAST, cside1, cableHeight, cableHeight, texture, scaleFactor),
      QuadFactory.makeQuadw(armLength + cableWidth, 0.0, 0.0, 0.0, cableHeight, -cableHeight, WEST, cside2, cableHeight, cableHeight, texture, scaleFactor).flipTexturedSide,
      QuadFactory.makeQuadw(armLength, 0.0, 0.0, cableWidth, 0.0, -cableHeight, DOWN, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90
    )
  }

  private fun mkICornerExt(texture: TextureAtlasSprite, edge: EnumEdge): List<Quad> {
    val rule = TransformRules.getRule(edge)
    return mkICornerExt(texture, rule.useAlt).map(rule.op)
  }

  private fun mkICornerExt(texture: TextureAtlasSprite, alt: Boolean): List<Quad> {
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
      QuadFactory.makeQuadw(armLength, cableHeight, armInnerSp, cableWidth, 0.0, armInnerLength, UP, top, cableWidth, armInnerLength, texture, scaleFactor).mapIf(alt) { it.mirrorTextureY },
      QuadFactory.makeQuadw(armLength, cableHeight, armInnerSp, 0.0, -cableHeight, armInnerLength, EAST, side1, cableHeight, armInnerLength, texture, scaleFactor).rotatedTexture90.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength + cableWidth, cableHeight, armInnerSp, 0.0, -cableHeight, armInnerLength, WEST, side2, cableHeight, armInnerLength, texture, scaleFactor).rotatedTexture90.flipTexturedSide.rotatedTexture180.mapIf(alt) { it.mirrorTextureX },
      QuadFactory.makeQuadw(armLength, cableHeight, 0.0, cableWidth, 0.0, armInnerSp, UP, front, cableHeight, cableWidth, texture, scaleFactor).rotatedTexture90,
      QuadFactory.makeQuadw(armLength, 0.0, 0.0, 0.0, cableHeight, armInnerSp, EAST, cside1, cableHeight, cableHeight, texture, scaleFactor).flipTexturedSide,
      QuadFactory.makeQuadw(armLength + cableWidth, 0.0, 0.0, 0.0, cableHeight, armInnerSp, WEST, cside2, cableHeight, cableHeight, texture, scaleFactor)
    )
  }

  override fun registerIcons(textureMap: TextureMap) {
    textures = texLocations.map { it to textureMap.registerSprite(it) }.toMap()
  }

  @Suppress("USELESS_ELVIS")
  override fun createKey(state: IExtendedBlockState, face: EnumFacing?): String = super.createKey(state, face) + (state[BlockWire.PropConnections] ?: emptyList()).joinToString()
}