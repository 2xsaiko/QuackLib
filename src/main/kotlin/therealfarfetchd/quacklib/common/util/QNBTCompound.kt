package therealfarfetchd.quacklib.common.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

/**
 * A wrapper around NBTTagCompound to make using NBT more concise.
 */
class QNBTCompound(val self: NBTTagCompound) {
  constructor() : this(NBTTagCompound())

  val empty: Boolean
    get() = self.hasNoTags()

  fun exists(s: String): Boolean = self.hasKey(s) || self.hasUniqueId(s)

  val bool = object : IView<String, Boolean> {
    override fun get(k: String): Boolean = self.getBoolean(k)
    override fun set(k: String, v: Boolean) = self.setBoolean(k, v)
  }

  val byte = object : IView<String, Byte> {
    override fun get(k: String): Byte = self.getByte(k)
    override fun set(k: String, v: Byte) = self.setByte(k, v)
  }

  val ubyte = object : IView<String, Int> {
    override fun get(k: String): Int = self.getByte(k).toInt() and 0xFF
    override fun set(k: String, v: Int) = self.setByte(k, v.toByte())
  }

  val short = object : IView<String, Short> {
    override fun get(k: String): Short = self.getShort(k)
    override fun set(k: String, v: Short) = self.setShort(k, v)
  }

  val ushort = object : IView<String, Int> {
    override fun get(k: String): Int = self.getShort(k).toInt() and 0xFFFF
    override fun set(k: String, v: Int) = self.setShort(k, v.toShort())
  }

  val char = object : IView<String, Char> {
    override fun get(k: String): Char = self.getShort(k).toChar()
    override fun set(k: String, v: Char) = self.setShort(k, v.toShort())
  }

  val int = object : IView<String, Int> {
    override fun get(k: String): Int = self.getInteger(k)
    override fun set(k: String, v: Int) = self.setInteger(k, v)
  }

  val long = object : IView<String, Long> {
    override fun get(k: String): Long = self.getLong(k)
    override fun set(k: String, v: Long) = self.setLong(k, v)
  }

  val float = object : IView<String, Float> {
    override fun get(k: String): Float = self.getFloat(k)
    override fun set(k: String, v: Float) = self.setFloat(k, v)
  }

  val double = object : IView<String, Double> {
    override fun get(k: String): Double = self.getDouble(k)
    override fun set(k: String, v: Double) = self.setDouble(k, v)
  }

  val string = object : IView<String, String> {
    override fun get(k: String): String = self.getString(k)
    override fun set(k: String, v: String) = self.setString(k, v)
  }

  val nbt = object : IView<String, QNBTCompound> {
    override fun get(k: String): QNBTCompound {
      if (exists(k)) return QNBTCompound(self.getCompoundTag(k))
      else return QNBTCompound().also { set(k, it) }
    }

    override fun set(k: String, v: QNBTCompound) = self.setTag(k, v.self)
  }

  val nbts = object : IView<String, List<QNBTCompound>> {
    override fun get(k: String): List<QNBTCompound> {
      val tag = self.getTag(k) as? NBTTagList ?: return emptyList()
      if (tag.tagType != 10) return emptyList()
      return (0 until tag.count()).map { QNBTCompound(tag.getCompoundTagAt(it)) }
    }

    override fun set(k: String, v: List<QNBTCompound>) {
      val tag = NBTTagList()
      v.forEach { tag.appendTag(it.self) }
      self.setTag(k, tag)
    }
  }

  val bytes = object : IView<String, ByteArray> {
    override fun get(k: String): ByteArray = self.getByteArray(k)
    override fun set(k: String, v: ByteArray) = self.setByteArray(k, v)
  }

  val ints = object : IView<String, IntArray> {
    override fun get(k: String): IntArray = self.getIntArray(k)
    override fun set(k: String, v: IntArray) = self.setIntArray(k, v)
  }

  val uuid = object : IView<String, UUID> {
    override fun get(k: String): UUID = self.getUniqueId(k)!!
    override fun set(k: String, v: UUID) = self.setUniqueId(k, v)
  }

  val item = object : IView<String, ItemStack> {
    override fun get(k: String): ItemStack = ItemStack(nbt[k].self)

    override fun set(k: String, v: ItemStack) {
      v.writeToNBT(nbt[k].self)
    }
  }

}