package therealfarfetchd.quacklib.block.data

import therealfarfetchd.quacklib.api.block.data.BlockDataPart

class StorageImpl(val part: BlockDataPart) : BlockDataPart.Storage {

  val data: MutableMap<String, Any?> = part.defs.mapValues { it.value.default }.toMutableMap()

  fun get(name: String): Any? {
    return data[name]
  }

  fun set(name: String, value: Any?) {
    if (getDef(name).isValid(value)) {
      data[name] = value
    } else {
      error("Invalid value $value for property $name!")
    }
  }

  @Suppress("UNCHECKED_CAST")
  fun getDef(name: String) = part.defs.getValue(name)

}

fun BlockDataPart.Storage.get(name: String): Any? = (this as StorageImpl).get(name)

fun BlockDataPart.Storage.set(name: String, value: Any?): Any? = (this as StorageImpl).set(name, value)