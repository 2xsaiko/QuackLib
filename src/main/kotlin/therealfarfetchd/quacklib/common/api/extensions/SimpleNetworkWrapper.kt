package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper

fun SimpleNetworkWrapper.sendToAllWatching(message: IMessage, dimensionId: Int, pos: BlockPos) {
  val world = DimensionManager.getWorld(dimensionId)
  val chunkMap = world.playerChunkMap
  val chunkX = pos.x shr 4
  val chunkZ = pos.z shr 4
  world.playerEntities
    .filter { chunkMap.isPlayerWatchingChunk(it as EntityPlayerMP, chunkX, chunkZ) }
    .forEach { sendTo(message, it as EntityPlayerMP) }
}