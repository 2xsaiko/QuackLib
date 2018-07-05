//@file:Suppress("UNCHECKED_CAST")
//
//package therealfarfetchd.quacklib.block.data
//
//import net.minecraft.block.state.IBlockState
//import net.minecraft.util.math.BlockPos
//import net.minecraft.world.IBlockAccess
//import net.minecraft.world.World
//import therealfarfetchd.quacklib.api.block.component.ImportedValue
//import therealfarfetchd.quacklib.api.block.data.BlockDataPart
//import therealfarfetchd.quacklib.api.block.data.PartAccessToken
//import therealfarfetchd.quacklib.block.impl.DataContainer
//import therealfarfetchd.quacklib.block.impl.TileQuackLib
//
//abstract class BlockDataBase : BlockData {
//
//  override fun <T : BlockDataPart> get(token: PartAccessToken<T>): T {
//    token as? PartAccessTokenImpl ?: error("Invalid token: $token")
//
//    val te = world.getTileEntity(pos) as? TileQuackLib
//             ?: error("Missing Tile Entity at $pos!")
//    return te.c.parts.getValue(token.rl) as T
//  }
//
//  override fun <T> get(value: ImportedValue<T>): T =
//    value.retrieve(this)
//
//}
//
//data class BlockDataMutableImpl(override val world: World, override val pos: BlockPos, override val state: IBlockState = world.getBlockState(pos)) : BlockDataBase(), BlockDataMutable
//
//data class BlockDataImpl(override val world: IBlockAccess, override val pos: BlockPos, override val state: IBlockState = world.getBlockState(pos)) : BlockDataBase()
//
//data class BlockDataDirectRef(val container: DataContainer, override val world: World, override val pos: BlockPos, override val state: IBlockState = world.getBlockState(pos)) : BlockDataMutable {
//
//  override fun <T : BlockDataPart> get(token: PartAccessToken<T>): T {
//    token as? PartAccessTokenImpl ?: error("Invalid token: $token")
//
//    return container.parts.getValue(token.rl) as T
//  }
//
//  override fun <T> get(value: ImportedValue<T>): T =
//    value.retrieve(this)
//
//}