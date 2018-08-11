package therealfarfetchd.quacklib.item.data

import therealfarfetchd.quacklib.api.item.data.ItemDataPart

class StorageImpl(val part: ItemDataPart) : ItemDataPart.Storage {

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

fun ItemDataPart.Storage.get(name: String): Any? = (this as StorageImpl).get(name)

fun ItemDataPart.Storage.set(name: String, value: Any?): Any? = (this as StorageImpl).set(name, value)