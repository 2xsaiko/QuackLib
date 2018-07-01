package therealfarfetchd.quacklib.core.init

import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope
import therealfarfetchd.quacklib.block.component.prefab.ComponentItemForBlock
import therealfarfetchd.quacklib.block.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.core.APIImpl
import therealfarfetchd.quacklib.core.mod.CommonProxy
import therealfarfetchd.quacklib.item.component.prefab.ComponentPlaceBlock

class InitializationContextImpl(val mod: BaseMod) : InitializationContext {

  override fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit): BlockReference {
    val conf = BlockConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addBlockTemplate(conf)
    return block(conf.rl)
  }

  override fun addItem(name: String, op: ItemConfigurationScope.() -> Unit): ItemReference {
    val conf = ItemConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addItemTemplate(conf)
    return item(conf.rl)
  }

  override fun addPlacementItem(block: BlockReference, name: String, op: ItemConfigurationScope.() -> Unit): ItemReference {
    // TODO no hax pls :V
    val definition = (mod.proxy as CommonProxy).blockTemplates.firstOrNull { it.rl == block.rl }

    val item = addItem(name) {
      if (definition != null && definition.isMultipart) apply(APIImpl.multipartAPI.createPlacementComponent(definition))
      else apply(ComponentPlaceBlock(block))
      op(this)
    }

    definition?.apply(ComponentItemForBlock(item(name)))
    return item
  }

  override fun addTab(name: String, icon: ItemReference, op: TabConfigurationScope.() -> Unit) {
    val conf = TabConfigurationScopeImpl(mod.modid, name, icon, this).also(op)
    (mod.proxy as CommonProxy).addTabTemplate(conf)
  }

}