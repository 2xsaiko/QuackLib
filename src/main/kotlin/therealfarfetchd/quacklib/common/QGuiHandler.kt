package therealfarfetchd.quacklib.common

import mcmultipart.api.container.IMultipartContainer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import therealfarfetchd.quacklib.client.api.gui.GuiApi
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.api.qblock.QBlock

object QGuiHandler : IGuiHandler {

  private var entriesClient: Map<ResourceLocation, (ResourceLocation, QBlock, EntityPlayer) -> GuiScreen> = emptyMap()
  private var entriesServer: Map<ResourceLocation, (ResourceLocation, QBlock, EntityPlayer) -> Container> = emptyMap()

  fun registerClientGui(rl: ResourceLocation, guiLocation: ResourceLocation = rl) {
    registerClientGui(rl) { _, qb, _ ->
      GuiApi.loadGui(guiLocation, mapOf("qb" to qb))
    }
  }

  fun registerClientGui(rl: ResourceLocation, op: (ResourceLocation, QBlock, EntityPlayer) -> GuiScreen) {
    entriesClient += rl to op
  }

  fun registerServerGui(rl: ResourceLocation, op: (ResourceLocation, QBlock, EntityPlayer) -> Container) {
    entriesServer += rl to op
  }

  fun hasEntry(id: ResourceLocation): Boolean = id in entriesClient || id in entriesServer

  fun getQBlock(id: Int, world: World, x: Int, y: Int, z: Int): QBlock? {
    val pos = BlockPos(x, y, z)
    world.getQBlock(pos)?.also { return it }
    val te = world.getTileEntity(pos)
    if (te is IMultipartContainer) {
      te.parts.values.mapNotNull { it.tile as? QBContainerTile }.map { it.qb }.firstOrNull { it.getBlockLocation() == id }?.also { return it }
    }
    return null
  }

  override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): GuiScreen? {
    val qb = getQBlock(ID, world, x, y, z) ?: return null
    val rl = qb.blockType
    return entriesClient[rl]?.invoke(rl, qb, player)
  }

  override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Container? {
    val qb = getQBlock(ID, world, x, y, z) ?: return null
    val rl = qb.blockType
    return entriesServer[rl]?.invoke(rl, qb, player)
  }
}