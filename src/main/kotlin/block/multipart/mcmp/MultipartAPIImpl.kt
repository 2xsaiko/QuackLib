package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.MCMultiPart
import mcmultipart.api.event.DrawMultipartHighlightEvent
import mcmultipart.api.ref.MCMPCapabilities
import mcmultipart.api.slot.EnumCenterSlot
import mcmultipart.api.slot.EnumCornerSlot
import mcmultipart.api.slot.EnumEdgeSlot
import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.block.Block
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import therealfarfetchd.quacklib.api.block.multipart.PartSlot
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.block.impl.BlockAdvancedOutline
import therealfarfetchd.quacklib.block.impl.BlockExtraDebug
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.block.multipart.MultipartAPIInternal
import therealfarfetchd.quacklib.config.QuackLibConfig
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.tools.getTextState
import mcmultipart.api.slot.IPartSlot as MCMPPartSlot

object MultipartAPIImpl : MultipartAPIInternal {

  val multipartTile = ResourceLocation(ModID, "multipart")

  val slotMap: Map<PartSlot, MCMPPartSlot> =
    mapOf(PartSlot.CENTER to EnumCenterSlot.CENTER as MCMPPartSlot) +
    EnumFaceSlot.VALUES.associateBy { PartSlot.getFace(it.facing) } +
    EnumEdgeSlot.VALUES.associateBy { PartSlot.getEdge(it.axis, it.face1, it.face2) } +
    EnumCornerSlot.VALUES.associateBy { PartSlot.getCorner(it.face1, it.face2, it.face3) }

  val slotMapRev: Map<MCMPPartSlot, PartSlot> = slotMap.entries.associate { it.value to it.key }

  override fun createPlacementComponent(type: BlockType): ItemComponent {
    return ComponentPlaceMultipart(type)
  }

  override fun registerBlock(e: RegistryEvent.Register<Block>, type: BlockTypeImpl) {
    val block = BlockQuackLib(type)
    val part = MultipartQuackLib(block)
    e.registry.register(block)
    MultipartRegistry.INSTANCE.registerPartWrapper(block, part)
  }

  @SubscribeEvent
  fun attachCapabilities(e: AttachCapabilitiesEvent<TileEntity>) {
    (e.`object` as? TileQuackLib)?.also {
      e.addCapability(multipartTile, TileCapabilityProvider(it))
    }
  }

  @SideOnly(Side.CLIENT)
  override fun onDrawOverlay(world: World, hit: RayTraceResult, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {
    if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
      val pos = hit.blockPos
      val te = world.getTileEntity(pos) ?: return
      val slothit = MCMultiPart.slotRegistry.getValue(hit.subHit) ?: return
      te.getCapability(MCMPCapabilities.MULTIPART_CONTAINER, null)?.get(slothit)?.ifPresent { part ->
        val state = part.state.getActualState(part.partWorld, part.partPos)

        if (QuackLibConfig.alwaysShowMultipartDebug || part.part.block is BlockExtraDebug)
          getTextState(right, state)

        (part.part.block as? BlockExtraDebug)?.also {
          it.addInformation(part.partWorld, part.partPos, state, player, left, right)
        }
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  fun onDrawMultipartOverlay(e: DrawMultipartHighlightEvent) {
    val block = e.partInfo.state.block
    if (block is BlockAdvancedOutline) {
      if (e.target.typeOfHit != RayTraceResult.Type.BLOCK) return
      val box = block.getSelectedBoundingBox(e.partInfo.state, e.partInfo.partWorld, e.partInfo.partPos, e.target)
      if (box != null) {
        val player = e.player
        val x = player.lastTickPosX + (player.posX - player.lastTickPosX) * e.partialTicks
        val y = player.lastTickPosY + (player.posY - player.lastTickPosY) * e.partialTicks
        val z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * e.partialTicks
        enableBlend()
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO)
        glLineWidth(2.0f)
        disableTexture2D()
        depthMask(false)

        RenderGlobal.drawSelectionBoundingBox(box.grow(0.002).offset(-x, -y, -z), 0f, 0f, 0f, .4f)

        depthMask(true)
        enableTexture2D()
        disableBlend()
      }
      e.isCanceled = true
    }
  }

}