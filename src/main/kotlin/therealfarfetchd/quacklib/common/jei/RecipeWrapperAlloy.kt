package therealfarfetchd.quacklib.common.jei

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.common.api.recipe.AlloyRecipe

class RecipeWrapperAlloy(val recipe: AlloyRecipe) : IRecipeWrapper {
  override fun getIngredients(ingredients: IIngredients) {
    ingredients.setInputs(ItemStack::class.java, recipe.first.map { it.makeStack() })
    ingredients.setOutput(ItemStack::class.java, recipe.second.makeStack())
  }
}