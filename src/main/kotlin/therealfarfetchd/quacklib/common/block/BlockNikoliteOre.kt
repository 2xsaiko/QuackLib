package therealfarfetchd.quacklib.common.block

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.QCreativeTab
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.item.ItemComponent
import java.util.*

object BlockNikoliteOre : Block(Material.ROCK) {
  val Item: ItemBlock = ItemBlock(this)

  init {
    soundType = SoundType.STONE
    registryName = ResourceLocation(ModID, "nikolite_ore")
    unlocalizedName = registryName.toString()
    setCreativeTab(QCreativeTab)
    setHardness(3.0F)
    setResistance(5.0F)

    Item.registryName = registryName
  }

  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item = ItemComponent

  override fun damageDropped(state: IBlockState?): Int = 23

  override fun quantityDroppedWithBonus(fortune: Int, random: Random): Int = quantityDropped(random) + random.nextInt(fortune + 1)

  override fun quantityDropped(random: Random): Int = 4 + random.nextInt(2)

  override fun getSilkTouchDrop(state: IBlockState): ItemStack = makeStack()

  override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState): ItemStack = makeStack()
}