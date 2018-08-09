package therealfarfetchd.quacklib.item.impl

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.item.component.*
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.objects.item.ItemImpl
import therealfarfetchd.quacklib.objects.world.toWorld
import kotlin.reflect.jvm.jvmName

class ItemQuackLib(val type: ItemType) : Item(), ItemExtraDebug {

  val components = type.components.asReversed()

  val cUse = getComponentsOfType<ItemComponentUse>()
  val cUpdate = getComponentsOfType<ItemComponentTickable>()
  val cCapability = getComponentsOfType<ItemComponentCapability>()

  init {
    registryName = type.registryName
    translationKey = type.registryName.toString()

    getComponentsOfType<ItemComponentTool>()
      .flatMap(ItemComponentTool::toolTypes)
      .forEach { setHarvestLevel(it.toolName, it.level) }
  }

  override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
    for (component in cUse) {
      val ret = component.onUse(ItemImpl(player.getHeldItem(hand)), player, worldIn.toWorld(), pos.toVec3i(), hand, facing, Vec3(hitX, hitY, hitZ))
      if (ret != EnumActionResult.PASS) return ret
    }
    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
  }

  override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
    for (component in cUpdate) {
      component.onTick(ItemImpl(stack))
    }
  }

  override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
    return CapabilityProviderItem(ItemImpl(type, stack))
  }

  override fun addInformation(world: World, stack: ItemStack, hand: EnumHand, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {
    val h = when (hand) {
      EnumHand.MAIN_HAND -> "main hand"
      EnumHand.OFF_HAND -> "offhand"
    }

    left += ""
    left +=
      """§b§l[Item in $h]
        |Components (${components.size}):
      """.trimMargin().lines()
    left += components.flatMap { getComponentInfo(stack, it) }
  }

  private fun getComponentInfo(stack: ItemStack, c: ItemComponent): List<String> {
    val bi = ItemImpl(stack)

    var descString = " - "
    descString += c::class.simpleName ?: c::class.qualifiedName ?: c::class.jvmName
    if (c is ItemComponentRegistered) descString += " (${c.rl})"
    if (c is ItemComponentInfo) descString += ":"

    var list = listOf(descString)
    if (c is ItemComponentInfo) {
      list += c.getInfo(bi).map { it.prependIndent("     ") }
    }
    return list
  }

  override fun isInCreativeTab(targetTab: CreativeTabs): Boolean {
    return targetTab is TabQuackLib || targetTab == CreativeTabs.SEARCH
  }

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

  override fun toString(): String {
    return "Item '${type.registryName}' (${components.size} components)"
  }

}