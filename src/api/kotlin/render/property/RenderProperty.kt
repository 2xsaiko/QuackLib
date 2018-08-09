package therealfarfetchd.quacklib.api.render.property

import net.minecraft.block.properties.IProperty
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import kotlin.reflect.KClass

interface RenderProperty<C : ComponentRenderProperties, in K, out T> {

  val name: String

  fun getValue(container: K): T

  fun getComponentClass(): KClass<out C>


}

interface RenderPropertyBlock<C : BlockComponentRenderProperties, out T> : RenderProperty<C, Block, T> {

  fun Unsafe.getMCProperty(): PropertyType<T>

}


interface ComponentRenderProperties

sealed class PropertyType<out T> {
  class Standard<T>(val prop: IProperty<*>) : PropertyType<T>()

  class Extended<T>(val prop: IUnlistedProperty<T>) : PropertyType<T>()
}

interface UnsafeExtRP : Unsafe {

  fun <T> RenderPropertyBlock<*, T>.getMCProperty() =
    self.getMCProperty()

}