package therealfarfetchd.quacklib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import therealfarfetchd.quacklib.common.QBContainer
import therealfarfetchd.quacklib.common.TestQB

/**
 * Created by marco on 08.07.17.
 */

const val ModID = "quacklib"

@Mod(modid = ModID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object QuackLib {
  val debug = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

  val testblock: QBContainer = QBContainer(ResourceLocation("quacklib", "testblock1"), ::TestQB)
  val tbitem: Item = ItemBlock(testblock)

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    if (!debug) return
    e.registry.register(testblock)
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    if (!debug) return
    e.registry.register(tbitem)
  }

}