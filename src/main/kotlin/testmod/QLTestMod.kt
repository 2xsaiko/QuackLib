package therealfarfetchd.quacklib.testmod

import net.minecraft.block.material.Material
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.item.component.prefab.ComponentPlaceBlock

@Mod(modid = "qltestmod", version = "1.0.0", name = "QuackLib Test Mod", dependencies = "required-after:quacklib", modLanguageAdapter = KotlinAdapter)
object QLTestMod : BaseMod() {

  override fun initContent(ctx: InitializationContext) = ctx {
    addBlock("test_block") {
      material = Material.IRON
      hardness = 0.5f
      validTools = setOf(Tool("pickaxe", 2))

      apply(ComponentTestItemDrop(ItemStack(Items.DIAMOND, 5)))
    }

    addItem("test_item") {
      apply(ComponentPlaceBlock(block("test_block")))
    }

    addTab("standard", item("test_item")) {
      include(item("test_item"))
    }
  }

}