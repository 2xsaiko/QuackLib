package therealfarfetchd.quacklib.common.jei

import mezz.jei.api.gui.IDrawableAnimated
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.gui.GuiHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID

object RecipeCategoryAlloy : IRecipeCategory<RecipeWrapperAlloy> {
  private val outputSlot = 1
  private val inputSlot1 = 2

  private val guiHelper = GuiHelper()
  private val texture = ResourceLocation(ModID, "textures/gui/jei/alloy_furnace.png")
  private val uid = "$modName:alloy_recipe"
  private val title = I18n.format("recipe.$uid")

  private val background = guiHelper.createDrawable(texture, 0, 0, 139, 54, 256, 128)

  private val flame = guiHelper.createAnimatedDrawable(
    guiHelper.createDrawable(texture, 139, 0, 14, 14, 256, 128),
    300, IDrawableAnimated.StartDirection.TOP, true)

  private val arrow = guiHelper.createAnimatedDrawable(
    guiHelper.createDrawable(texture, 139, 14, 23, 16, 256, 128),
    200, IDrawableAnimated.StartDirection.LEFT, false)

  override fun getUid() = uid

  override fun getTitle() = title

  override fun getBackground() = background

  override fun getModName() = ModID

  override fun drawExtras(minecraft: Minecraft) {
    flame.draw(minecraft, 1, 2)
    arrow.draw(minecraft, 86, 19)
  }

  override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: RecipeWrapperAlloy, ingredients: IIngredients) {
    val guiItemStacks = recipeLayout.itemStacks

    guiItemStacks.init(outputSlot, false, 117, 18)

    for (j in 0 until 3) for (i in 0 until 3) {
      guiItemStacks.init(inputSlot1 + i + j * 3, true, 27 + i * 18, j * 18)
    }

    guiItemStacks.set(ingredients)
  }
}