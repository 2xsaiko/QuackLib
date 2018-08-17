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

  // FIXME remove these and use executeAsMod, this is a horrible hack

  fun dissociate(pkg: String, recursive: Boolean = false, filter: (String) -> Boolean = { true }) {
    packageOwners.removeAll(pkg)
    if (recursive)
      packageOwners.keys()
        .filter { it.startsWith("$pkg.") && filter(it) }
        .forEach { packageOwners.removeAll(it) }
  }

  fun associate(pkg: String, name: String) {
    require(Loader.isModLoaded(name)) { "Mod '$name' does not exist!" }
    packageOwners.put(pkg, Loader.instance().indexedModList[name])
  }

  fun associate(pkg: String, mod: ModContainer) {
    packageOwners.put(pkg, mod)
  }

  override fun <R> lockMod(op: () -> R): R =
    executeAsMod(currentMod(), op)

  fun <R> executeAsMod(mod: ModContainer?, op: () -> R): R {
    val oldContainer = activeContainer
    activeContainer = mod
    try {
      return op()
    } finally {
      activeContainer = oldContainer
    }
  }

  fun <R> executeAsMod(name: String, op: () -> R): R =
    executeAsMod(theLoader.indexedModList[name], op)

  override fun currentMod(): ModContainer? = Loader.instance().activeModContainer()

}