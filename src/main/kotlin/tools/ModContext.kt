package therealfarfetchd.quacklib.tools

import com.google.common.collect.ListMultimap
import net.minecraftforge.fml.common.LoadController
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import therealfarfetchd.quacklib.api.tools.ModContext
import therealfarfetchd.quacklib.api.tools.access
import therealfarfetchd.quacklib.api.tools.accessDelegate

object ModContext : ModContext {

  private val theLoader: Loader = Loader.instance()
  private val loadController: LoadController = theLoader.access("modController")
  private val packageOwners: ListMultimap<String, ModContainer> = loadController.access("packageOwners")
  private var activeContainer: ModContainer? by loadController.accessDelegate("activeContainer")

  fun dissociate(pkg: String, recursive: Boolean = false) {
    packageOwners.removeAll(pkg)
    if (recursive)
      packageOwners.keys()
        .filter { it.startsWith("$pkg.") }
        .forEach { packageOwners.removeAll(it) }
  }

  fun associate(pkg: String, name: String) {
    require(Loader.isModLoaded(name), { "Mod '$name' does not exist!" })
    packageOwners.put(pkg, Loader.instance().indexedModList[name])
  }

  fun associate(pkg: String, mod: ModContainer) {
    packageOwners.put(pkg, mod)
  }

  fun <R> executeAsMod(name: String, op: () -> R): R {
    val oldContainer = activeContainer
    activeContainer = theLoader.indexedModList[name]!!
    try {
      return op()
    } finally {
      activeContainer = oldContainer
    }
  }

  override fun currentMod(): ModContainer? = Loader.instance().activeModContainer()

}