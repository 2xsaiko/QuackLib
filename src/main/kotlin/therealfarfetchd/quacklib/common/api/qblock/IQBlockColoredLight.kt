package therealfarfetchd.quacklib.common.api.qblock

import com.elytradev.mirage.lighting.Light
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
interface IQBlockColoredLight {
  fun getColoredLight(): Light?
}