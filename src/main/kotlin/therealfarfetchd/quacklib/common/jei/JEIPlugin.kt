package therealfarfetchd.quacklib.common.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import therealfarfetchd.quacklib.client.gui.GuiAlloyFurnace
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.recipe.AlloyFurnaceRecipes
import therealfarfetchd.quacklib.common.block.BlockAlloyFurnace
import therealfarfetchd.quacklib.common.block.ContainerAlloyFurnace

@JEIPlugin
class JEIPlugin : IModPlugin {
  override fun registerCategories(registry: IRecipeCategoryRegistration) {
    registry.addRecipeCategories(RecipeCategoryAlloy)
  }

  override fun register(registry: IModRegistry) {
    registry.addRecipes(AlloyFurnaceRecipes.recipesList.map(::RecipeWrapperAlloy), RecipeCategoryAlloy.uid)
    registry.addRecipeCatalyst(BlockAlloyFurnace.Item.makeStack(), RecipeCategoryAlloy.uid)

    registry.recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloyFurnace::class.java, RecipeCategoryAlloy.uid, 2, 9, 11, 36)
    registry.addRecipeClickArea(GuiAlloyFurnace::class.java, 102, 34, 22, 16, RecipeCategoryAlloy.uid)
  }
}