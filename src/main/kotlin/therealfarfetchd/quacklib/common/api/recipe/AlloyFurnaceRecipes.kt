package therealfarfetchd.quacklib.common.api.recipe

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.extensions.makeStack

typealias AlloyRecipe = Pair<List<ItemTemplate>, ItemTemplate>

object AlloyFurnaceRecipes {
  var recipesList: Set<AlloyRecipe> = emptySet(); private set

  fun addRecipe(op: AlloyRecipeTemplate.() -> Unit) {
    val t = AlloyRecipeTemplate().also(op)
    if (!t.isValid()) QuackLib.Logger.error("Invalid alloy recipe (missing items?): [${t.inputs.joinToString(separator = ", ")}] -> ${t.output}")
    else recipesList += t.inputs to t.output
  }

  fun findRecipe(stacksIn: List<ItemStack>): AlloyRecipe? {
    val stacks: List<ItemStack> = {
      var m: Map<Pair<Item, Int>, Int> = emptyMap()
      stacksIn
        .filterNot { it.isEmpty }
        .forEach {
          val key = it.item to it.metadata
          m += key to it.count + (m[key] ?: 0)
        }
      m.map { it.key.first.makeStack(it.value, it.key.second) }
    }()

    main@ for (recipe in recipesList) {
      val st = stacks.map { it.copy() } // temp copy so we can remove items at will
      for (input in recipe.first) {
        var count = input.makeStack().count
        for (s in st) {
          if (count == 0) break
          if (!input.isSameItem(s)) continue
          val take = minOf(s.count, count)
          count -= take
          s.count -= take
        }
        if (count > 0) continue@main
      }
      return recipe
    }
    return null
  }
}