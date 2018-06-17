package therealfarfetchd.quacklib.block.data

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import therealfarfetchd.quacklib.api.block.data.DataPartSerializationRegistry
import therealfarfetchd.quacklib.api.block.data.Value
import therealfarfetchd.quacklib.tools.getType
import therealfarfetchd.quacklib.tools.loadClass
import java.io.*
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

object DataPartSerializationRegistryImpl : DataPartSerializationRegistry {

  private val priomap: MutableMap<KClass<*>, Int> = mutableMapOf()
  private val savemap: MutableMap<KClass<*>, NBTTagCompound.(String, Any) -> Unit> = mutableMapOf()
  private val loadmap: MutableMap<KClass<*>, NBTTagCompound.(String) -> Any?> = mutableMapOf()
  private val cache: MutableMap<KClass<*>, KClass<*>?> = mutableMapOf()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> register(type: KClass<T>,
                                  save: NBTTagCompound.(name: String, type: T) -> Unit,
                                  load: NBTTagCompound.(name: String) -> T?,
                                  priority: Int) {
    if (type in savemap) error("Serialization impl for $type already registered!")

    if (type == Any::class) error("No.")

    priomap[type] = priority
    savemap[type] = save as NBTTagCompound.(String, Any) -> Unit
    loadmap[type] = load
    cache[type] = type
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : S, S : Any> registerRedirect(type: KClass<T>, from: KClass<S>, cast: (S) -> T, priority: Int) {
    registerRedirect(type, from, { it }, cast, priority)
  }

  override fun <T : Any, S : Any> registerRedirect(type: KClass<T>, from: KClass<S>, castIn: (T) -> S, castOut: (S) -> T, priority: Int) {
    if (type in savemap) error("Serialization impl for $type already registered!")
    if (from !in savemap) error("No serialization impl for $from found")

    register(type,
      savemap.getValue(from).let { { tag: NBTTagCompound, name: String, value: Any -> it(tag, name, castIn(value as T)) } },
      loadmap.getValue(from).let { { tag: NBTTagCompound, name: String -> castOut(it(tag, name) as S) } },
      priority)
  }

  override fun <T : Any> save(tag: NBTTagCompound, name: String, value: T?) {
    if (value == null) {
      val nbt = NBTTagCompound()
      nbt.setBoolean("@null", true)
      tag.setTag(name, nbt)
      return
    }

    val type = value::class

    val st = find(type) ?: error("No serialization impl for $type found!")

    val op = savemap.getValue(st)
    op(tag, name, value)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> load(tag: NBTTagCompound, type: KClass<T>, name: String): Value<T?>? {
    val st = find(type) ?: error("No serialization impl for $type found!")
    if (!tag.hasKey(name)) return null
    if (tag.getCompoundTag(name).hasKey("@null")) return Value(null)

    val op = loadmap.getValue(st)
    return Value(op(tag, name) as T?)
  }

  private fun find(type: KClass<*>): KClass<*>? {
    data class Ref(val depth: Int, val priority: Int, val cls: KClass<*>)

    if (type in cache) return cache[type]
    var types = listOf(type)
    var alltypes: Set<Ref> = emptySet()
    var depth = 0
    while (types.any { it != Any::class }) {
      val c = types.filter { it in savemap }
      alltypes += c.map { Ref(depth, priomap.getValue(it), it) }
      types = types.flatMap { it.superclasses }
      depth++
    }

    val s = alltypes.sortedWith(Comparator { o1, o2 ->
      when {
        o1.priority < o2.priority -> 2
        o1.priority > o2.priority -> -2
        o1.depth > o2.depth -> 1
        o1.depth < o2.depth -> -1
        else -> 0
      }
    })

    val top = s.firstOrNull()

    if (top != null && s.count { it.depth == top.depth && it.priority == top.priority } > 1)
      error("Ambiguous serialization impl for $type: " +
            s.filter { it.depth == top.depth && it.priority == top.priority }.map { it.cls })

    cache[type] = top?.cls
    return top?.cls
  }

  init {
    fun <T : Any> registerSimple(type: KClass<T>, tagid: Int, set: NBTTagCompound.(String, T) -> Unit, get: NBTTagCompound.(String) -> T) {
      register(
        type = type,
        save = { name, obj -> set(name, obj) },
        load = { name -> get(name).takeIf { hasKey(name, tagid) } }
      )
    }

    // Primitives
    registerSimple(Boolean::class, Constants.NBT.TAG_BYTE, NBTTagCompound::setBoolean, NBTTagCompound::getBoolean)
    registerSimple(Byte::class, Constants.NBT.TAG_BYTE, NBTTagCompound::setByte, NBTTagCompound::getByte)
    registerSimple(Short::class, Constants.NBT.TAG_SHORT, NBTTagCompound::setShort, NBTTagCompound::getShort)
    registerSimple(Int::class, Constants.NBT.TAG_INT, NBTTagCompound::setInteger, NBTTagCompound::getInteger)
    registerSimple(Long::class, Constants.NBT.TAG_LONG, NBTTagCompound::setLong, NBTTagCompound::getLong)
    registerSimple(Float::class, Constants.NBT.TAG_FLOAT, NBTTagCompound::setFloat, NBTTagCompound::getFloat)
    registerSimple(Double::class, Constants.NBT.TAG_DOUBLE, NBTTagCompound::setDouble, NBTTagCompound::getDouble)
    registerSimple(String::class, Constants.NBT.TAG_STRING, NBTTagCompound::setString, NBTTagCompound::getString)

    // Collections
    register(
      type = List::class,
      save = { name, type ->
        val list = NBTTagList()
        for (value in type) {
          val nbt = NBTTagCompound()
          if (value != null) {
            save(nbt, "@data", value)
            nbt.setString("@type", find(value::class)!!.java.getType())
          }
          list.appendTag(nbt)
        }
        setTag(name, list)
      },
      load = { name ->
        if (!hasKey(name, Constants.NBT.TAG_LIST)) return@register null
        val list: MutableList<Any?> = mutableListOf()
        val tags = getTagList(name, Constants.NBT.TAG_COMPOUND)
        for (nbt in tags.map { it as NBTTagCompound }) {
          val type = nbt.getString("@type").takeIf { nbt.hasKey("@type") }?.let(::loadClass)
          list += type?.let { load(nbt, it.kotlin, "@data")?.value }
        }
        list
      }
    )
    registerRedirect(Set::class, List::class, { it.toList() }, { it.toSet() })
    registerRedirect(Collection::class, List::class, { it.toList() }, { it })

    register(
      type = Map::class,
      save = { name, type ->
        val list = NBTTagList()
        for ((key, value) in type) {
          val nbt = NBTTagCompound()
          save(nbt, "@key", key)
          save(nbt, "@value", value)
          if (key != null) nbt.setString("@key_type", find(key::class)!!.java.getType())
          if (value != null) nbt.setString("@value_type", find(value::class)!!.java.getType())
          list.appendTag(nbt)
        }
        setTag(name, list)
      },
      load = { name ->
        if (!hasKey(name, Constants.NBT.TAG_LIST)) return@register null
        val list: MutableMap<Any?, Any?> = mutableMapOf()
        val tags = getTagList(name, Constants.NBT.TAG_COMPOUND)
        for (nbt in tags.map { it as NBTTagCompound }) {
          val key = nbt.getString("@key_type").takeIf { nbt.hasKey("@key_type") }?.let(::loadClass)
          val value = nbt.getString("@value_type").takeIf { nbt.hasKey("@value_type") }?.let(::loadClass)
          list += Pair(
            key?.let { load(nbt, it.kotlin, "@key")?.value },
            value?.let { load(nbt, it.kotlin, "@value")?.value }
          )
        }
        list
      }
    )

    // Tuples
    register(
      type = Pair::class,
      save = { name, type ->
        val nbt = NBTTagCompound()
        save(nbt, "@first", type.first)
        save(nbt, "@second", type.second)
        type.first?.also {
          nbt.setString("@first_type", find(it::class)!!.java.getType())
        }
        type.second?.also {
          nbt.setString("@second_type", find(it::class)!!.java.getType())
        }
        setTag(name, nbt)
      },
      load = { name ->
        if (!hasKey(name, Constants.NBT.TAG_COMPOUND)) return@register null
        val nbt = getCompoundTag(name)
        val firstClass = nbt.getString("@first_type").takeIf { nbt.hasKey("@first_type") }?.let(::loadClass)
        val secondClass = nbt.getString("@second_type").takeIf { nbt.hasKey("@second_type") }?.let(::loadClass)
        Pair(
          firstClass?.let { load(nbt, it.kotlin, "@first")?.value },
          secondClass?.let { load(nbt, it.kotlin, "@second")?.value }
        )
      }
    )

    // ItemStack
    register(
      type = ItemStack::class,
      save = { name, type ->
        val nbt = NBTTagCompound()
        type.writeToNBT(nbt)
        setTag(name, nbt)
      },
      load = { name ->
        val nbt = getCompoundTag(name)
        ItemStack(nbt)
      }
    )

    // Serializable
    register(
      type = Serializable::class,
      save = { name, type ->
        val ostr = ByteArrayOutputStream()
        val obj = ObjectOutputStream(ostr)
        obj.writeObject(type)
        setString(name, ostr.toByteArray().toString(Charsets.UTF_8))
      },
      load = { name ->
        val istr = ByteArrayInputStream(getString(name).toByteArray(Charsets.UTF_8))
        val obj = ObjectInputStream(istr)
        obj.readObject() as Serializable
      },
      priority = -100
    )

    // Enum
    register(
      type = Enum::class,
      save = { name, type ->
        setString("@${name}_enum", type.javaClass.getType())
        setInteger(name, type.ordinal)
      },
      load = { name ->
        val enumClass = loadClass(getString("@${name}_enum"))
        enumClass?.enumConstants?.get(getInteger(name)) as? Enum<*>
      },
      priority = 1000
    )
  }

}