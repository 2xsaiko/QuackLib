//package therealfarfetchd.quacklib.block.multipart.cbmp
//
//import codechicken.lib.data.MCDataInput
//import codechicken.lib.data.MCDataOutput
//import codechicken.lib.raytracer.IndexedCuboid6
//import codechicken.lib.vec.Cuboid6
//import codechicken.multipart.*
//import net.minecraft.block.Block
//import net.minecraft.block.state.BlockStateContainer
//import net.minecraft.block.state.IBlockState
//import net.minecraft.nbt.NBTTagCompound
//import net.minecraft.util.BlockRenderLayer
//import net.minecraft.util.EnumFacing
//import net.minecraft.util.ResourceLocation
//import net.minecraft.util.math.AxisAlignedBB
//import net.minecraftforge.common.capabilities.Capability
//import net.minecraftforge.common.capabilities.ICapabilityProvider
//import therealfarfetchd.quacklib.api.block.component.*
//import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
//import therealfarfetchd.quacklib.block.impl.BlockQuackLib
//import therealfarfetchd.quacklib.block.impl.DataContainer
//
//class MultipartQuackLib() : TMultiPart(), ICapabilityProvider, TCuboidPart, TNormalOcclusionPart, TSlottedPart, IModelRenderPart
//// , TIconHitEffectsPart
//{
//
//  val c = DataContainer()
//
//  var cActivate = c.getComponentsOfType<BlockComponentActivation>()
//  var cDrops = c.getComponentsOfType<BlockComponentDrops>()
//  var cPickBlock = c.getComponentsOfType<BlockComponentPickBlock>()
//  var cPart = c.getComponentsOfType<BlockComponentData<*>>()
//  var cCollision = c.getComponentsOfType<BlockComponentCollision>()
//  var cMouseOver = c.getComponentsOfType<BlockComponentMouseOver>()
//  var cCustomMouseOver = c.getComponentsOfType<BlockComponentCustomMouseOver>()
//  var cNeighborListener = c.getComponentsOfType<BlockComponentNeighborListener>()
//  var cPlacementCheck = c.getComponentsOfType<BlockComponentPlacementCheck>()
//  var cCapability = c.getComponentsOfType<BlockComponentCapability>()
//
//  constructor(def: BlockConfiguration) : this() {
//    c.setConfiguration(def)
//    updateComponents()
//  }
//
//  fun updateComponents() {
//    cActivate = c.getComponentsOfType()
//    cDrops = c.getComponentsOfType()
//    cPickBlock = c.getComponentsOfType()
//    cPart = c.getComponentsOfType()
//    cCollision = c.getComponentsOfType()
//    cMouseOver = c.getComponentsOfType()
//    cCustomMouseOver = c.getComponentsOfType()
//    cNeighborListener = c.getComponentsOfType()
//    cPlacementCheck = c.getComponentsOfType()
//    cCapability = c.getComponentsOfType()
//  }
//
//  override fun getType(): ResourceLocation = c.def.rl
//
//  override fun getBounds(): Cuboid6 {
//    val data = getBlockData()
//
//    return if (cMouseOver.isNotEmpty()) {
//      cMouseOver
//        .flatMap { it.getRaytraceBoundingBoxes(data) }
//        .also { if (it.isEmpty()) return NOPE_BOX }
//        .reduce(AxisAlignedBB::union)
//        .let(::Cuboid6)
//    } else Cuboid6.full
//  }
//
//  override fun getSubParts(): Iterable<IndexedCuboid6> {
//    return listOf(IndexedCuboid6(0, bounds))
//  }
//
//  override fun getCollisionBoxes(): Iterable<Cuboid6> {
//    if (world() == null) return placementBoxes.get()
//
//    val data = getBlockData()
//
//    return (cCollision.takeIf { it.isNotEmpty() }?.flatMap { it.getCollisionBoundingBoxes(data) }
//            ?: setOf(Block.FULL_BLOCK_AABB))
//      .map(::Cuboid6)
//  }
//
//  override fun canRenderInLayer(layer: BlockRenderLayer): Boolean {
//    TODO("not implemented")
//  }
//
//  override fun getModelPath(): ResourceLocation = c.def.rl
//
//  override fun createBlockStateContainer(): BlockStateContainer {
//    val bs = BlockQuackLib.createBlockState(null)
//
//    //    properties = bs._2()
//    //    extproperties = bs._3()
//    //    propRetrievers = bs._4()
//    //    extpropRetrievers = bs._5()
//
//    return bs._1()
//  }
//
//  override fun getCurrentState(state: IBlockState?): IBlockState {
//    TODO("not implemented")
//  }
//
//  //  override fun getBreakingIcon(hit: CuboidRayTraceResult?): TextureAtlasSprite {
//  //    TODO("not implemented")
//  //  }
//  //
//  //  override fun getBrokenIcon(side: Int): TextureAtlasSprite {
//  //    TODO("not implemented")
//  //  }
//  //
//
//  override fun getOcclusionBoxes(): Iterable<Cuboid6> {
//    return collisionBoxes
//  }
//
//  override fun getSlotMask(): Int = 0
//
//  override fun save(tag: NBTTagCompound) {
//    c.saveData(tag) { _, prop -> prop.persistent }
//  }
//
//  override fun load(tag: NBTTagCompound) {
//    c.loadData(tag) { _, prop -> prop.persistent }
//    updateComponents()
//  }
//
//  override fun writeDesc(packet: MCDataOutput) {
//    val nbt = NBTTagCompound()
//    c.saveData(nbt) { _, prop -> prop.render || prop.sync }
//    packet.writeNBTTagCompound(nbt)
//  }
//
//  override fun readDesc(packet: MCDataInput) {
//    val nbt = packet.readNBTTagCompound()
//    c.loadData(nbt) { _, prop -> prop.render || prop.sync }
//    updateComponents()
//  }
//
//  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
//    // TODO
//    return null
//  }
//
//  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
//    // TODO
//    return false
//  }
//
//  private fun getBlockData() = BlockDataDirectRef(c, world(), pos())
//
//  companion object {
//    val NOPE_BOX = Cuboid6(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
//
//    val placementBoxes = ThreadLocal<List<Cuboid6>>().also { it.set(emptyList()) }
//  }
//
//}