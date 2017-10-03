package therealfarfetchd.quacklib.common.api.recipe

import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.common.api.extensions.makeStack

object AlloyFurnaceRecipes {
  private var recipesList: Set<Pair<List<ItemTemplate>, ItemTemplate>> = emptySet()

  init {
    // TODO: remove temp recipe
    addRecipe {
      // 3xredstone, 2xdyeCyan -> 1xstick
      inputs += stack(Items.REDSTONE, 3)
      inputs += oredict("dyeCyan", 2)

      output = stack(Items.STICK)
    }
  }

  fun addRecipe(op: AlloyRecipeTemplate.() -> Unit) {
    val t = AlloyRecipeTemplate().also(op)
    if (!t.isValid()) error("Invalid recipe.")
    recipesList += t.inputs to t.output
  }

  fun findRecipe(stacksIn: List<ItemStack>): Pair<List<ItemTemplate>, ItemTemplate>? {
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

    for (recipe in recipesList) {
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
        if (count > 0) return null
      }
      return recipe
    }
    return null
  }
}