package therealfarfetchd.quacklib.core.init

import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.block.component.prefab.ComponentItemForBlock
import therealfarfetchd.quacklib.block.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.core.APIImpl
import therealfarfetchd.quacklib.core.mod.CommonProxy
import therealfarfetchd.quacklib.item.component.prefab.ComponentPlaceBlock
import therealfarfetchd.quacklib.objects.block.CreatedBlockTypeImpl
import therealfarfetchd.quacklib.objects.item.CreatedItemTypeImpl

class InitializationContextImpl(val mod: BaseMod) : InitializationContext {

  override fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit): BlockType {
    val conf = BlockConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addBlockTemplate(conf)
    return CreatedBlockTypeImpl(conf.rl, conf)
  }

  override fun addItem(name: String, op: ItemConfigurationScope.() -> Unit): ItemType {
    val conf = ItemConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addItemTemplate(conf)
    return CreatedItemTypeImpl(conf.rl, conf)
  }

  override fun addPlacementItem(block: BlockType, name: String, op: ItemConfigurationScope.() -> Unit): ItemType {
    // TODO no hax pls :V
    val definition = (mod.proxy as CommonProxy).blockTemplates.firstOrNull { it.rl == block.registryName }

    val item = addItem(name) {
      if (definition != null && definition.isMultipart) apply(APIImpl.multipartAPI.createPlacementComponent(block))
      else apply(ComponentPlaceBlock(block))
      op(this)
    }

    definition?.apply(ComponentItemForBlock(item(name)))
    return item
  }

  override fun addTab(name: String, icon: ItemType, op: TabConfigurationScope.() -> Unit) {
    val conf = TabConfigurationScopeImpl(mod.modid, name, icon, this).also(op)
    (mod.proxy as CommonProxy).addTabTemplate(conf)
  }

}