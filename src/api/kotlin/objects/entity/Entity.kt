package therealfarfetchd.quacklib.api.objects.entity

//import therealfarfetchd.quacklib.api.core.Unsafe
//import therealfarfetchd.quacklib.api.objects.Instance
//import therealfarfetchd.quacklib.api.objects.Instantiable
//import therealfarfetchd.quacklib.api.objects.Registered
//import therealfarfetchd.quacklib.api.objects.world.WorldMutable
//import therealfarfetchd.quacklib.api.tools.Position
//
//typealias MCEntity = net.minecraftforge.fml.common.registry.EntityEntry
//typealias MCEntityInstance = net.minecraft.entity.Entity
//
//interface EntityType : Instantiable, Registered {
//
//  val Unsafe.mc: MCEntity
//
//  fun create(world: WorldMutable, position: Position = Position.Origin, spawn: Boolean = true): Entity
//
//}
//
//interface Entity : Instance<EntityType> {
//
//  val Unsafe.mc: MCEntityInstance
//
//}
//
//interface UnsafeExtEntity : Unsafe {
//
//  val EntityType.mc
//    get() = self.mc
//
//  val Entity.mc
//    get() = self.mc
//
//}